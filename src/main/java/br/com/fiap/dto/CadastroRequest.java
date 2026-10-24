package br.com.fiap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(min = 3, max = 100, message = "O nome deve ter de 3 a 100 caracteres.")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Size(max = 60, message = "O e-mail deve ter no máximo 60 caracteres.")
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Informe um e-mail válido.")
        String email,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "A senha deve conter letra e número.")
        String senha) {
}