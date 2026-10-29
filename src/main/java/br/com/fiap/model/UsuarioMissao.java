package br.com.fiap.model;

import java.time.LocalDateTime;

public class UsuarioMissao {

    private int id;
    private int idUsuario;
    private int idMissao;
    private LocalDateTime dataConclusao;
    private int pontosGanhos;
    private String periodoRef;

    public UsuarioMissao(int id, int idUsuario, int idMissao, LocalDateTime dataConclusao,
                         int pontosGanhos, String periodoRef) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idMissao = idMissao;
        this.dataConclusao = dataConclusao;
        this.pontosGanhos = pontosGanhos;
        this.periodoRef = periodoRef;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdMissao() { return idMissao; }
    public void setIdMissao(int idMissao) { this.idMissao = idMissao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public int getPontosGanhos() { return pontosGanhos; }
    public void setPontosGanhos(int pontosGanhos) { this.pontosGanhos = pontosGanhos; }

    public String getPeriodoRef() { return periodoRef; }
    public void setPeriodoRef(String periodoRef) { this.periodoRef = periodoRef; }
}