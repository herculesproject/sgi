package org.crue.hercules.sgi.framework.data.search;

import lombok.Data;

@Data
public class QueryCriteria {
  private String key;
  private QueryOperation operation;
  private String value;
}