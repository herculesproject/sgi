import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

import { NGXLogger } from 'ngx-logger';
import { throwError, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { environment } from '@env';

export class BaseService<T> {
  protected apiEndpoint: string;

  constructor(protected logger: NGXLogger, protected http: HttpClient, protected mapping: string) {
    this.logger.debug(BaseService.name, 'constructor(protected logger: NGXLogger, protected http: HttpClient, mapping: string)', 'start');
    this.apiEndpoint = environment.apiUrl + mapping;
    this.logger.debug(BaseService.name, 'constructor(protected logger: NGXLogger, protected http: HttpClient, mapping: string)', 'end');
  }

  public create(object: T): Observable<T> {
    this.logger.debug(BaseService.name, 'create(object: T)', 'start');
    const postBody = this.convert(object);

    return this.http.post<any>(this.apiEndpoint, postBody, {
      headers: new HttpHeaders().set('Accept', 'application/json')
        .set('Content-Type', 'application/json'),
    }).pipe(
      map(res => { this.logger.debug(BaseService.name, 'create(object: T)', 'end'); return this.mapObject(res); }),
      catchError(error => {
        this.logger.error(BaseService.name, error);
        return throwError(error);
      }),
    );
  }

  public update(object: T, id: any): Observable<T> {
    this.logger.debug(BaseService.name, 'update(object: T, id: any)', 'start');
    const putBody = this.convert(object);

    return this.http.put<any>(this.apiEndpoint + '/' + id, putBody, {
      headers: new HttpHeaders().set('Accept', 'application/json')
        .set('Content-Type', 'application/json'),
    }).pipe(
      map(res => { this.logger.debug(BaseService.name, 'update(object: T, id: any)', 'end'); return this.mapObject(res); }),
      catchError(error => {
        this.logger.error(BaseService.name, error);
        return throwError(error);
      }),
    );
  }

  public delete(id: any): Observable<T> {
    this.logger.debug(BaseService.name, 'delete(id: any)', 'start');

    return this.http.delete<any>(this.apiEndpoint + '/' + id, {
      headers: new HttpHeaders().set('Accept', 'application/json')
        .set('Content-Type', 'application/json'),
    }).pipe(
      map(res => { this.logger.debug(BaseService.name, 'delete(id: any)', 'end'); return res; }),
      catchError(error => {
        this.logger.error(BaseService.name, error);
        return throwError(error);
      }),
    );
  }

  public findAll(filtro?: any): Observable<T[]> {
    this.logger.debug(BaseService.name, 'findAll()', 'start');
    return this.http.get<Array<T>>(this.apiEndpoint, {
      headers: new HttpHeaders().set('Accept', 'application/json'),
      params: new HttpParams({ fromObject: { ...filtro } })
    }).pipe(
      map(res => { this.logger.debug(BaseService.name, 'findAll()', 'end'); return this.mapObjects(res); }),
      catchError(error => {
        this.logger.error(BaseService.name, error);
        return throwError(error);
      }),
    );
  }

  public getOne(id: any): Observable<T> {
    this.logger.debug(BaseService.name, 'getOne(id: any)', 'start');
    return this.http.get<T>(this.apiEndpoint + '/' + id, {
      headers: new HttpHeaders().set('Accept', 'application/json')
    }).pipe(
      map(res => { this.logger.debug(BaseService.name, 'getOne(id: any)', 'end'); return this.mapObject(res); }),
      catchError(error => {
        this.logger.error(error);
        return throwError(error);
      }),
    );
  }



  protected mapObjects(elements: Array<any>): Array<T> {
    return elements.map(element => this.mapObject(element));
  }

  protected mapObject(element: any): T {
    return element;
  }

  protected convert(object: T): any {
    return object;
  }
}
