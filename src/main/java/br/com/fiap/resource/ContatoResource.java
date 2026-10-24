package br.com.fiap.resource;

import br.com.fiap.bo.ContatoBO;
import br.com.fiap.dto.ContatoRequest;
import br.com.fiap.dto.ContatoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/contatos")
public class ContatoResource {

    private final ContatoBO contatoBO;

    public ContatoResource(ContatoBO contatoBO) {
        this.contatoBO = contatoBO;
    }

    @PostMapping
    public ResponseEntity<ContatoResponse> enviar(@Valid @RequestBody ContatoRequest req)
            throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ContatoResponse.from(contatoBO.registrar(req)));
    }
}