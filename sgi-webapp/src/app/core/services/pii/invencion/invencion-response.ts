export interface IInvencionResponse {
  id: number;
  titulo: string;
  fechaComunicacion: string;
  descripcion: string;
  comentarios: string;
  proyectoRef: string;
  tipoProteccion: {
    id: number;
    nombre: string;
    padre: {
      id: number;
      nombre: string;
    }
  }
  activo: boolean;
}