package br.com.fiap.resource;

import br.com.fiap.bo.UsuarioBO;
import br.com.fiap.dto.AuthResponse;
import br.com.fiap.dto.CadastroRequest;
import br.com.fiap.dto.LoginRequest;
import br.com.fiap.dto.UsuarioResponse;
import br.com.fiap.model.Usuario;
import br.com.fiap.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final UsuarioBO usuarioBO;
    private final TokenService tokenService;

    public AuthResource(UsuarioBO usuarioBO, TokenService tokenService) {
        this.usuarioBO = usuarioBO;
        this.tokenService = tokenService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroRequest req)
            throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(montarResposta(usuarioBO.cadastrar(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req)
            throws SQLException {
        return ResponseEntity.ok(montarResposta(usuarioBO.autenticar(req)));
    }

    @GetMapping("/me")
    public UsuarioResponse me(@RequestAttribute("idUsuario") int idUsuario) throws SQLException {
        return UsuarioResponse.from(usuarioBO.buscarPorId(idUsuario));
    }

    private AuthResponse montarResposta(Usuario usuario) {
        return new AuthResponse(tokenService.gerar(usuario.getId()), UsuarioResponse.from(usuario));
    }
}