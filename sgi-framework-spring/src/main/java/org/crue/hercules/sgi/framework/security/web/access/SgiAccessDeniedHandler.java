package org.crue.hercules.sgi.framework.security.web.access;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SgiAccessDeniedHandler implements AccessDeniedHandler {
  ObjectMapper mapper;

  public SgiAccessDeniedHandler(ObjectMapper mapper) {
    log.debug("SgiAccessDeniedHandler(ObjectMapper mapper) - start");
    this.mapper = mapper;
    log.debug("SgiAccessDeniedHandler(ObjectMapper mapper) - end");
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException, ServletException {
    log.debug("handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) - start");
    Error error = new Error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
    response.setStatus(error.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON.toString());
    OutputStream out = response.getOutputStream();
    mapper.writeValue(out, error);
    out.flush();
    log.debug("handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) - end");
  }

}