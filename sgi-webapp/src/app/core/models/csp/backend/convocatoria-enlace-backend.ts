import { ITipoEnlaceResponse } from "@core/services/csp/tipo-enlace/tipo-enlace-response";

export interface IConvocatoriaEnlaceBackend {
  /** id */
  id: number;
  /** tipoEnlace */
  tipoEnlace: ITipoEnlaceResponse;
  /** activo */
  activo: boolean;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** URL */
  url: string;
  /** descripcion */
  descripcion: string;
}
