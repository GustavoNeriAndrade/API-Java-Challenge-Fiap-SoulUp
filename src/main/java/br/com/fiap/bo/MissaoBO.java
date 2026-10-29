package br.com.fiap.bo;

import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.UsuarioMissaoDAO;
import br.com.fiap.dto.ConclusaoResponse;
import br.com.fiap.dto.UsuarioMissaoResponse;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.Missao;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.UsuarioMissao;
import br.com.fiap.util.PeriodoUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MissaoBO {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");

    private final MissaoDAO missaoDAO;
    private final UsuarioMissaoDAO usuarioMissaoDAO;
    private final UsuarioDAO usuarioDAO;

    public MissaoBO(MissaoDAO missaoDAO, UsuarioMissaoDAO usuarioMissaoDAO, UsuarioDAO usuarioDAO) {
        this.missaoDAO = missaoDAO;
        this.usuarioMissaoDAO = usuarioMissaoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public List<Missao> listar() throws SQLException {
        return missaoDAO.listarTodas();
    }

    public Missao buscarPorId(int id) throws SQLException {
        Missao missao = missaoDAO.buscarPorId(id);
        if (missao == null) {
            throw new RegraNegocioException(HttpStatus.NOT_FOUND, "Missão não encontrada.");
        }
        return missao;
    }

    // Histórico de conclusões; periodoAtual indica se a conclusão vale para o período em andamento
    public List<UsuarioMissaoResponse> listarConcluidas(int idUsuario) throws SQLException {
        LocalDateTime agora = PeriodoUtil.agora();
        Map<Integer, Missao> missoes = missaoDAO.listarTodas().stream()
                .collect(Collectors.toMap(Missao::getId, Function.identity()));

        return usuarioMissaoDAO.listarPorUsuario(idUsuario).stream()
                .map(um -> {
                    Missao missao = missoes.get(um.getIdMissao());
                    boolean periodoAtual = missao != null
                            && PeriodoUtil.referencia(missao.getPeriodo(), agora).equals(um.getPeriodoRef());
                    return UsuarioMissaoResponse.from(um, periodoAtual);
                })
                .toList();
    }

    public ConclusaoResponse concluir(int idUsuario, int idMissao) throws SQLException {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado.");
        }

        Missao missao = buscarPorId(idMissao);
        if (!missao.isAtiva()) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Esta missão não está ativa no momento.");
        }

        LocalDateTime agora = PeriodoUtil.agora();
        String periodoRef = PeriodoUtil.referencia(missao.getPeriodo(), agora);
        LocalDateTime reiniciaEm = PeriodoUtil.reiniciaEm(missao.getPeriodo(), agora);

        if (usuarioMissaoDAO.jaConcluiu(idUsuario, idMissao, periodoRef)) {
            throw jaConcluida(reiniciaEm);
        }

        UsuarioMissao conclusao = new UsuarioMissao(0, idUsuario, idMissao, agora,
                missao.getPontosRecompensa(), periodoRef);

        try {
            usuarioMissaoDAO.concluir(conclusao);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: duas conclusões ao mesmo tempo
                throw jaConcluida(reiniciaEm);
            }
            throw e;
        }

        Usuario atualizado = usuarioDAO.buscarPorId(idUsuario);
        return new ConclusaoResponse(idMissao, agora, conclusao.getPontosGanhos(),
                atualizado.getPontos(), reiniciaEm);
    }

    private RegraNegocioException jaConcluida(LocalDateTime reiniciaEm) {
        String mensagem = "Você já concluiu esta missão.";
        if (reiniciaEm != null) {
            mensagem += " Ela reinicia em " + reiniciaEm.format(FORMATO) + ".";
        }
        return new RegraNegocioException(HttpStatus.CONFLICT, mensagem);
    }
}