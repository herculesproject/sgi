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
import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.data.sort.SortCriteria;

public class SortQueryMediator extends AbstractMediator {

  private static SortCriteriaConverter sortCriteriaConverter = new SortCriteriaConverter();
  
  private static String ORDER_BY = " ORDER BY ";
  private static String SORT_QUERY_PROPERTY_KEY = "sortQuery";
  private static String SORT_PARAM_PROPERTY_KEY = "query.param.s";
  private static String SQL_ASC = " ASC";
  private static String SQL_DESC = " DESC";

  @Override
  public boolean mediate(MessageContext messageContext) {

    String sortQuery = "";
    
    try {
      String sortParam = (String) messageContext.getProperty(SORT_PARAM_PROPERTY_KEY);

      if (StringUtils.isNotBlank(sortParam)) {
        String decodedSortParam = URLDecoder.decode(sortParam, StandardCharsets.UTF_8.toString());
        sortQuery = URLEncoder.encode(convertSortQuery(decodedSortParam), StandardCharsets.UTF_8.toString());
      }
      
    } catch (UnsupportedEncodingException e) {
      messageContext.setProperty(SORT_QUERY_PROPERTY_KEY, "");
      return false;
    }

    messageContext.setProperty(SORT_QUERY_PROPERTY_KEY, sortQuery);

    return true;
  }
  

  /**
   * Convierte el sortParam en su representancion en SQL.
   * 
   * @param sortParam string a convertir
   * @return order by con las condiciones recuperadas del sortParam.
   */
  private String convertSortQuery(String sortParam) {
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(sortParam);

    String sortQuery = sortCriterias.stream().map(sortCriteria -> {
      String key = sortCriteria.getKey().replace(".", "_");
      
      switch (sortCriteria.getOperation()) {
        case ASC:
          return key.concat(SQL_ASC);
        case DESC:
          return key.concat(SQL_DESC);
        default:
          return null;
      }
    }).filter((sort) -> sort != null).collect(Collectors.joining(", "));

    return ORDER_BY.concat(sortQuery);
  }
  
}
