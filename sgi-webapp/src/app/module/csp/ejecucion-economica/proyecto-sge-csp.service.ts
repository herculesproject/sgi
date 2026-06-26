import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { environment } from '@env';

/**
 * Variante de {@link ProyectoSgeService} para el perfil de investigador que
 * enruta las peticiones a CSP que valida el acceso y reenvía al SGE.
 */
@Injectable({
  providedIn: 'root'
})
export class ProyectoSgeCspService extends ProyectoSgeService {
  private static readonly MAPPING_CSP = '/ejecucion-economica-investigador/proyectos-sge';

  constructor(protected http: HttpClient) {
    super(http);
    // Sobreescribe la URL base
    (this as unknown as { endpointUrl: string }).endpointUrl =
      `${environment.serviceServers.csp}${ProyectoSgeCspService.MAPPING_CSP}`;
  }

}
