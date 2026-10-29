package br.com.fiap.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

public final class PeriodoUtil {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private PeriodoUtil() {
    }

    public static LocalDateTime agora() {
        return LocalDateTime.now(FUSO);
    }

    // Identifica o período atual: '2026-10-29' (dia), '2026-W44' (semana), '2026-10' (mês) ou 'UNICA'
    public static String referencia(String periodo, LocalDateTime agora) {
        LocalDate dia = agora.toLocalDate();
        return switch (periodo) {
            case "DIARIA" -> dia.toString();
            case "SEMANAL" -> String.format("%d-W%02d",
                    dia.get(IsoFields.WEEK_BASED_YEAR), dia.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            case "MENSAL" -> String.format("%d-%02d", dia.getYear(), dia.getMonthValue());
            default -> "UNICA";
        };
    }

    // Momento em que o período atual termina e a missão reinicia (null para missão única)
    public static LocalDateTime reiniciaEm(String periodo, LocalDateTime agora) {
        LocalDate dia = agora.toLocalDate();
        return switch (periodo) {
            case "DIARIA" -> dia.plusDays(1).atStartOfDay();
            case "SEMANAL" -> dia.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();
            case "MENSAL" -> dia.with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay();
            default -> null;
        };
    }
}