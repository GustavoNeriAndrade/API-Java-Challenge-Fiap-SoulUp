package br.com.fiap.dto;

import br.com.fiap.model.Contato;
import java.time.LocalDateTime;

public record ContatoResponse(int idContato, LocalDateTime dataEnvio) {

    public static ContatoResponse from(Contato c) {
        return new ContatoResponse(c.getId(), c.getDataEnvio());
    }
}