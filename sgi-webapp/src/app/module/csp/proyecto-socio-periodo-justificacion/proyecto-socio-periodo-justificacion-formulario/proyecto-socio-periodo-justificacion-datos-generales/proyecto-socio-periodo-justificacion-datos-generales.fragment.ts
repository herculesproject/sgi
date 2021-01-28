import { FormFragment } from '@core/services/action-service';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { NGXLogger } from 'ngx-logger';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { tap, map } from 'rxjs/operators';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { DateValidator } from '@core/validators/date-validator';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';

export class ProyectoSocioPeriodoJustificacionDatosGeneralesFragment extends FormFragment<IProyectoSocioPeriodoJustificacion> {

  constructor(
    private logger: NGXLogger,
    key: number,
    private service: ProyectoSocioPeriodoJustificacionService,
    private proyectoSocio: IProyectoSocio,
    private periodoJustificacion: IProyectoSocioPeriodoJustificacion,
    private selectedPeriodosJustificacion: IProyectoSocioPeriodoJustificacion[]
  ) {
    super(key);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, 'constructor()', 'start');
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const rangosExistentes = this.selectedPeriodosJustificacion.map(
      periodo => {
        const value: IRange = {
          inicio: new Date(periodo.fechaInicio),
          fin: new Date(periodo.fechaFin)
        };
        return value;
      }
    );
    const proyecto = this.proyectoSocio.proyecto;
    const fechaInicio = typeof proyecto?.fechaInicio === 'string' ? new Date(proyecto?.fechaInicio) : proyecto?.fechaInicio;
    const fechaFin = typeof proyecto?.fechaFin === 'string' ? new Date(proyecto?.fechaFin) : proyecto?.fechaFin;
    const form = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.periodoJustificacion.numPeriodo,
          disabled: true
        }),
        fechaInicio: new FormControl('', [
          Validators.required,
          DateValidator.minDate(fechaInicio),
          DateValidator.maxDate(fechaFin)
        ]),
        fechaFin: new FormControl('', [
          Validators.required,
          DateValidator.minDate(fechaInicio),
          DateValidator.maxDate(fechaFin)
        ]),
        fechaInicioPresentacion: new FormControl(''),
        fechaFinPresentacion: new FormControl(''),
        observaciones: new FormControl('', [Validators.maxLength(2_000)]),
        documentacionRecibida: new FormControl(false),
        fechaRecepcion: new FormControl(''),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfter('fechaInicioPresentacion', 'fechaFinPresentacion'),
          DateValidator.rangeWithTime('fechaInicioPresentacion', 'fechaInicio', 'fechaFin'),
          DateValidator.rangeWithTime('fechaFinPresentacion', 'fechaInicio', 'fechaFin'),
          RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangosExistentes),
        ]
      }
    );
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(value: IProyectoSocioPeriodoJustificacion): { [key: string]: any; } {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
      `buildPatch(value: ${value})`, 'start');
    const result = {
      documentacionRecibida: value.documentacionRecibida,
      fechaFin: value.fechaFin ? new Date(value.fechaFin) : undefined,
      fechaFinPresentacion: value.fechaFinPresentacion,
      fechaInicio: value.fechaInicio ? new Date(value.fechaInicio) : undefined,
      fechaInicioPresentacion: value.fechaInicioPresentacion,
      fechaRecepcion: value.fechaRecepcion ? new Date(value.fechaRecepcion) : undefined,
      observaciones: value.observaciones,
      numPeriodo: value.numPeriodo
    };
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
      `buildPatch(value: ${value})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IProyectoSocioPeriodoJustificacion> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return of(this.periodoJustificacion).pipe(
      tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
        `initializer(key: ${key})`, 'end'))
    );
  }

  getValue(): IProyectoSocioPeriodoJustificacion {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.periodoJustificacion.documentacionRecibida = form.documentacionRecibida.value;
    this.periodoJustificacion.fechaFin = form.fechaFin.value;
    this.periodoJustificacion.fechaFinPresentacion = form.fechaFinPresentacion.value;
    this.periodoJustificacion.fechaInicio = form.fechaInicio.value;
    this.periodoJustificacion.fechaInicioPresentacion = form.fechaInicioPresentacion.value;
    this.periodoJustificacion.fechaRecepcion = this.periodoJustificacion.documentacionRecibida ? form.fechaRecepcion.value : undefined;
    this.periodoJustificacion.observaciones = form.observaciones.value;
    this.periodoJustificacion.numPeriodo = form.numPeriodo.value;
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.periodoJustificacion;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const periodo = this.getValue();
    periodo.proyectoSocio = this.proyectoSocio;
    const observable$ = this.isEdit() ? this.update(periodo) : this.create(periodo);
    return observable$.pipe(
      map(result => {
        this.periodoJustificacion = result;
        this.setChanges(false);
        this.refreshInitialState(true);
        return this.periodoJustificacion.id;
      }),
      tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(periodoJustificacion: IProyectoSocioPeriodoJustificacion): Observable<IProyectoSocioPeriodoJustificacion> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
      `create(periodoJustificacion: ${periodoJustificacion})`, 'start');
    return this.service.create(periodoJustificacion).pipe(
      tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
        `create(periodoJustificacion: ${periodoJustificacion})`, 'end'))
    );
  }

  private update(periodoJustificacion: IProyectoSocioPeriodoJustificacion): Observable<IProyectoSocioPeriodoJustificacion> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
      `update(periodoJustificacion: ${periodoJustificacion})`, 'start');
    return this.service.update(Number(this.getKey()), periodoJustificacion).pipe(
      tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesFragment.name,
        `update(periodoJustificacion: ${periodoJustificacion})`, 'end'))
    );
  }

  get isAbierto(): boolean {
    const proyecto = this.proyectoSocio.proyecto;
    return proyecto?.estado?.estado === TipoEstadoProyecto.ABIERTO;
  }
}
