package org.crue.hercules.sgi.framework.security.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SgiAuthenticationEntryPoint implements AuthenticationEntryPoint {
  ObjectMapper mapper;

  public SgiAuthenticationEntryPoint(ObjectMapper mapper) {
    log.debug("SgiAuthenticationEntryPoint(ObjectMapper mapper) - start");
    this.mapper = mapper;
    log.debug("SgiAuthenticationEntryPoint(ObjectMapper mapper) - end");
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
      throws IOException, ServletException {
    log.debug("commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) - start");
    Error error = new Error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
    response.setStatus(error.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON.toString());
    OutputStream out = response.getOutputStream();
    mapper.writeValue(out, error);
    out.flush();
    log.debug("commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) - end");
  }

}