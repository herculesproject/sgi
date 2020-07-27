import { Comite } from './comite';
import { TipoConvocatoriaReunion } from './tipo-convocatoria-reunion';

export class ConvocatoriaReunion {

  /** ID */
  id: number;
  /** Comite */
  comite: Comite;
  /** Tipo Convocatoria Reunion */
  tipoConvocatoriaReunion: TipoConvocatoriaReunion;
  /** Fecha evaluación */
  fechaEvaluacion: Date;
  /** Hora fin */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Fecha Limite */
  fechaLimite: Date;
  /** Lugar */
  lugar: string;
  /** Orden día */
  ordenDia: string;
  /** Código   */
  codigo: string;
  /** Fecha Envío */
  fechaEnvio: Date;
  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.comite = null;
    this.tipoConvocatoriaReunion = null;
    this.fechaEvaluacion = null;
    this.horaInicio = null;
    this.minutoInicio = null;
    this.fechaLimite = null;
    this.lugar = null;
    this.ordenDia = null;
    this.codigo = null;
    this.fechaEnvio = null;
    this.activo = true;
  }

}
