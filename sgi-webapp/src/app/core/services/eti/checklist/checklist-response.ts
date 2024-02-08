import { IFormlyNombre } from '@core/models/eti/formly-nombre';

export interface IChecklistResponse {
  id: number;
  personaRef: string;
  formly: {
    id: number;
    formlyNombres: IFormlyNombre[]
  };
  fechaCreacion: string;
  respuesta: {
    [name: string]: any;
  };
}
