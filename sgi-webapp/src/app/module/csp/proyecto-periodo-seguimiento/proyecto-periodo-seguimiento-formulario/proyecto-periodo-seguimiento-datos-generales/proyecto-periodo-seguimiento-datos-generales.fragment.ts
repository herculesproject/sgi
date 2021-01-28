import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { tap, map } from 'rxjs/operators';
import { IProyecto } from '@core/models/csp/proyecto';
import { DateValidator } from '@core/validators/date-validator';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';

export class ProyectoPeriodoSeguimientoDatosGeneralesFragment extends FormFragment<IProyectoPeriodoSeguimiento> {
  proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento;

  constructor(
    private logger: NGXLogger,
    key: number,
    private service: ProyectoPeriodoSeguimientoService,
    private proyecto: IProyecto,
    public selectedProyectoPeriodoSeguimientos: IProyectoPeriodoSeguimiento[],
    private readonly
  ) {
    super(key);
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, 'constructor()', 'start');
    this.proyectoPeriodoSeguimiento = {
      proyecto: this.proyecto
    } as IProyectoPeriodoSeguimiento;
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.getLastNumPeriodo() ? this.getLastNumPeriodo() + 1 : 1,
          disabled: true
        }),
        fechaInicio: new FormControl('', [
          Validators.required
        ]),
        fechaFin: new FormControl('', [
          Validators.required
        ]),
        fechaInicioPresentacion: new FormControl(''),
        fechaFinPresentacion: new FormControl(''),
        observaciones: new FormControl('', [Validators.maxLength(250)]),
        fechaInicioProyecto: new FormControl(this.proyecto?.fechaInicio),
        fechaFinProyecto: new FormControl(this.proyecto?.fechaFin)
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfter('fechaInicioPresentacion', 'fechaFinPresentacion'),
          DateValidator.isAfterOrEqual('fechaInicioProyecto', 'fechaInicio'),
          DateValidator.isBefore('fechaFinProyecto', 'fechaFin')
        ]
      }
    );

    if (this.proyecto?.estado?.estado === TipoEstadoProyecto.ABIERTO) {
      form.controls.fechaInicioPresentacion.setValidators([Validators.required]);
      form.controls.fechaFinPresentacion.setValidators([Validators.required]);
    } else {
      form.controls.fechaInicioPresentacion.setValidators(null);
      form.controls.fechaFinPresentacion.setValidators(null);
    }

    if (this.readonly) {
      form.disable();
    }

    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): { [key: string]: any; } {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoPeriodoSeguimiento})`, 'start');
    const result = {
      numPeriodo: proyectoPeriodoSeguimiento.numPeriodo,
      fechaInicio: proyectoPeriodoSeguimiento.fechaInicio,
      fechaFin: proyectoPeriodoSeguimiento.fechaFin,
      fechaInicioPresentacion: proyectoPeriodoSeguimiento.fechaInicioPresentacion,
      fechaFinPresentacion: proyectoPeriodoSeguimiento.fechaFinPresentacion,
      observaciones: proyectoPeriodoSeguimiento.observaciones
    };
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoPeriodoSeguimiento})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IProyectoPeriodoSeguimiento> {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.service.findById(key)
      .pipe(
        tap((proyectoPeriodoSeguimiento) => this.proyectoPeriodoSeguimiento = proyectoPeriodoSeguimiento),
        tap(() => this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
          `initializer(key: ${key})`, 'end'))
      );
  }

  getValue(): IProyectoPeriodoSeguimiento {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.proyectoPeriodoSeguimiento.numPeriodo = form.numPeriodo.value;
    this.proyectoPeriodoSeguimiento.fechaInicio = form.fechaInicio.value;
    this.proyectoPeriodoSeguimiento.fechaFin = form.fechaFin.value;
    this.proyectoPeriodoSeguimiento.fechaInicioPresentacion = form.fechaInicioPresentacion.value;
    this.proyectoPeriodoSeguimiento.fechaFinPresentacion = form.fechaFinPresentacion.value;
    this.proyectoPeriodoSeguimiento.observaciones = form.observaciones.value;
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.proyectoPeriodoSeguimiento;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const proyectoPeriodoSeguimiento = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoPeriodoSeguimiento) : this.create(proyectoPeriodoSeguimiento);
    return observable$.pipe(
      map(result => {
        this.proyectoPeriodoSeguimiento = result;
        return this.proyectoPeriodoSeguimiento.id;
      }),
      tap(() => this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): Observable<IProyectoPeriodoSeguimiento> {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
      `create(proyectoPeriodoSeguimiento: ${proyectoPeriodoSeguimiento})`, 'start');

    return this.service.create(proyectoPeriodoSeguimiento)
      .pipe(
        tap(() => this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
          `create(proyectoPeriodoSeguimiento: ${proyectoPeriodoSeguimiento})`, 'end'))
      );
  }

  private update(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): Observable<IProyectoPeriodoSeguimiento> {
    this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
      `update(proyectoPeriodoSeguimiento: ${proyectoPeriodoSeguimiento})`, 'start');

    return this.service.update(proyectoPeriodoSeguimiento.id, proyectoPeriodoSeguimiento)
      .pipe(
        tap(() => this.logger.debug(ProyectoPeriodoSeguimientoDatosGeneralesFragment.name,
          `update(proyectoPeriodoSeguimiento: ${proyectoPeriodoSeguimiento})`, 'end'))
      );
  }

  /**
   * Obtiene el último número de período de la tabla de periodos de seguimiento científico
   */
  getLastNumPeriodo(): number {
    if (this.selectedProyectoPeriodoSeguimientos && this.selectedProyectoPeriodoSeguimientos.length > 0) {
      this.selectedProyectoPeriodoSeguimientos
        .sort((a, b) => (a.fechaInicio > b.fechaInicio) ? 1 : ((b.fechaInicio > a.fechaInicio) ? -1 : 0));

      return this.selectedProyectoPeriodoSeguimientos[this.selectedProyectoPeriodoSeguimientos.length - 1].numPeriodo;
    }
    return null;
  }
}
