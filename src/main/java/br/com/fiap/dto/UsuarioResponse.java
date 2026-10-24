package br.com.fiap.dto;

import br.com.fiap.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(int idUsuario, String nome, String email,
                              int pontos, LocalDateTime dataCadastro) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(),
                u.getPontos(), u.getDataCadastro());
    }
}
