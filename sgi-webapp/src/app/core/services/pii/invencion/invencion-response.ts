import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { ITipoProteccionResponse } from '../tipo-proteccion/tipo-proteccion-response';

export interface IInvencionResponse {
  id: number;
  titulo: string;
  fechaComunicacion: string;
  descripcion: string;
  comentarios: string;
  proyectoRef: string;
  tipoProteccion: ITipoProteccionResponse;
  activo: boolean;
}