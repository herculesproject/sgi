import { ITipoActividad } from './tipo-actividad';
import { Persona } from '../sgp/persona';

export class PeticionEvaluacion extends Persona {

  /** ID */
  id: number;

  /** Solicitud convocatoria ref */
  solicitudConvocatoriaRef: string;

  /** Código */
  codigo: string;

  /** Título */
  titulo: string;

  /** Tipo de actividad */
  tipoActividad: ITipoActividad;

  /** Referencia fuente financiacion */
  fuenteFinanciacion: string;

  /** Fecha Inicio. */
  fechaInicio: Date;

  /** Fecha Fin. */
  fechaFin: Date;

  /** Resumen */
  resumen: string;

  /** Valor social */
  valorSocial: number;

  /** Otro valor social */
  otroValorSocial: string;

  /** Objetivos */
  objetivos: string;

  /** Diseño metodológico */
  disMetodologico: string;

  /** Externo */
  externo: boolean;

  /** Tiene fondos propios */
  tieneFondosPropios: boolean;

  /** Referencia persona */
  personaRef: string;

  /** Activo */
  activo: boolean;

  constructor() {
    super();
    this.id = null;
    this.solicitudConvocatoriaRef = null;
    this.codigo = null;
    this.titulo = null;
    this.tipoActividad = null;
    this.fuenteFinanciacion = null;
    this.fechaInicio = null;
    this.fechaFin = null;
    this.resumen = null;
    this.valorSocial = null;
    this.otroValorSocial = null;
    this.objetivos = null;
    this.disMetodologico = null;
    this.externo = null;
    this.tieneFondosPropios = null;
    this.personaRef = null;
    this.activo = true;
  }

}
