package br.com.fiap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContatoRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(min = 2, max = 100, message = "O nome deve ter de 2 a 100 caracteres.")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Size(max = 60, message = "O e-mail deve ter no máximo 60 caracteres.")
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Informe um e-mail válido.")
        String email,

        @NotBlank(message = "Escreva uma mensagem.")
        @Size(min = 10, max = 1000, message = "A mensagem deve ter de 10 a 1000 caracteres.")
        String mensagem) {
}