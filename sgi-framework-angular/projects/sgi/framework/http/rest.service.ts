import { SgiMutableRestService } from './mutable.rest.service';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiNoConversionConverter } from '@sgi/framework/core';

/**
 * Base service to consume REST endpoints without transformation
 *
 * Contains the common operations.
 *
 * @template K type of ID
 * @template T type of return element
 */
export class SgiRestService<K extends number | string, T> extends SgiMutableRestService<K, T, T> {
  constructor(serviceName: string, logger: NGXLogger, endpointUrl: string, http: HttpClient) {
    super(serviceName, logger, endpointUrl, http, new SgiNoConversionConverter<T, T>());
  }
}