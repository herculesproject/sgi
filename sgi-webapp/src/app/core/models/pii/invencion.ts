import { DateTime } from "luxon";

export interface IInvencion {
  id: number;
  titulo: string;
  fechaComunicacion: DateTime;
  descripcion: string;
  comentarios: string;
  activo: boolean;
}