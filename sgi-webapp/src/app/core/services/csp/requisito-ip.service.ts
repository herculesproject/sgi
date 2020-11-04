import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRequisitoIP } from '@core/models/csp/requisito-ip';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RequisitoIPService extends SgiRestService<number, IRequisitoIP> {
  private static readonly MAPPING = '/convocatoria-requisitoips';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      RequisitoIPService.name,
      logger,
      `${environment.serviceServers.csp}${RequisitoIPService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera el requisito ip de la convocatoria
   * @param id convocatoria
   */
  getRequisitoIPConvocatoria(id: number): Observable<IRequisitoIP> {
    this.logger.debug(RequisitoIPService.name, `getRequisitoIP(${id})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${id}`;
    return this.http.get<IRequisitoIP>(endpointUrl)
      .pipe(
        tap(() => this.logger.debug(RequisitoIPService.name, `getRequisitoIP(${id})`, '-', 'end'))
      );
  }
}
