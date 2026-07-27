package org.crue.hercules.sgi.com.exceptions;

import org.crue.hercules.sgi.com.enums.ServiceType;

/**
 * Exception to throw when the URL for a known {@link ServiceType} is not
 * configured.
 */
public class ServiceUrlNotConfiguredException extends RuntimeException {
  public ServiceUrlNotConfiguredException(String serviceType) {
    super(String.format("URL not configured for Service Type: %s", serviceType));
  }
}
