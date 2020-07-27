import { TipoActividad } from './tipo-actividad';

export class PeticionEvaluacion {

  /** ID */
  id: number;

  /** Solicitud convocatoria ref */
  solicitudConvocatoriaRef: string;

  /** Código */
  codigo: string;

  /** Título */
  titulo: string;

  /** Tipo de actividad */
  tipoActividad: TipoActividad;

  /** Referencia fuente financiacion */
  fuenteFinanciacionRef: string;

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

  /** Referencia usuario */
  usuarioRef: string;

  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.solicitudConvocatoriaRef = null;
    this.codigo = null;
    this.titulo = null;
    this.tipoActividad = null;
    this.fuenteFinanciacionRef = null;
    this.fechaInicio = null;
    this.fechaFin = null;
    this.resumen = null;
    this.valorSocial = null;
    this.otroValorSocial = null;
    this.objetivos = null;
    this.disMetodologico = null;
    this.externo = null;
    this.tieneFondosPropios = null;
    this.usuarioRef = null;
    this.activo = true;
  }

}
