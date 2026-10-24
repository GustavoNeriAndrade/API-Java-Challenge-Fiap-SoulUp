// VoucherResponse.java
package br.com.fiap.dto;

import br.com.fiap.model.Voucher;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherResponse(int idVoucher, String codigoVoucher, BigDecimal valorCreditoVoucher,
                              LocalDateTime dataEmissao, LocalDateTime dataValidade,
                              String statusVoucher, String nomeOperadorTransporte, int idConversao) {

    public static VoucherResponse from(Voucher v) {
        // GERADO com validade vencida é exibido como EXPIRADO
        String status = ("GERADO".equals(v.getStatus()) && v.getDataValidade().isBefore(LocalDateTime.now()))
                ? "EXPIRADO" : v.getStatus();

        return new VoucherResponse(v.getId(), v.getCodigo(), v.getValorCredito(), v.getDataEmissao(),
                v.getDataValidade(), status, v.getOperador(), v.getIdConversao());
    }
}