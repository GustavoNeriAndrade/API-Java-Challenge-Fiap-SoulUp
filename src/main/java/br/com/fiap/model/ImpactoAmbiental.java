package br.com.fiap.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ImpactoAmbiental {

    private int id;
    private BigDecimal kmPercorridos;
    private BigDecimal co2Economizado;
    private BigDecimal arvoresEquivalentes;
    private LocalDateTime dataRegistro;
    private int idUsuario;

    public ImpactoAmbiental(int id, BigDecimal kmPercorridos, BigDecimal co2Economizado,
                            BigDecimal arvoresEquivalentes, LocalDateTime dataRegistro, int idUsuario) {
        this.id = id;
        this.kmPercorridos = kmPercorridos;
        this.co2Economizado = co2Economizado;
        this.arvoresEquivalentes = arvoresEquivalentes;
        this.dataRegistro = dataRegistro;
        this.idUsuario = idUsuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getKmPercorridos() { return kmPercorridos; }
    public void setKmPercorridos(BigDecimal kmPercorridos) { this.kmPercorridos = kmPercorridos; }

    public BigDecimal getCo2Economizado() { return co2Economizado; }
    public void setCo2Economizado(BigDecimal co2Economizado) { this.co2Economizado = co2Economizado; }

    public BigDecimal getArvoresEquivalentes() { return arvoresEquivalentes; }
    public void setArvoresEquivalentes(BigDecimal arvoresEquivalentes) { this.arvoresEquivalentes = arvoresEquivalentes; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}