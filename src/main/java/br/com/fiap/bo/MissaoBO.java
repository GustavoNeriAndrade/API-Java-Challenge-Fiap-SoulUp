package br.com.fiap.bo;

import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.UsuarioMissaoDAO;
import br.com.fiap.dto.ConclusaoResponse;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.Missao;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.UsuarioMissao;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MissaoBO {

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

    public List<UsuarioMissao> listarConcluidas(int idUsuario) throws SQLException {
        return usuarioMissaoDAO.listarPorUsuario(idUsuario);
    }

    public ConclusaoResponse concluir(int idUsuario, int idMissao) throws SQLException {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado.");
        }

        Missao missao = buscarPorId(idMissao);

        if (!missao.isAtiva()) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Esta missão não está ativa no momento.");
        }
        if (usuarioMissaoDAO.jaConcluiu(idUsuario, idMissao)) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Você já concluiu esta missão.");
        }

        UsuarioMissao conclusao = new UsuarioMissao(idUsuario, idMissao,
                LocalDateTime.now(), missao.getPontosRecompensa());

        try {
            usuarioMissaoDAO.concluir(conclusao);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: duas conclusões ao mesmo tempo
                throw new RegraNegocioException(HttpStatus.CONFLICT, "Você já concluiu esta missão.");
            }
            throw e;
        }

        Usuario atualizado = usuarioDAO.buscarPorId(idUsuario);
        return new ConclusaoResponse(idMissao, conclusao.getDataConclusao(),
                conclusao.getPontosGanhos(), atualizado.getPontos());
    }
}