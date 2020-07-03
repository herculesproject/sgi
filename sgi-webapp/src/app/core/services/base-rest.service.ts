import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { Direction, Filter, FilterType, FindOptions, ListResult, PageRequest, Sort } from './types';

/**
 * Base service to consume REST endpoints.
 *
 * Contains the common operations.
 */
export abstract class BaseRestService<T> {
  /** The HttpClient to use in request */
  protected readonly http: HttpClient;
  /** The REST Endpoint URL common for all service operations */
  protected readonly endpointUrl: string;
  /** The logger */
  private readonly logger: NGXLogger;
  /** The Service Name to log */
  private readonly serviceName: string;

  /**
   *
   * @param serviceName The service name to appear in log
   * @param logger The logger to use
   * @param endpointRelativePath The endpoint relative URL path
   * @param http The HttpClient to use
   */
  constructor(
    serviceName: string,
    logger: NGXLogger,
    endpointUrl: string,
    http: HttpClient
  ) {
    this.serviceName = serviceName;
    this.logger = logger;
    this.endpointUrl = endpointUrl;
    this.http = http;
  }

  /**
   * Create the element and return the persisted value
   *
   * @param element The element to create
   */
  public create(element: T): Observable<T> {
    this.logger.debug(
      this.serviceName,
      `create(${JSON.stringify(element)})`,
      '-',
      'START'
    );
    return this.http.post<T>(this.endpointUrl, element).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(
          this.serviceName,
          `create(${JSON.stringify(element)}):`,
          error
        );
        // Pass the error to subscribers. Anyway they would decide what
        // to do with the error.
        return throwError(error);
      }),
      map((response) => {
        this.logger.debug(
          this.serviceName,
          `create(${JSON.stringify(element)})`,
          '-',
          'END'
        );
        return response;
      })
    );
  }

  /**
   * Update an element and return the persisted value
   *
   * @param id The ID of the element
   * @param element The element to update
   */
  public update(id: number, element: T): Observable<T> {
    this.logger.debug(
      this.serviceName,
      `update(${id}, ${JSON.stringify(element)})`,
      '-',
      'START'
    );
    return this.http.put<T>(`${this.endpointUrl}/${id}`, element).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(
          this.serviceName,
          `update(${id}, ${JSON.stringify(element)}):`,
          error
        );
        // Pass the error to subscribers. Anyway they would decide what to do
        // with the error.
        return throwError(error);
      }),
      map((response) => {
        this.logger.debug(
          this.serviceName,
          `update(${id}, ${JSON.stringify(element)})`,
          '-',
          'END'
        );
        return response;
      })
    );
  }

  /**
   * Delete an element by their ID
   *
   * @param id The ID of the element
   */
  public deleteById(id: number) {
    this.logger.debug(this.serviceName, `deleteById(${id})`, '-', 'START');
    return this.http.delete<T>(`${this.endpointUrl}/${id}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(this.serviceName, `deleteById(${id}):`, error);
        // Pass the error to subscribers. Anyway they would decide what to do
        // with the error.
        return throwError(error);
      }),
      map(() => {
        this.logger.debug(this.serviceName, `deleteById(${id})`, '-', 'END');
      })
    );
  }

  /**
   * Delete all elements
   */
  public deleteAll() {
    this.logger.debug(this.serviceName, `deleteAll()`, '-', 'START');
    return this.http.delete<T>(`${this.endpointUrl}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(this.serviceName, `deleteAll():`, error);
        // Pass the error to subscribers. Anyway they would decide what to do
        // with the error.
        return throwError(error);
      }),
      map(() => {
        this.logger.debug(this.serviceName, `deleteAll()`, '-', 'END');
      })
    );
  }

  /**
   * Find an element by their ID
   *
   * @param id The ID of the element
   */
  // TODO: Manage 404 (NotFound) and return an empty element?
  public findById(id: number): Observable<T> {
    this.logger.debug(this.serviceName, `findById(${id})`, '-', 'START');
    return this.http.get<T>(`${this.endpointUrl}/${id}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(this.serviceName, `findById(${id}):`, error);
        // Pass the error to subscribers. Anyway they would decide what to do
        // with the error.
        return throwError(error);
      }),
      map((response) => {
        this.logger.debug(this.serviceName, `findById(${id})`, '-', 'END');
        return response;
      })
    );
  }

  /**
   * Find a list of elements. Optionally, the elements can be requested
   * combining pagination, sorting and filtering
   *
   * @param options The options to apply
   */
  public findAll(options?: FindOptions): Observable<ListResult<T>> {
    this.logger.debug(
      this.serviceName,
      `findAll(${options ? JSON.stringify(options) : ''})`,
      '-',
      'START'
    );
    return this.http
      .get<T[]>(this.endpointUrl, {
        headers: this.getRequestHeaders(options?.page),
        params: this.getSearchParam(options?.sort, options?.filters),
        observe: 'response',
      })
      .pipe(
        // TODO: Explore the use a global HttpInterceptor with or without a
        // custom error
        catchError((error: HttpErrorResponse) => {
          // Log the error
          this.logger.error(
            this.serviceName,
            `findAll(${options ? JSON.stringify(options) : ''}):`,
            error
          );
          // Pass the error to subscribers. Anyway they would decide what to
          // do with the error.
          return throwError(error);
        }),
        switchMap((r) => {
          this.logger.debug(
            this.serviceName,
            `findAll(${options ? JSON.stringify(options) : ''})`,
            '-',
            'END'
          );
          return of({
            page: {
              index: Number(r.headers.get('X-Page')),
              size: Number(r.headers.get('X-Page-Size')),
              count: Number(r.headers.get('X-Page-Count')),
              total: Number(r.headers.get('X-Page-Total-Count')),
            },
            total: Number(r.headers.get('X-Total-Count')),
            items: r.body,
          });
        })
      );
  }

  private getCommonHeaders(): HttpHeaders {
    return new HttpHeaders().set('Accept', 'application/json');
  }

  /**
   * Build the request headers to use
   * @param pageRequest Optional page request to use
   */
  private getRequestHeaders(pageRequest?: PageRequest): HttpHeaders {
    let headers = this.getCommonHeaders();
    if (pageRequest) {
      if (pageRequest.size) {
        headers = headers.set('X-Page-Size', pageRequest.size.toString());
        headers = headers.set(
          'X-Page',
          pageRequest.index ? pageRequest.index.toString() : '0'
        );
      }
    }
    return headers;
  }

  private getSearchParam(sort: Sort, filters: Filter[]): HttpParams {
    let param = new HttpParams();
    const filterValues: string[] = [];
    if (filters) {
      filters.forEach((filter) => {
        if (
          filter.field &&
          filter.value &&
          filter.type &&
          filter.type !== FilterType.NONE
        ) {
          filterValues.push(
            filter.field + filter.type.toString() + filter.value
          );
        }
      });
    }
    if (filterValues.length > 0) {
      param = param.append('q', filterValues.join(','));
    }
    // Sorting only is valid if al least a field is declared
    if (sort && sort.field) {
      // If no declared direction, then ASC is used
      param = param.append(
        's',
        sort.field +
          (sort.direction ? sort.direction.toString() : Direction.ASC)
      );
    }
    return param;
  }
}
