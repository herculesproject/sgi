import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { map, switchMap, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { EmpresaEconomicaService } from '../sgp/empresa-economica.service';

export interface ISolicitudProyectoSocioBackend {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatos;
  empresaRef: string;
  rolSocio: IRolSocio;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
}

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioService extends SgiMutableRestService<number, ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio>  {
  private static readonly MAPPING = '/solicitudproyectosocio';

  static CONVERTER = new class extends SgiBaseConverter<ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio> {
    toTarget(value: ISolicitudProyectoSocioBackend): ISolicitudProyectoSocio {
      const empresa: IEmpresaEconomica = {
        direccion: undefined,
        numeroDocumento: undefined,
        personaRef: value.empresaRef,
        personaRefPadre: undefined,
        razonSocial: undefined,
        tipo: undefined,
        tipoDocumento: undefined,
        tipoEmpresa: undefined
      };
      const result: ISolicitudProyectoSocio = {
        empresa,
        id: value.id,
        importeSolicitado: value.importeSolicitado,
        mesFin: value.mesFin,
        mesInicio: value.mesInicio,
        numInvestigadores: value.numInvestigadores,
        rolSocio: value.rolSocio,
        solicitudProyectoDatos: value.solicitudProyectoDatos
      };
      return result;
    }

    fromTarget(value: ISolicitudProyectoSocio): ISolicitudProyectoSocioBackend {
      const result: ISolicitudProyectoSocioBackend = {
        empresaRef: value.empresa.personaRef,
        id: value.id,
        importeSolicitado: value.importeSolicitado,
        mesFin: value.mesFin,
        mesInicio: value.mesInicio,
        numInvestigadores: value.numInvestigadores,
        rolSocio: value.rolSocio,
        solicitudProyectoDatos: value.solicitudProyectoDatos
      };
      return result;
    }
  }();

  constructor(
    logger: NGXLogger,
    protected http: HttpClient,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
    super(
      SolicitudProyectoSocioService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioService.MAPPING}`,
      http,
      SolicitudProyectoSocioService.CONVERTER
    );
  }

  findById(id: number): Observable<ISolicitudProyectoSocio> {
    this.logger.debug(SolicitudProyectoSocioService.name, `findById()`, '-', 'start');
    return super.findById(id).pipe(
      switchMap(solicitudProyectoSocio => {
        const personaRef = solicitudProyectoSocio.empresa.personaRef;
        return this.empresaEconomicaService.findById(personaRef).pipe(
          map(empresa => {
            solicitudProyectoSocio.empresa = empresa;
            return solicitudProyectoSocio;
          })
        );
      }),
      tap(() => this.logger.debug(SolicitudProyectoSocioService.name, `findById()`, '-', 'end'))
    );
  }
}
