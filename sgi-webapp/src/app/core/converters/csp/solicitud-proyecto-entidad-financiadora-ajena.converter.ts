import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoEntidadFinanciadoraAjenaConverter extends
  SgiBaseConverter<ISolicitudProyectoEntidadFinanciadoraAjenaBackend, ISolicitudProyectoEntidadFinanciadoraAjena> {

  toTarget(value: ISolicitudProyectoEntidadFinanciadoraAjenaBackend): ISolicitudProyectoEntidadFinanciadoraAjena {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidadFinanciadoraAjena;
    }
    return {
      id: value.id,
      empresa: { personaRef: value.entidadRef } as IEmpresaEconomica,
      solicitudProyectoId: value.solicitudProyectoId,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion
    };
  }

  fromTarget(value: ISolicitudProyectoEntidadFinanciadoraAjena): ISolicitudProyectoEntidadFinanciadoraAjenaBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidadFinanciadoraAjenaBackend;
    }
    return {
      id: value.id,
      entidadRef: value.empresa.personaRef,
      solicitudProyectoId: value.solicitudProyectoId,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion
    };
  }
}

export const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER = new SolicitudProyectoEntidadFinanciadoraAjenaConverter();
