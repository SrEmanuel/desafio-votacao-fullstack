package dev.emanuelm.votacao.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class DataUtils {

  // Formatter para o padrão ISO_INSTANT (ex: 2025-06-07T11:31:00Z)
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

  /**
   * Converte um LocalDateTime para uma String no formato UTC (ISO-8601).
   * Assume que o LocalDateTime de entrada está em UTC.
   *
   * @param localDateTime O LocalDateTime a ser convertido.
   * @return A representação em String no formato "yyyy-MM-dd'T'HH:mm:ss'Z'".
   */
  public static String fromLocalDateTime(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    return localDateTime.atOffset(ZoneOffset.of("-03:00")).format(FORMATTER);
  }

  /**
   * Converte uma String no formato UTC (ISO-8601) para um LocalDateTime.
   * A informação de fuso horário da string é usada para o parse, mas o
   * objeto LocalDateTime resultante não a conterá.
   *
   * @param isoString A String a ser convertida (ex: "2025-06-07T11:31:00Z")
   * @return O objeto LocalDateTime correspondente.
   */
  public static LocalDateTime toLocalDateTime(String isoString) {
    if (isoString == null || isoString.isBlank()) {
      return null;
    }
    OffsetDateTime odt = OffsetDateTime.parse(isoString);
    return odt.toLocalDateTime();
  }

}
