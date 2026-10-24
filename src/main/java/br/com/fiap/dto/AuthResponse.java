package br.com.fiap.dto;

public record AuthResponse(String token, UsuarioResponse usuario) {
}