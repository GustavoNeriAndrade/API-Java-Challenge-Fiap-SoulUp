package br.com.fiap.model;

import java.time.LocalDateTime;

public class Contato {

    private int id;
    private String nome;
    private String email;
    private String mensagem;
    private LocalDateTime dataEnvio;

    public Contato(int id, String nome, String email, String mensagem, LocalDateTime dataEnvio) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.mensagem = mensagem;
        this.dataEnvio = dataEnvio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}