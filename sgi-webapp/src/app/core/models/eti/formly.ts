import { IFormlyNombre } from './formly-nombre';

export interface IFormly {
  id: number;
  version: number;
  formlyNombres: IFormlyNombre[];
}
