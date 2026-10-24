package br.com.fiap.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConversaoPontos {

    private int id;
    private int pontosUtilizados;
    private BigDecimal valorCredito;
    private LocalDateTime dataConversao;
    private String status;
    private int idUsuario;

    public ConversaoPontos(int id, int pontosUtilizados, BigDecimal valorCredito,
                           LocalDateTime dataConversao, String status, int idUsuario) {
        this.id = id;
        this.pontosUtilizados = pontosUtilizados;
        this.valorCredito = valorCredito;
        this.dataConversao = dataConversao;
        this.status = status;
        this.idUsuario = idUsuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPontosUtilizados() { return pontosUtilizados; }
    public void setPontosUtilizados(int pontosUtilizados) { this.pontosUtilizados = pontosUtilizados; }

    public BigDecimal getValorCredito() { return valorCredito; }
    public void setValorCredito(BigDecimal valorCredito) { this.valorCredito = valorCredito; }

    public LocalDateTime getDataConversao() { return dataConversao; }
    public void setDataConversao(LocalDateTime dataConversao) { this.dataConversao = dataConversao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}