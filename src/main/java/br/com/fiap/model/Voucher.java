package br.com.fiap.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Voucher {

    private int id;
    private String codigo;
    private BigDecimal valorCredito;
    private LocalDateTime dataEmissao;
    private LocalDateTime dataValidade;
    private String status;
    private String operador;
    private int idConversao;

    public Voucher(int id, String codigo, BigDecimal valorCredito, LocalDateTime dataEmissao,
                   LocalDateTime dataValidade, String status, String operador, int idConversao) {
        this.id = id;
        this.codigo = codigo;
        this.valorCredito = valorCredito;
        this.dataEmissao = dataEmissao;
        this.dataValidade = dataValidade;
        this.status = status;
        this.operador = operador;
        this.idConversao = idConversao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public BigDecimal getValorCredito() { return valorCredito; }
    public void setValorCredito(BigDecimal valorCredito) { this.valorCredito = valorCredito; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public int getIdConversao() { return idConversao; }
    public void setIdConversao(int idConversao) { this.idConversao = idConversao; }
}