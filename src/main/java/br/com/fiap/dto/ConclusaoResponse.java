// dto/ConclusaoResponse.java
package br.com.fiap.dto;

import java.time.LocalDateTime;

public record ConclusaoResponse(int idMissao, LocalDateTime dataConclusao,
                                int pontosGanhos, int saldoPontos, LocalDateTime reiniciaEm) {
}