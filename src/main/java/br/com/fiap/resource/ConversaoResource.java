package br.com.fiap.resource;

import br.com.fiap.bo.ConversaoBO;
import br.com.fiap.dto.ConversaoRequest;
import br.com.fiap.dto.ConversaoResultadoResponse;
import br.com.fiap.dto.HistoricoConversaoResponse;
import br.com.fiap.dto.SimulacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/conversoes")
public class ConversaoResource {

    private final ConversaoBO conversaoBO;

    public ConversaoResource(ConversaoBO conversaoBO) {
        this.conversaoBO = conversaoBO;
    }

    @GetMapping
    public List<HistoricoConversaoResponse> historico(@RequestAttribute("idUsuario") int idUsuario)
            throws SQLException {
        return conversaoBO.historico(idUsuario);
    }

    @GetMapping("/simulacao")
    public SimulacaoResponse simular(@RequestParam int pontos) {
        return conversaoBO.simular(pontos);
    }

    @PostMapping
    public ResponseEntity<ConversaoResultadoResponse> converter(
            @Valid @RequestBody ConversaoRequest req,
            @RequestAttribute("idUsuario") int idUsuario) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversaoBO.converter(idUsuario, req));
    }
}