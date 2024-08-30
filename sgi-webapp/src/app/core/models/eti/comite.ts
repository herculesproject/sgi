export interface IComite {
  /** Id. */
  id: number;
  /** Código */
  codigo: string;
  /** Nombre  */
  nombre: string;
  /** Genero del nombre */
  genero: string;
  /** Formulario Memoria*/
  formularioMemoriaId: number;
  /** Formulario Seguimiento Anual */
  formularioSeguimientoAnualId: number;
  /** Formulario Seguimiento Final */
  formularioSeguimientoFinalId: number;
  /** Formulario Retrospectiva */
  formularioRetrospectivaId: number
  /** Requiere retrospectiva */
  requiereRetrospectiva: boolean;
  /** Prefijo en las memorias */
  prefijoReferencia: string;
  /** Permitir memorias de tipo ratificación */
  permitirRatificacion: boolean;
  /** Habilitar la creación de tareas con nombre libre */
  tareaNombreLibre: boolean;
  /** Habilitar la creación de tareas con experiencia libre */
  tareaExperienciaLibre: boolean;
  /** Habilitar la creación de tareas con detelle de experiencia */
  tareaExperienciaDetalle: boolean;
  /** Habilitar la creación de memorias con título libre */
  memoriaTituloLibre: boolean;
  /** Activo */
  activo: boolean;
}
