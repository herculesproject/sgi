package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.Oracle12cDialect;

public class CustomOracle12cDialect extends Oracle12cDialect {

  public CustomOracle12cDialect() {
    super();
    registerFunction("remove_accents_and_html_tags",
        new RemoveAccentsAndHTMLOracleSQLFunction("remove_accents_and_html_tags"));
  }
}