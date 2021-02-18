package org.crue.hercules.sgi.esb.mediator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.crue.hercules.sgi.framework.core.convert.converter.QueryCriteriaConverter;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

public class FilterQueryMediator extends AbstractMediator {

  private static QueryCriteriaConverter queryCriteriaConverter = new QueryCriteriaConverter();
  
  
  private static String FILTER_QUERY_PROPERTY_KEY = "filterQuery";
  private static String FILTER_PARAM_PROPERTY_KEY = "query.param.q";
  private static String SQL_AND = " AND ";
  private static String SQL_EQUALS = " = ";
  private static String SQL_NOT_EQUALS = " != ";
  private static String SQL_LOWER_OR_EQUAL = " <= ";
  private static String SQL_GREATER_OR_EQUAL = " >= ";
  private static String SQL_LIKE = " LIKE ";
  private static String SQL_NOT_LIKE = " NOT LIKE ";
  private static String SQL_LOWER = " < ";
  private static String SQL_GREATER = " > ";
  private static String SQL_TRUE = " true ";
  private static String SQL_TRUE_ENCODED = "%20true%20";

  @Override
  public boolean mediate(MessageContext messageContext) {

    String filterQuery = SQL_TRUE_ENCODED;
    
    try {
      String filterParam = (String) messageContext.getProperty(FILTER_PARAM_PROPERTY_KEY);

      if (StringUtils.isNotBlank(filterParam)) {
        String decodedFilterParam = URLDecoder.decode(filterParam, StandardCharsets.UTF_8.toString());
        filterQuery = URLEncoder.encode(convertFilterQuery(decodedFilterParam), StandardCharsets.UTF_8.toString());
      }
      
    } catch (UnsupportedEncodingException e) {
      messageContext.setProperty(FILTER_QUERY_PROPERTY_KEY, SQL_TRUE_ENCODED);
      return false;
    }

    messageContext.setProperty(FILTER_QUERY_PROPERTY_KEY, filterQuery);

    return true;
  }
  
  /**
   * Convierte el filterParam en su representancion en SQL.
   * 
   * @param filterParam string a convertir
   * @return where con las condiciones recuperadas del filterParam.
   */
  private String convertFilterQuery(String filterParam) {
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(filterParam);

    String filterQuery = queryCriterias.stream().map(queryCriteria -> {
      String key = queryCriteria.getKey().replace(".", "_");
        
      switch (queryCriteria.getOperation()) {
        case EQUALS:
          return key.concat(SQL_EQUALS).concat("'").concat(queryCriteria.getValue()).concat("'");

        case NOT_EQUALS:
          return key.concat(SQL_NOT_EQUALS).concat("'").concat(queryCriteria.getValue()).concat("'");

        case LOWER_OR_EQUAL:
          return key.concat(SQL_LOWER_OR_EQUAL).concat("'").concat(queryCriteria.getValue())
              .concat("'");

        case GREATER_OR_EQUAL:
          return key.concat(SQL_GREATER_OR_EQUAL).concat("'").concat(queryCriteria.getValue())
              .concat("'");

        case LIKE:
          return "LOWER(".concat(key).concat(")").concat(SQL_LIKE).concat("LOWER('%")
              .concat(queryCriteria.getValue()).concat("%')");

        case NOT_LIKE:
          return "LOWER(".concat(key).concat(")").concat(SQL_NOT_LIKE).concat("LOWER('%")
              .concat(queryCriteria.getValue()).concat("%')");

        case LOWER:
          return key.concat(SQL_LOWER).concat("'").concat(queryCriteria.getValue()).concat("'");

        case GREATER:
          return key.concat(SQL_GREATER).concat("'").concat(queryCriteria.getValue()).concat("'");

        default:
          return null;
      }
    }).filter((queryCriteria) -> queryCriteria != null).collect(Collectors.joining(SQL_AND));

    if (StringUtils.isBlank(filterQuery)) {
      filterQuery = SQL_TRUE;
    }

    return filterQuery;
  }
  
}
