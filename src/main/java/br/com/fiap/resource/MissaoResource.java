package br.com.fiap.resource;

import br.com.fiap.bo.MissaoBO;
import br.com.fiap.dto.ConclusaoResponse;
import br.com.fiap.dto.MissaoResponse;
import br.com.fiap.dto.UsuarioMissaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/missoes")
public class MissaoResource {

    private final MissaoBO missaoBO;

    public MissaoResource(MissaoBO missaoBO) {
        this.missaoBO = missaoBO;
    }

    @GetMapping
    public List<MissaoResponse> listar() throws SQLException {
        return missaoBO.listar().stream().map(MissaoResponse::from).toList();
    }

    @GetMapping("/concluidas")
    public List<UsuarioMissaoResponse> concluidas(@RequestAttribute("idUsuario") int idUsuario)
            throws SQLException {
        return missaoBO.listarConcluidas(idUsuario).stream()
                .map(UsuarioMissaoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MissaoResponse buscar(@PathVariable int id) throws SQLException {
        return MissaoResponse.from(missaoBO.buscarPorId(id));
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<ConclusaoResponse> concluir(@PathVariable int id,
                                                      @RequestAttribute("idUsuario") int idUsuario)
            throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(missaoBO.concluir(idUsuario, id));
    }
}