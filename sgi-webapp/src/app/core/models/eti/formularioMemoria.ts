import { MemoriaActionService } from 'src/app/module/eti/memoria/memoria.action.service';
import { IMemoria } from './memoria';

export interface IFormularioMemoria {
  /** Id */
  id: number;

  /** Memoria */
  memoria: IMemoria;

  activo: boolean;
}
