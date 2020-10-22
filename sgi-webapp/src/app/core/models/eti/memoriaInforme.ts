import { IFormularioMemoria } from './formularioMemoria';

export interface IInformeFormulario {
  /** Id */
  id: number;

  /** Version */
  version: number;

  /** IFormularioMemoria */
  formularioMemoria: IFormularioMemoria;

  /** referencia */
  documentoRef: string;
}
