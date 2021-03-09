import { IProyectoContextoBackend } from '@core/models/csp/backend/proyecto-contexto-backend';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_CONVERTER } from './proyecto.converter';

class ProyectoContextoConverter extends SgiBaseConverter<IProyectoContextoBackend, IProyectoContexto> {

  toTarget(value: IProyectoContextoBackend): IProyectoContexto {
    if (!value) {
      return value as unknown as IProyectoContexto;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.toTarget(value.proyecto),
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados,
      areaTematica: value.areaTematica,
      areaTematicaConvocatoria: value.areaTematicaConvocatoria
    };
  }

  fromTarget(value: IProyectoContexto): IProyectoContextoBackend {
    if (!value) {
      return value as unknown as IProyectoContextoBackend;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.fromTarget(value.proyecto),
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados,
      areaTematica: value.areaTematica,
      areaTematicaConvocatoria: value.areaTematicaConvocatoria
    };
  }
}

export const PROYECTO_CONTEXTO_CONVERTER = new ProyectoContextoConverter();
