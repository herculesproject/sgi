import { IFormlyNombre } from '@core/models/eti/formly-nombre';

export interface IFormlyResponse {
  id: number;
  version: number;
  formlyNombres: IFormlyNombre[];
}
