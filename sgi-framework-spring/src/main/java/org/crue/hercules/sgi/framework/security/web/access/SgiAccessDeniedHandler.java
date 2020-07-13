package org.crue.hercules.sgi.framework.security.web.access;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class SgiAccessDeniedHandler implements AccessDeniedHandler {
  ObjectMapper mapper;

  public SgiAccessDeniedHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException, ServletException {
    Error error = new Error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
    response.setStatus(error.getStatus().value());
    OutputStream out = response.getOutputStream();
    mapper.writeValue(out, error);
    out.flush();
  }

}