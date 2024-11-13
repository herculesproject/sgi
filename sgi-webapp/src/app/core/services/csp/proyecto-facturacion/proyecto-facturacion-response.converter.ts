import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FACTURACION_RESPONSE_CONVERTER } from '../tipo-facturacion/tipo-facturacion-response.converter';
import { IProyectoFacturacionResponse } from './proyecto-facturacion-response';

class ProyectoFacturacionResponseConverter extends
  SgiBaseConverter<IProyectoFacturacionResponse, IProyectoFacturacion> {

  toTarget(value: IProyectoFacturacionResponse): IProyectoFacturacion {

    return !value ? value as unknown as IProyectoFacturacion : {
      id: value.id,
      comentario: value.comentario,
      estadoValidacionIP: value.estadoValidacionIP,
      fechaConformidad: LuxonUtils.fromBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.fromBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      tipoFacturacion: TIPO_FACTURACION_RESPONSE_CONVERTER.toTarget(value.tipoFacturacion),
      proyectoProrroga: value.proyectoProrrogaId ? { id: value.proyectoProrrogaId } as IProyectoProrroga : null,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }

  fromTarget(value: IProyectoFacturacion): IProyectoFacturacionResponse {
    return !value ? value as unknown as IProyectoFacturacionResponse : {
      id: value.id,
      comentario: value.comentario,
      estadoValidacionIP: value.estadoValidacionIP,
      fechaConformidad: LuxonUtils.toBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      tipoFacturacion: TIPO_FACTURACION_RESPONSE_CONVERTER.fromTarget(value.tipoFacturacion),
      proyectoProrrogaId: value.proyectoProrroga?.id,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }

}

export const PROYECTO_FACTURACION_RESPONSE_CONVERTER = new ProyectoFacturacionResponseConverter();
