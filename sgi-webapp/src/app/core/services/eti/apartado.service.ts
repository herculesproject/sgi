import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { IApartadoOutput } from '@core/models/eti/apartado-output';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApartadoService extends SgiReadOnlyRestService<number, IApartado> {
  private static readonly MAPPING = '/apartados';

  constructor(protected http: HttpClient) {
    super(
      ApartadoService.name,
      `${environment.serviceServers.eti}${ApartadoService.MAPPING}`,
      http
    );
  }

  /**
 * Obtiene el apartado traducido
 * @param idApartado identificador del apartado
 */
  getByApartadoId(idApartado: number): Observable<IApartadoOutput> {
    return this.http.get<IApartadoOutput>(
      `${this.endpointUrl}/${idApartado}`
    );
  }

  /**
 * Obtiene el apartado traducido
 * @param idApartado identificador del apartado
 */
  getByApartadoIdAndPadreId(idApartado: number, idPadre: number): Observable<IApartadoOutput> {
    return this.http.get<IApartadoOutput>(
      `${this.endpointUrl}/${idApartado}/padre/${idPadre}`
    );
  }

  /**
   * Devuelve los apartados hijos de un apartado
   *
   * @param id Id del apartado
   * @param options Opciones de paginación
   */
  getHijos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartadoOutput>> {
    return this.find<IApartadoOutput, IApartadoOutput>(`${this.endpointUrl}/${id}/hijos`, options);
  }

  /**
 * Devuelve los apartados hijos de un apartado
 *
 * @param id Id del apartado
 * @param options Opciones de paginación
 */
  getApartadosAllLanguages(id: number): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/all-languages`);
  }

  /**
  * Devuelve los apartados hijos de un apartado
  *
  * @param id Id del apartado
  @param idPadre Id del apartado padre
  * @param options Opciones de paginación
  */
  getApartadosPadresAllLanguages(id: number, idPadre: number): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/all-languages/${idPadre}`);
  }

  /**
 * Devuelve los apartados hijos de un apartado
 *
 * @param id Id del apartado
 * @param options Opciones de paginación
 */
  getHijosAllLanguages(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/hijos/all-languages`, options);
  }
}
