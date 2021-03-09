import { ISolicitudProyectoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-backend';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_DATOS_CONVERTER } from './solicitud-proyecto-datos.converter';

class SolicitudProyectoSocioConverter extends SgiBaseConverter<ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio> {

  toTarget(value: ISolicitudProyectoSocioBackend): ISolicitudProyectoSocio {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocio;
    }
    return {
      empresa: { personaRef: value.empresaRef } as IEmpresaEconomica,
      id: value.id,
      importeSolicitado: value.importeSolicitado,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      numInvestigadores: value.numInvestigadores,
      rolSocio: value.rolSocio,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.toTarget(value.solicitudProyectoDatos)
    };
  }

  fromTarget(value: ISolicitudProyectoSocio): ISolicitudProyectoSocioBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioBackend;
    }
    return {
      empresaRef: value.empresa.personaRef,
      id: value.id,
      importeSolicitado: value.importeSolicitado,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      numInvestigadores: value.numInvestigadores,
      rolSocio: value.rolSocio,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.fromTarget(value.solicitudProyectoDatos)
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_CONVERTER = new SolicitudProyectoSocioConverter();
