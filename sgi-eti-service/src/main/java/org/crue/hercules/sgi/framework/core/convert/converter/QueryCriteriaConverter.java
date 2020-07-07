package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.data.search.QueryOperation;

import org.springframework.core.convert.converter.Converter;

public class QueryCriteriaConverter implements Converter<String, List<QueryCriteria>> {
  private static String wordRegex = "[A-Za-z0-9_ñÑ\\.]*";
  private static String valueRegex = "[A-Za-z0-9_áéíóúäëïöüÁÉÍÓÚÄËÏÖÜñÑ\\-\\.%\\s:]+";
  private static String operatorRegex = "(!?:|!?~|<:?|>:?)";
  private static String fullRegex = "(" + wordRegex + ")" + operatorRegex + "(" + valueRegex + ")?,";
  private static final Pattern searchPattern = Pattern.compile(fullRegex);

  @Override
  public List<QueryCriteria> convert(String source) {
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
    return searchCriterias;
  }

}