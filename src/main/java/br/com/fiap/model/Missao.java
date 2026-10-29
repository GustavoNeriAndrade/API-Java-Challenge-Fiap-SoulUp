package br.com.fiap.model;

import java.util.List;

public class Missao {

    private int id;
    private String titulo;
    private String descricao;
    private int pontosRecompensa;
    private String categoria;
    private boolean ativa;
    private List<String> dicas;
    private String periodo;

    public Missao(int id, String titulo, String descricao, int pontosRecompensa,
                  String categoria, boolean ativa, List<String> dicas, String periodo) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.pontosRecompensa = pontosRecompensa;
        this.categoria = categoria;
        this.ativa = ativa;
        this.dicas = dicas;
        this.periodo = periodo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getPontosRecompensa() { return pontosRecompensa; }
    public void setPontosRecompensa(int pontosRecompensa) { this.pontosRecompensa = pontosRecompensa; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public List<String> getDicas() { return dicas; }
    public void setDicas(List<String> dicas) { this.dicas = dicas; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
}