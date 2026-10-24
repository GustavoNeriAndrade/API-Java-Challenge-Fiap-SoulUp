// ConversaoResultadoResponse.java
package br.com.fiap.dto;

public record ConversaoResultadoResponse(ConversaoResponse conversao, VoucherResponse voucher,
                                         int saldoPontos) {
}