package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.List;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.data.search.QueryOperation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryCriteriaConverterTest {

  @InjectMocks
  QueryCriteriaConverter queryCriteriaConverter;

  /**
   * @throws Exception
   */
  @Test
  public void convertEqualsExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: an equals expression
    String column = "column";
    String operator = ":";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertNotEqualsExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a not enquals expression
    String column = "column";
    String operator = "!:";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertLikeExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a like expression
    String column = "column";
    String operator = "~";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertNotLikeExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a not like expression
    String column = "column";
    String operator = "!~";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertGreaterExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a greater expression
    String column = "column";
    String operator = ">";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertGreaterOrEquealExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a greter or equeals expression
    String column = "column";
    String operator = ">:";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertLowerExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a lower expression
    String column = "column";
    String operator = "<";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertLowerOrEqualExpression_returnsEqualsQueryCriteria() throws Exception {
    // given: a lower or equals expression
    String column = "column";
    String operator = "<:";
    String value = "value";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_withSpecialCharacters_returnsQueryCriteria() throws Exception {
    // given: an expression with special characters
    String column = "column";
    String operator = ":";
    String value = "aA0ñÑáÁüÜ %_-.";
    String query = column + operator + value;

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(1);
    Assertions.assertThat(queryCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(queryCriterias.get(0).getOperation()).isEqualTo(QueryOperation.fromString(operator));
    Assertions.assertThat(queryCriterias.get(0).getValue()).isEqualTo(value);
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_multipleExpresion_returnsQueryCriteriaList() throws Exception {
    // given: an equals expression
    String column = "column";
    String operator = ":";
    String value = "value";
    String query = "";
    int elements = 3;
    for (int i = 0; i < elements; i++) {
      query += "," + column + i + operator + value + i;
    }
    query = query.substring(1);

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: the right QueryCriteria list is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(elements);
    for (int i = 0; i < elements; i++) {
      Assertions.assertThat(queryCriterias.get(i).getKey()).isEqualTo(column + i);
      Assertions.assertThat(queryCriterias.get(i).getOperation()).isEqualTo(QueryOperation.fromString(operator));
      Assertions.assertThat(queryCriterias.get(i).getValue()).isEqualTo(value + i);
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_noExpresion_returnsEmptyList() throws Exception {
    // given: a no query expression
    String query = "value not";

    // when: convert method invoqued with given expression
    List<QueryCriteria> queryCriterias = queryCriteriaConverter.convert(query);

    // then: no QueryCriteria is returned
    Assertions.assertThat(queryCriterias.size()).isEqualTo(0);
  }
}