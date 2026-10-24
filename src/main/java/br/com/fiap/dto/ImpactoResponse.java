// dto/ImpactoResponse.java
package br.com.fiap.dto;

import br.com.fiap.model.ImpactoAmbiental;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ImpactoResponse(int idImpacto, BigDecimal kmPercorridos, BigDecimal co2Economizado,
                              BigDecimal arvoresEquivalentes, LocalDateTime dataRegistro, int idUsuario) {

    public static ImpactoResponse from(ImpactoAmbiental i) {
        return new ImpactoResponse(i.getId(), i.getKmPercorridos(), i.getCo2Economizado(),
                i.getArvoresEquivalentes(), i.getDataRegistro(), i.getIdUsuario());
    }
}