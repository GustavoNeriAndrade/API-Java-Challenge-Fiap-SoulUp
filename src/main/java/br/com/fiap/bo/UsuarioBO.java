package br.com.fiap.bo;

import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dto.CadastroRequest;
import br.com.fiap.dto.LoginRequest;
import br.com.fiap.exception.RegraNegocioException;
import br.com.fiap.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioBO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(CadastroRequest req) throws SQLException {
        String email = req.email().trim().toLowerCase();

        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new RegraNegocioException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario(0, req.nome().trim(), email,
                encoder.encode(req.senha()), 0, LocalDateTime.now());
        usuarioDAO.inserir(usuario);
        return usuario;
    }

    public Usuario autenticar(LoginRequest req) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorEmail(req.email().trim().toLowerCase());

        // Mesma mensagem para e-mail inexistente e senha errada (como o front já faz)
        if (usuario == null || !encoder.matches(req.senha(), usuario.getSenha())) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }
        return usuario;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new RegraNegocioException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado.");
        }
        return usuario;
    }
}