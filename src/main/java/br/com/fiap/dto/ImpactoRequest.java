// dto/ImpactoRequest.java
package br.com.fiap.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ImpactoRequest(
        @NotNull(message = "Informe a distância percorrida.")
        @DecimalMin(value = "0.01", message = "Informe uma distância maior que zero.")
        @DecimalMax(value = "99999999.99", message = "A distância máxima é de 99.999.999 km.")
        BigDecimal km) {
}