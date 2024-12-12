import { IProyectoContextoBackend } from '@core/models/csp/backend/proyecto-contexto-backend';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { AREA_TEMATICA_RESPONSE_CONVERTER } from '@core/services/csp/area-tematica/area-tematica-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoContextoConverter extends SgiBaseConverter<IProyectoContextoBackend, IProyectoContexto> {

  toTarget(value: IProyectoContextoBackend): IProyectoContexto {
    if (!value) {
      return value as unknown as IProyectoContexto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados,
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.toTarget(value.areaTematica),
    };
  }

  fromTarget(value: IProyectoContexto): IProyectoContextoBackend {
    if (!value) {
      return value as unknown as IProyectoContextoBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados,
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.fromTarget(value.areaTematica),
    };
  }
}

export const PROYECTO_CONTEXTO_CONVERTER = new ProyectoContextoConverter();
