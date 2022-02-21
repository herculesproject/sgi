package org.crue.hercules.sgi.csp.service.cnf;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.cnf.ConfigOutput;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigService {
  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING_CONFIG = PATH_DELIMITER + "config";

  public static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  public static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  public static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  public static final String MESSAGE_KEY_NAME = "name";

  public static final String CLIENT_REGISTRATION_ID = "csp-service";

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final String baseUrl;

  public ConfigService(RestTemplate restTemplate, RestApiProperties restApiProperties, ObjectMapper mapper) {
    log.debug(
        "ConfigService(RestTemplate restTemplate, RestApiProperties restApiProperties, ObjectMapper mapper) - start");
    this.restTemplate = restTemplate;
    this.mapper = mapper;
    this.baseUrl = restApiProperties.getCnfUrl() + MAPPING_CONFIG;
    log.debug(
        "ConfigService(RestTemplate restTemplate, RestApiProperties restApiProperties, ObjectMapper mapper) - end");
  }

  public String findByName(String name) {
    log.debug("findByName(String name) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                ConfigOutput.class))
            .build());
    String endPoint = baseUrl + "/" + name;
    log.info("Calling SGI API endpoint: {}", endPoint);
    ResponseEntity<ConfigOutput> response = restTemplate.exchange(
        endPoint, HttpMethod.GET,
        new HttpEntityBuilder<>().withClientAuthorization(
            CLIENT_REGISTRATION_ID).build(),
        ConfigOutput.class);
    log.info("Endpoint response: {}", response);

    ConfigOutput configOutput = response.getBody();
    String returnValue = null;
    if (configOutput != null) {
      returnValue = configOutput.getValue();
    }
    log.debug("findByName(String name) - end");
    return returnValue;
  }

  public List<String> findStringListByName(String name) throws JsonProcessingException {
    log.debug("findStringListByName(String name) - start");
    String value = findByName(name);
    List<String> valueList = null;
    if (value == null) {
      valueList = Collections.emptyList();
    } else {
      valueList = mapper.readValue(value,
          TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }

    log.debug("findStringListByName(String name) - end");
    return valueList;
  }

}
