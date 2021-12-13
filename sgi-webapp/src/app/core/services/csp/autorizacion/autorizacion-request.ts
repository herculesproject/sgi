
export interface IAutorizacionRequest {
  observaciones: string;
  responsableRef: string;
  solitanteRef: string;
  tituloProyecto: string;
  entidadRef: string;
  horasDedicacion: number;
  datosResponsable: string;
  datosEntidad: string;
  datosConvocatoria: string;
  convocatoriaId: number;
  estadoId: number;
}
