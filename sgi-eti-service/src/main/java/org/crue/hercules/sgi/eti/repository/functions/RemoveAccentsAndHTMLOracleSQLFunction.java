package org.crue.hercules.sgi.eti.repository.functions;

import java.util.List;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class RemoveAccentsAndHTMLOracleSQLFunction extends StandardSQLFunction {

  public RemoveAccentsAndHTMLOracleSQLFunction(String name) {
    super(name);
  }

  @Override
  public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
    return "TRANSLATE(UPPER(regexp_replace(" + arguments.get(0)
        + ", '<[^>]*>', '')), 'ñáéíóúàèìòùãõâêîôôäëïöüçÑÁÉÍÓÚÀÈÌÒÙÃÕÂÊÎÔÛÄËÏÖÜÇ', 'naeiouaeiouaoaeiooaeioucNAEIOUAEIOUAOAEIOOAEIOUC')";
  }
}