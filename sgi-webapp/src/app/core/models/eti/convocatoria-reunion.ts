import { Comite } from './comite';
import { TipoConvocatoriaReunion } from './tipo-convocatoria-reunion';

export class ConvocatoriaReunion {
  /** Id */
  id: number;

  /** Comite */
  comite: Comite;

  /** Fecha Evaluación */
  fechaEvaluacion: Date;

  /** Fecha Límite */
  fechaLimite: Date;

  /** Lugar */
  lugar: string;

  /** Orden del día */
  ordenDia: string;

  /** Código */
  codigo: string;

  /** Tipo Convocatoria Reunión */
  tipoConvocatoriaReunion: TipoConvocatoriaReunion;

  /** Hora Inicio */
  horaInicio: number;

  /** Minuto Inicio */
  minutoInicio: number;

  /** Fecha Envío */
  fechaEnvio: Date;

  /** Control de borrado lógico */
  activo: boolean;

  constructor() {
    this.id = null;
    this.comite = new Comite();
    this.fechaEvaluacion = null;
    this.fechaLimite = null;
    this.lugar = '';
    this.ordenDia = '';
    this.codigo = '';
    this.tipoConvocatoriaReunion = new TipoConvocatoriaReunion();
    this.horaInicio = 0;
    this.minutoInicio = 0;
    this.fechaEnvio = null;
    this.activo = false;
  }
}
