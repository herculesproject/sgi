import { Servicio } from './servicio';
import { EstadoTipoReservableEnum } from '@core/enums/estado-tipo-reservable-enum';

export class TipoReservable {

  /** ID */
  id: number;
  /** Descripción */
  descripcion: string;
  /** Servicio */
  servicio: Servicio;
  /** Duración mínima en minutos */
  duracionMin: number;
  /** Días de antelación */
  diasAnteMax: number;
  /** Reserva múltiple */
  reservaMulti: boolean;
  /** Vista máxima de calendario en días. */
  diasVistaMaxCalen: number;
  /** Hora antelación mínima. */
  horasAnteMin: number;
  /** Horas antelación anular usuario. */
  horasAnteAnular: number;
  /** Estado */
  estado: EstadoTipoReservableEnum;
  /** Control de borrado lógico */
  activo: boolean;

  constructor() {
    this.id = null;
    this.descripcion = '';
    this.servicio = null;
    this.duracionMin = null;
    this.diasAnteMax = null;
    this.reservaMulti = null;
    this.diasVistaMaxCalen = null;
    this.horasAnteMin = null;
    this.horasAnteAnular = null;
    this.estado = null;
    this.activo = true;
  }

}
