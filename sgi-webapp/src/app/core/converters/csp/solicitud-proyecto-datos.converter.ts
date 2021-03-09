import { ISolicitudProyectoDatosBackend } from '@core/models/csp/backend/solicitud-proyecto-datos-backend';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_CONVERTER } from './solicitud.converter';

class SolicitudProyectoDatosConverter extends SgiBaseConverter<ISolicitudProyectoDatosBackend, ISolicitudProyectoDatos> {

  toTarget(value: ISolicitudProyectoDatosBackend): ISolicitudProyectoDatos {
    if (!value) {
      return value as unknown as ISolicitudProyectoDatos;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.toTarget(value.solicitud),
      titulo: value.titulo,
      acronimo: value.acronimo,
      duracion: value.duracion,
      colaborativo: value.colaborativo,
      coordinadorExterno: value.coordinadorExterno,
      universidadSubcontratada: value.universidadSubcontratada,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      areaTematica: value.areaTematica,
      checkListRef: value.checkListRef,
      envioEtica: value.envioEtica,
      presupuestoPorEntidades: value.presupuestoPorEntidades
    };
  }

  fromTarget(value: ISolicitudProyectoDatos): ISolicitudProyectoDatosBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoDatosBackend;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.fromTarget(value.solicitud),
      titulo: value.titulo,
      acronimo: value.acronimo,
      duracion: value.duracion,
      colaborativo: value.colaborativo,
      coordinadorExterno: value.coordinadorExterno,
      universidadSubcontratada: value.universidadSubcontratada,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      areaTematica: value.areaTematica,
      checkListRef: value.checkListRef,
      envioEtica: value.envioEtica,
      presupuestoPorEntidades: value.presupuestoPorEntidades
    };
  }
}

export const SOLICITUD_PROYECTO_DATOS_CONVERTER = new SolicitudProyectoDatosConverter();
