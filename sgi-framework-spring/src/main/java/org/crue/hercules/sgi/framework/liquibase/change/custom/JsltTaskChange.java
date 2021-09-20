package org.crue.hercules.sgi.framework.liquibase.change.custom;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsltTaskChange implements CustomTaskChange {

  private ResourceAccessor resourceAccessor;
  private String tableName;
  private String idColumnName;
  private String jsonColumnName;
  private String jsltFile;

  private static ObjectMapper mapper = new ObjectMapper();
  private int updateCount = 0;
  private Expression jsltExpression;

  @Override
  public String getConfirmationMessage() {
    return "JsltTaskChange: " + updateCount + " row(s) affected";
  }

  @Override
  public void setUp() throws SetupException {
    // Do nothing
  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {
    this.resourceAccessor = resourceAccessor;
  }

  @Override
  public ValidationErrors validate(Database database) {
    return null;
  }

  @Override
  public void execute(Database database) throws CustomChangeException {
    // Connection con = ((JdbcConnection)
    // database.getConnection()).getUnderlyingConnection();
    JdbcConnection db_connection = (JdbcConnection) database.getConnection();
    PreparedStatement selectStatement = null;
    PreparedStatement updateStatement = null;
    ResultSet results;

    try {
      selectStatement = db_connection
          .prepareStatement("SELECT " + idColumnName + ", " + jsonColumnName + " FROM " + tableName);
      results = selectStatement.executeQuery();

      while (results.next()) {
        String rowid = results.getString(idColumnName);
        String json = results.getString(jsonColumnName);
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode newJsonNode = applyJslt(jsonNode);
        if (!jsonNode.equals(newJsonNode)) {
          String newJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newJsonNode);
          StringBuilder sb = new StringBuilder();
          sb.append("UPDATE " + tableName);
          sb.append(" SET ");
          sb.append(jsonColumnName + " = ? ");
          sb.append("WHERE " + idColumnName + " = ?");
          updateStatement = db_connection.prepareStatement(sb.toString());
          updateStatement.setString(1, newJson);
          updateStatement.setString(2, rowid);

          executePreparedStatement(updateStatement);
          updateStatement.close();
          updateStatement = null;
        }
      }
      selectStatement.close();
      selectStatement = null;
    } catch (Exception e) {
      throw new CustomChangeException("Cannot update", e);
    } finally {
      try {
        if (selectStatement != null)
          selectStatement.close();
        if (updateStatement != null)
          updateStatement.close();
      } catch (SQLException e) {
        log.warn("Database error: ", e);
      }
    }
  }

  protected void executePreparedStatement(PreparedStatement stmt) throws SQLException {
    // if execute returns false, we can retrieve the affected rows count
    // (true used when resultset is returned)
    if (!stmt.execute()) {
      int partialUpdateCount = stmt.getUpdateCount();
      if (partialUpdateCount != -1)
        updateCount += partialUpdateCount;
    } else {
      int partialUpdateCount = 0;
      // cycle for retrieving row counts from all statements
      do {
        if (!stmt.getMoreResults()) {
          partialUpdateCount = stmt.getUpdateCount();
          if (partialUpdateCount != -1)
            updateCount += partialUpdateCount;
        }
      } while (updateCount != -1);
    }
  }

  private JsonNode applyJslt(JsonNode jsonNode)
      throws CustomChangeException, JsonMappingException, JsonProcessingException {
    Expression expression = getJsltExpression();
    if (expression != null) {
      return expression.apply(jsonNode);
    } else {
      return jsonNode;
    }
  }

  private Expression getJsltExpression() throws CustomChangeException {
    if (jsltExpression == null) {
      try {
        Set<InputStream> streams = resourceAccessor.getResourcesAsStream(jsltFile);

        // TODO make sure there is one and only one jslt file
        for (InputStream stream : streams) {
          this.jsltExpression = new Parser(new InputStreamReader(stream)).compile();
        }
      } catch (Exception e) {
        throw new CustomChangeException(e);
      }
    }
    return jsltExpression;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getJsonColumnName() {
    return jsonColumnName;
  }

  public void setJsonColumnName(String jsonColumnName) {
    this.jsonColumnName = jsonColumnName;
  }

  public String getJsltFile() {
    return jsltFile;
  }

  public void setJsltFile(String jsltFile) {
    this.jsltFile = jsltFile;
  }

  public String getIdColumnName() {
    return idColumnName;
  }

  public void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

}
