import { IFormulario } from '@core/models/eti/formulario';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFormularioResponse } from './formulario-response';

class FormularioResponseConverter
  extends SgiBaseConverter<IFormularioResponse, IFormulario> {
  toTarget(value: IFormularioResponse): IFormulario {
    if (!value) {
      return value as unknown as IFormulario;
    }
    return {
      activo: value.activo,
      descripcion: value.descripcion,
      id: value.id,
      nombre: value.nombre
    };
  }

  fromTarget(value: IFormulario): IFormularioResponse {
    if (!value) {
      return value as unknown as IFormularioResponse;
    }
    return {
      activo: value.activo,
      descripcion: value.descripcion,
      id: value.id,
      nombre: value.nombre
    };
  }
}

export const FORMULARIO_RESPONSE_CONVERTER = new FormularioResponseConverter();
