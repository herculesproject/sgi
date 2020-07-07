package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ErrorController
 */
@RestController
@RequestMapping("error")
@Slf4j
public class ErrorController
    extends org.crue.hercules.sgi.framework.boot.autoconfigure.web.servlet.error.ErrorController {

  @Autowired
  public ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
      List<ErrorViewResolver> errorViewResolvers) {
    super(errorAttributes, serverProperties, errorViewResolvers);
    log.debug(
        "ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties, List<ErrorViewResolver> errorViewResolvers) - start");
  }

}