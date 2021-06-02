import { TipoPartida } from "../convocatoria-partida-presupuestaria";

export interface IConvocatoriaPartidaPresupuestariaBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Código  */
  codigo: string;
  /** Descripción */
  descripcion: string;
  /** Tipo de partida */
  tipoPartida: TipoPartida;

}
