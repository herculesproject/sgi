import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRespuesta } from '@core/models/eti/respuesta';
import { environment } from '@env';
import { SgiRestFilterType, SgiRestFindOptions, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RespuestaService extends SgiRestService<number, IRespuesta>{

  private static readonly MAPPING = '/respuestas';

  constructor(protected http: HttpClient) {
    super(
      RespuestaService.name,
      `${environment.serviceServers.eti}${RespuestaService.MAPPING}`,
      http
    );
  }

  findByMemoriaIdAndApartadoId(memoriaId: number, apartadoId: number): Observable<IRespuesta> {
    const options: SgiRestFindOptions = {
      filters: [
        {
          field: 'memoria.id',
          type: SgiRestFilterType.EQUALS,
          value: memoriaId.toString()
        },
        {
          field: 'apartado.id',
          type: SgiRestFilterType.EQUALS,
          value: apartadoId.toString()
        },
      ]
    };
    return this.find<IRespuesta, IRespuesta>(`${this.endpointUrl}`, options).pipe(
      map((response) => {
        if (response.items.length > 0) {
          return response.items[0];
        }
        else {
          return;
        }
      })
    );
  }

}
