import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaConverter extends SgiBaseConverter<IConvocatoriaBackend, IConvocatoria> {

  toTarget(value: IConvocatoriaBackend): IConvocatoria {
    if (!value) {
      return value as unknown as IConvocatoria;
    }
    return {
      id: value.id,
      unidadGestion: { acronimo: value.unidadGestionRef } as IUnidadGestion,
      modeloEjecucion: value.modeloEjecucion,
      codigo: value.codigo,
      anio: value.anio,
      titulo: value.titulo,
      objeto: value.objeto,
      observaciones: value.observaciones,
      finalidad: value.finalidad,
      regimenConcurrencia: value.regimenConcurrencia,
      colaborativos: value.colaborativos,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: value.ambitoGeografico,
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo
    };
  }

  fromTarget(value: IConvocatoria): IConvocatoriaBackend {
    if (!value) {
      return value as unknown as IConvocatoriaBackend;
    }
    return {
      id: value.id,
      unidadGestionRef: value.unidadGestion?.acronimo,
      modeloEjecucion: value.modeloEjecucion,
      codigo: value.codigo,
      anio: value.anio,
      titulo: value.titulo,
      objeto: value.objeto,
      observaciones: value.observaciones,
      finalidad: value.finalidad,
      regimenConcurrencia: value.regimenConcurrencia,
      colaborativos: value.colaborativos,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: value.ambitoGeografico,
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo
    };
  }
}

export const CONVOCATORIA_CONVERTER = new ConvocatoriaConverter();
