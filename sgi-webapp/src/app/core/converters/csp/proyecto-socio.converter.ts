import { IProyectoSocioBackend } from '@core/models/csp/backend/proyecto-socio-backend';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_CONVERTER } from './proyecto.converter';

class ProyectoSocioConverter extends SgiBaseConverter<IProyectoSocioBackend, IProyectoSocio> {

  toTarget(value: IProyectoSocioBackend): IProyectoSocio {
    if (!value) {
      return value as unknown as IProyectoSocio;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.toTarget(value.proyecto),
      empresa: { personaRef: value.empresaRef } as IEmpresaEconomica,
      rolSocio: value.rolSocio,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido
    };
  }

  fromTarget(value: IProyectoSocio): IProyectoSocioBackend {
    if (!value) {
      return value as unknown as IProyectoSocioBackend;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.fromTarget(value.proyecto),
      empresaRef: value.empresa.personaRef,
      rolSocio: value.rolSocio,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido
    };
  }
}

export const PROYECTO_SOCIO_CONVERTER = new ProyectoSocioConverter();
