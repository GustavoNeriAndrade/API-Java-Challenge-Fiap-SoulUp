// ConversaoResponse.java
package br.com.fiap.dto;

import br.com.fiap.model.ConversaoPontos;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConversaoResponse(int idConversao, int pontosUtilizados, BigDecimal valorCredito,
                                LocalDateTime dataConversao, String status, int idUsuario) {

    public static ConversaoResponse from(ConversaoPontos c) {
        return new ConversaoResponse(c.getId(), c.getPontosUtilizados(), c.getValorCredito(),
                c.getDataConversao(), c.getStatus(), c.getIdUsuario());
    }
}