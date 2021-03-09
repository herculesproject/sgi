import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_DATOS_CONVERTER } from './solicitud-proyecto-datos.converter';

class SolicitudProyectoPresupuestoConverter extends SgiBaseConverter<ISolicitudProyectoPresupuestoBackend, ISolicitudProyectoPresupuesto> {

  toTarget(value: ISolicitudProyectoPresupuestoBackend): ISolicitudProyectoPresupuesto {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuesto;
    }
    return {
      id: value.id,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.toTarget(value.solicitudProyectoDatos),
      conceptoGasto: value.conceptoGasto,
      empresa: { personaRef: value.entidadRef } as IEmpresaEconomica,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      observaciones: value.observaciones,
      financiacionAjena: value.financiacionAjena
    };
  }

  fromTarget(value: ISolicitudProyectoPresupuesto): ISolicitudProyectoPresupuestoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuestoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.fromTarget(value.solicitudProyectoDatos),
      conceptoGasto: value.conceptoGasto,
      entidadRef: value.empresa?.personaRef,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      observaciones: value.observaciones,
      financiacionAjena: value.financiacionAjena ? true : false
    };
  }
}

export const SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER = new SolicitudProyectoPresupuestoConverter();
