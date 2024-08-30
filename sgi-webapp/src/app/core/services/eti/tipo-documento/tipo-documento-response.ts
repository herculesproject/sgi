export interface ITipoDocumentoResponse {
  /** Id */
  id: number;
  /** Codigo */
  codigo: string;
  /** Nombre */
  nombre: string;
  /** Formulario Id */
  formularioId: number;
  /** Documentación adicional */
  adicional: boolean;
  /** Activo */
  activo: boolean;
}