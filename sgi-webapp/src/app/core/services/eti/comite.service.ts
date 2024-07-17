import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { ITipoMemoria } from '@core/models/eti/tipo-memoria';
import { IMemoriaResponse } from '@core/services/eti/memoria/memoria-response';
import { MEMORIA_RESPONSE_CONVERTER } from '@core/services/eti/memoria/memoria-response.converter';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends SgiReadOnlyRestService<number, IComite> {
  private static readonly MAPPING = '/comites';

  constructor(protected http: HttpClient) {
    super(
      ComiteService.name,
      `${environment.serviceServers.eti}${ComiteService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera la lista paginada de los tipos de memoria en función del comité recibido.
   * @param id Identificador del comité.
   * @param options Opciones de búsqueda.
   */
  findTipoMemoria(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoMemoria>> {
    return this.find<ITipoMemoria, ITipoMemoria>(`${this.endpointUrl}/${id}/tipo-memorias`, options);
  }

  /**
   * Recupera la lista paginada de las  memorias en función del comité recibido.
   * @param idComite Identificador del comité.
   * @param idPeticionEvaluacion Identificador de la petición de evaluación.
   * @param options Opciones de búsqueda.
   */
  findMemoriasComitePeticionEvaluacion(idComite: number, idPeticionEvaluacion: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/${idComite}/memorias-peticion-evaluacion/${idPeticionEvaluacion}`,
      options,
      MEMORIA_RESPONSE_CONVERTER
    );
  }
}
