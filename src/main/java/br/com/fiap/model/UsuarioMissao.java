package br.com.fiap.model;

import java.time.LocalDateTime;

public class UsuarioMissao {

    private int idUsuario;
    private int idMissao;
    private LocalDateTime dataConclusao;
    private int pontosGanhos;

    public UsuarioMissao(int idUsuario, int idMissao, LocalDateTime dataConclusao, int pontosGanhos) {
        this.idUsuario = idUsuario;
        this.idMissao = idMissao;
        this.dataConclusao = dataConclusao;
        this.pontosGanhos = pontosGanhos;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdMissao() { return idMissao; }
    public void setIdMissao(int idMissao) { this.idMissao = idMissao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public int getPontosGanhos() { return pontosGanhos; }
    public void setPontosGanhos(int pontosGanhos) { this.pontosGanhos = pontosGanhos; }
}
