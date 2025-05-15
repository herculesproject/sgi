package org.crue.hercules.sgi.prc.config;

import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Propiedades de configuración.
 */
@ConfigurationProperties(prefix = "sgi")
@Data
public class SgiConfigProperties {
  /**
   * TimeZone.
   */
  private TimeZone timeZone;

  /**
   * Web Url.
   */
  @NotNull
  private String webUrl;
}
