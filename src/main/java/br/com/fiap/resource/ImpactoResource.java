package br.com.fiap.resource;

import br.com.fiap.bo.ImpactoBO;
import br.com.fiap.dto.ImpactoRequest;
import br.com.fiap.dto.ImpactoResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/impactos")
public class ImpactoResource {

    private final ImpactoBO impactoBO;

    public ImpactoResource(ImpactoBO impactoBO) {
        this.impactoBO = impactoBO;
    }

    @GetMapping
    public List<ImpactoResponse> listar(@RequestAttribute("idUsuario") int idUsuario) throws SQLException {
        return impactoBO.listar(idUsuario).stream().map(ImpactoResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<ImpactoResponse> registrar(@Valid @RequestBody ImpactoRequest req,
                                                     @RequestAttribute("idUsuario") int idUsuario)
            throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImpactoResponse.from(impactoBO.registrar(idUsuario, req.km())));
    }
}