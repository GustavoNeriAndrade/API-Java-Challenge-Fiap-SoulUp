// ConversaoRequest.java
package br.com.fiap.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversaoRequest(
        @NotNull(message = "Informe a quantidade de pontos.")
        @Min(value = 100, message = "Conversão mínima: 100 pontos.")
        Integer pontos,

        @NotBlank(message = "Selecione a operadora de transporte.")
        String operador) {
}