package br.com.fiap.bo;

import br.com.fiap.dao.ConversaoPontosDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.VoucherDAO;
import br.com.fiap.dto.ConversaoRequest;
import br.com.fiap.dto.ConversaoResponse;
import br.com.fiap.dto.ConversaoResultadoResponse;
import br.com.fiap.dto.HistoricoConversaoResponse;
import br.com.fiap.dto.SimulacaoResponse;
import br.com.fiap.dto.VoucherResponse;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.ConversaoPontos;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.Voucher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConversaoBO {

    public static final int PONTOS_MINIMOS = 100;
    public static final int PONTOS_POR_REAL = 100;
    private static final int VALIDADE_MESES = 6;
    private static final List<String> OPERADORES = List.of("SPTrans", "Metro SP", "EMTU", "CPTM", "Outro");

    private final ConversaoPontosDAO conversaoDAO;
    private final VoucherDAO voucherDAO;
    private final UsuarioDAO usuarioDAO;

    public ConversaoBO(ConversaoPontosDAO conversaoDAO, VoucherDAO voucherDAO, UsuarioDAO usuarioDAO) {
        this.conversaoDAO = conversaoDAO;
        this.voucherDAO = voucherDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public SimulacaoResponse simular(int pontos) {
        validarPontos(pontos);
        return new SimulacaoResponse(pontos, calcularValor(pontos),
                LocalDateTime.now().plusMonths(VALIDADE_MESES), PONTOS_MINIMOS, PONTOS_POR_REAL);
    }

    public ConversaoResultadoResponse converter(int idUsuario, ConversaoRequest req) throws SQLException {
        int pontos = req.pontos();
        validarPontos(pontos);

        if (!OPERADORES.contains(req.operador())) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST, "Operadora de transporte inválida.");
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado.");
        }
        if (usuario.getPontos() < pontos) {
            throw saldoInsuficiente();
        }

        LocalDateTime agora = LocalDateTime.now();
        BigDecimal valor = calcularValor(pontos);
        String codigo = String.format("VCH-%d-%04d", agora.getYear(), voucherDAO.proximoSequencial());

        ConversaoPontos conversao = new ConversaoPontos(0, pontos, valor, agora, "APROVADO", idUsuario);
        Voucher voucher = new Voucher(0, codigo, valor, agora, agora.plusMonths(VALIDADE_MESES),
                "GERADO", req.operador(), 0);

        // O DAO também confere o saldo no próprio UPDATE (protege contra cliques simultâneos)
        if (!conversaoDAO.converter(conversao, voucher)) {
            throw saldoInsuficiente();
        }

        int saldo = usuarioDAO.buscarPorId(idUsuario).getPontos();
        return new ConversaoResultadoResponse(ConversaoResponse.from(conversao),
                VoucherResponse.from(voucher), saldo);
    }

    public List<HistoricoConversaoResponse> historico(int idUsuario) throws SQLException {
        Map<Integer, Voucher> vouchersPorConversao = voucherDAO.listarPorUsuario(idUsuario).stream()
                .collect(Collectors.toMap(Voucher::getIdConversao, Function.identity()));

        return conversaoDAO.listarPorUsuario(idUsuario).stream()
                .map(c -> {
                    Voucher v = vouchersPorConversao.get(c.getId());
                    return new HistoricoConversaoResponse(ConversaoResponse.from(c),
                            v == null ? null : VoucherResponse.from(v));
                })
                .toList();
    }

    private void validarPontos(int pontos) {
        if (pontos < PONTOS_MINIMOS) {
            throw new RegraNegocioException(HttpStatus.BAD_REQUEST,
                    "Conversão mínima: " + PONTOS_MINIMOS + " pontos.");
        }
    }

    private BigDecimal calcularValor(int pontos) {
        return BigDecimal.valueOf(pontos).divide(BigDecimal.valueOf(PONTOS_POR_REAL), 2, RoundingMode.HALF_UP);
    }

    private RegraNegocioException saldoInsuficiente() {
        return new RegraNegocioException(HttpStatus.CONFLICT, "Saldo de pontos insuficiente.");
    }
}