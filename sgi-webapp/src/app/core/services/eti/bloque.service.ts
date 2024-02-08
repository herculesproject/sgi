import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { IApartadoOutput } from '@core/models/eti/apartado-output';
import { IBloque } from '@core/models/eti/bloque';
import { IBloqueOutput } from '@core/models/eti/bloque-output';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BloqueService extends SgiReadOnlyRestService<number, IBloque> {
  private static readonly MAPPING = '/bloques';

  constructor(protected http: HttpClient) {
    super(
      BloqueService.name,
      `${environment.serviceServers.eti}${BloqueService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve el bloque de comentarios generales
   */
  getBloqueComentariosGenerales(): Observable<IBloque> {
    return this.http.get<IBloque>(`${this.endpointUrl}/comentarios-generales`);
  }

  /**
* Obtiene el bloque traducido
* @param idBloque identificador del apartado
*/
  getByBloqueId(idBloque: number): Observable<IBloqueOutput> {
    return this.http.get<IBloqueOutput>(
      `${this.endpointUrl}/${idBloque}`
    );
  }

  /**
 * Devuelve los apartados de un bloque
 *
 * @param id Id del bloque
 * @param options Opciones de paginación
 */
  getApartadosAllLanguages(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/apartados/all-languages`, options);
  }

}
