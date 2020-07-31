package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.data.sort.SortOperation;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

/**
 * A converter converts a source object of type {@code String} to a target of
 * type {@code List<SortCriteria>}.
 */
@Slf4j
public class SortCriteriaConverter implements Converter<String, List<SortCriteria>> {
  private static String wordRegex = "[A-Za-z0-9_ñÑ\\.]*";
  private static String operatorRegex = "(\\+|-)";
  private static String fullRegex = "(" + wordRegex + ")" + operatorRegex + ",";
  private static final Pattern sortPattern = Pattern.compile(fullRegex);

  /**
   * Convert the source object of type {@code String} to target type
   * {@code List<SortCriteria>}.
   * 
   * @param source the source object to convert, which must be an instance of
   *               {@code String} (never {@code null})
   * @return the converted object, which must be an instance of
   *         {@code List<SortCriteria>} (potentially {@code null})
   * @throws IllegalArgumentException if the source cannot be converted to the
   *                                  desired target type
   */
  @Override
  public List<SortCriteria> convert(String source) {
    log.debug("convert(String source) - start");
    List<SortCriteria> sortCriterias = new ArrayList<>();
    if (source != null) {
      Matcher matcher = sortPattern.matcher(source.replace(' ', '+') + ",");
      while (matcher.find()) {
        SortCriteria sortCriteria = new SortCriteria();
        sortCriteria.setKey(matcher.group(1));
        sortCriteria.setOperation(SortOperation.fromString(matcher.group(2)));
        sortCriterias.add(sortCriteria);
      }
    }
    log.debug("convert(String source) - end");
    return sortCriterias;
  }

}