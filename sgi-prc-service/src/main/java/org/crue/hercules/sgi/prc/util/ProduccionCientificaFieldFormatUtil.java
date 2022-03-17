package org.crue.hercules.sgi.prc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ProduccionCientificaFieldFormatUtil {

  public static final String PATTERN_MONTH_DAY_01_01 = "-01-01";

  public static String formatDate(String value, TimeZone timeZone) {
    if (value.matches("^(\\d{4})-(\\d{2})-(\\d{2})$")) {
      return formatDateStringISO(value, timeZone);
    } else {
      return value;
    }
  }

  public static String formatDateStringISO(String value, TimeZone timeZone) {
    String result = "";
    if (StringUtils.hasText(value)) {
      try {

        Instant instant = LocalDate.parse(value).atStartOfDay(timeZone.toZoneId()).toInstant();

        DateTimeFormatter dfDateTimeOut = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
        result = dfDateTimeOut.format(instant);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    return result;
  }

  public static String formatNumber(String value) {
    String valorParse = StringUtils.hasText(value) ? value : "0";
    BigDecimal bd = new BigDecimal(valorParse).setScale(2, RoundingMode.HALF_UP);
    DecimalFormat df = new DecimalFormat("000000000000000.00");
    return df.format(bd.doubleValue());
  }

  public static String normalizeString(String value) {
    String stringNormalize = Normalizer.normalize(value, Normalizer.Form.NFKD);
    return stringNormalize.replaceAll("[^a-z,^A-Z,^0-9]", "");
  }

  public static Instant calculateFechaFinBaremacionByAnio(Integer anio, TimeZone timeZone) {
    String strFechaInicioBaremacion = (anio + 1) + PATTERN_MONTH_DAY_01_01;

    return LocalDate.parse(strFechaInicioBaremacion)
        .atStartOfDay(timeZone.toZoneId()).toInstant().minusSeconds(1);
  }

  public static Pair<Instant, Instant> calculateFechasInicioFinBaremacionByAnio(Integer anio, TimeZone timeZone) {
    String strFechaInicioBaremacion = anio + "-01-01";

    Instant fechaInicioBaremacion = LocalDate.parse(strFechaInicioBaremacion)
        .atStartOfDay(timeZone.toZoneId()).toInstant();

    Instant fechaFinBaremacion = calculateFechaFinBaremacionByAnio(anio, timeZone);

    return Pair.of(fechaInicioBaremacion, fechaFinBaremacion);
  }

  public static String formatInstantToStringWithTimeZone(Instant fecha, TimeZone timeZone) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withZone(timeZone.toZoneId()).withLocale(LocaleContextHolder.getLocale());
    return formatter.format(fecha);
  }

  public static BaremacionInput createBaremacionInput(int anio, Long convocatoriaBaremacionId, TimeZone timeZone) {

    Pair<Instant, Instant> pairFechas = calculateFechasInicioFinBaremacionByAnio(anio, timeZone);

    return BaremacionInput.builder()
        .anio(anio)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .fechaInicio(formatInstantToStringWithTimeZone(pairFechas.getFirst(), timeZone))
        .fechaFin(
            formatInstantToStringWithTimeZone(pairFechas.getSecond(), timeZone))
        .build();
  }

}
