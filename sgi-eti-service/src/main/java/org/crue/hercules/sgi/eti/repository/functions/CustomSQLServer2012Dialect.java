package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.SQLServer2012Dialect;

public class CustomSQLServer2012Dialect extends SQLServer2012Dialect {

  public CustomSQLServer2012Dialect() {
    super();
    registerFunction("remove_accents_and_html_tags",
        new RemoveAccentsAndHTMLSQLServerFunction("remove_accents_and_html_tags"));
  }
}