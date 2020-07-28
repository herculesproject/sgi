package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.data.search.QueryOperation;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

/**
 * A converter converts a source object of type {@code String} to a target of
 * type {@code List<QueryCriteria>}.
 */
@Slf4j
public class QueryCriteriaConverter implements Converter<String, List<QueryCriteria>> {
  private static String wordRegex = "[A-Za-z0-9_ñÑ\\.]*";
  private static String valueRegex = "[A-Za-z0-9_áéíóúäëïöüÁÉÍÓÚÄËÏÖÜñÑ\\-\\.%\\s:]+";
  private static String operatorRegex = "(!?:|!?~|<:?|>:?)";
  private static String fullRegex = "(" + wordRegex + ")" + operatorRegex + "(" + valueRegex + ")?,";
  private static final Pattern searchPattern = Pattern.compile(fullRegex);

  /**
   * Convert the source object of type {@code String} to target type
   * {@code List<QueryCriteria>}.
   * 
   * @param source the source object to convert, which must be an instance of
   *               {@code String} (never {@code null})
   * @return the converted object, which must be an instance of
   *         {@code List<QueryCriteria>} (potentially {@code null})
   * @throws IllegalArgumentException if the source cannot be converted to the
   *                                  desired target type
   */
  @Override
  public List<QueryCriteria> convert(String source) {
    log.debug("convert(String source) - start");
    List<QueryCriteria> searchCriterias = new ArrayList<>();
    if (source != null) {
      Matcher matcher = searchPattern.matcher(source + ",");
      while (matcher.find()) {
        QueryCriteria searchCriteria = new QueryCriteria();
        searchCriteria.setKey(matcher.group(1));
        searchCriteria.setOperation(QueryOperation.fromString(matcher.group(2)));
        searchCriteria.setValue(matcher.group(3));
        searchCriterias.add(searchCriteria);
      }
    }
    log.debug("convert(String source) - end");
    return searchCriterias;
  }

}