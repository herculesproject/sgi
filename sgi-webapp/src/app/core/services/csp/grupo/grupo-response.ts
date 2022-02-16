import { Tipo } from "@core/models/csp/grupo-tipo";

export interface IGrupoResponse {
  id: number;
  nombre: string;
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipo: {
    id: number;
    tipo: Tipo;
  };
  especialInvestigacionId: number;
  activo: boolean;
}
