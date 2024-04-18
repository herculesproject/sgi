package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.PostgreSQL94Dialect;

public class CustomPostgreSQL9Dialect extends PostgreSQL94Dialect {

  public CustomPostgreSQL9Dialect() {
    super();
    registerFunction("remove_accents_and_html_tags",
        new RemoveAccentsAndHTMLPostgresSQLFunction("remove_accents_and_html_tags"));
  }
}