// SimulacaoResponse.java
package br.com.fiap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoResponse(int pontos, BigDecimal valorCredito, LocalDateTime dataValidade,
                                int pontosMinimos, int pontosPorReal) {
}