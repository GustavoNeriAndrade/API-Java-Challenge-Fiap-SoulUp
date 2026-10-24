package br.com.fiap.bo;

import br.com.fiap.dao.ImpactAmbientalDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.ImpactoAmbiental;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImpactoBO {

    private static final BigDecimal KG_CO2_POR_KM = new BigDecimal("0.17");
    private static final BigDecimal KG_CO2_POR_ARVORE = new BigDecimal("21");

    private final ImpactAmbientalDAO impactoDAO;
    private final UsuarioDAO usuarioDAO;

    public ImpactoBO(ImpactAmbientalDAO impactoDAO, UsuarioDAO usuarioDAO) {
        this.impactoDAO = impactoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public ImpactoAmbiental registrar(int idUsuario, BigDecimal kmInformado) throws SQLException {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado.");
        }

        BigDecimal km = kmInformado.setScale(2, RoundingMode.HALF_UP);
        BigDecimal co2 = km.multiply(KG_CO2_POR_KM).setScale(4, RoundingMode.HALF_UP);
        BigDecimal arvores = co2.divide(KG_CO2_POR_ARVORE, 2, RoundingMode.HALF_UP);

        ImpactoAmbiental impacto = new ImpactoAmbiental(0, km, co2, arvores, LocalDateTime.now(), idUsuario);
        impactoDAO.inserir(impacto);
        return impacto;
    }

    public List<ImpactoAmbiental> listar(int idUsuario) throws SQLException {
        return impactoDAO.listarPorUsuario(idUsuario);
    }
}