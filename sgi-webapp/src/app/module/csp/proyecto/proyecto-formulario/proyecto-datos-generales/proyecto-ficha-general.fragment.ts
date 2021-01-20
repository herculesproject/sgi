import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyecto } from '@core/models/csp/proyecto';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, EMPTY, of } from 'rxjs';
import { map, tap, switchMap, catchError } from 'rxjs/operators';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DateValidator } from '@core/validators/date-validator';
import { ProyectoActionService } from '../../proyecto.action.service';

export class ProyectoFichaGeneralFragment extends FormFragment<IProyecto> {

  private proyecto: IProyecto;

  selectedConvocatoria: IConvocatoria;

  abiertoRequired: boolean;
  comentarioEstadoCancelado: boolean;

  constructor(
    private fb: FormBuilder,
    private readonly logger: NGXLogger,
    key: number,
    private service: ProyectoService,
    private unidadGestionService: UnidadGestionService,
    private convocatoriaService: ConvocatoriaService,
    private actionService: ProyectoActionService
  ) {
    super(key);
    this.logger.debug(ProyectoFichaGeneralFragment.name, 'constructor()', 'start');
    this.proyecto = { activo: true } as IProyecto;
    this.logger.debug(ProyectoFichaGeneralFragment.name, 'constructor()', 'end');
  }

  protected initializer(key: number): Observable<IProyecto> {
    this.logger.debug(ProyectoFichaGeneralFragment.name, `initializer(key: ${key})`, 'start');
    return this.service.findById(key).pipe(
      tap(proyecto => {
        this.proyecto = proyecto;
      }),
      switchMap(() => {
        return this.loadUnidadGestion(this.proyecto.unidadGestion.acronimo);
      }),
      switchMap(() => {

        return this.proyecto.convocatoria ? this.loadConvocatoria(this.proyecto.convocatoria.id) : of(EMPTY);
      }),
      map(() => {
        this.logger.debug(ProyectoFichaGeneralFragment.name, `initializer(key: ${key})`, 'end');
        return this.proyecto;
      }),
      catchError(() => {
        this.logger.error(ProyectoFichaGeneralFragment.name, `initializer(key: ${key})`, 'error');
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoFichaGeneralFragment.name, `${this.buildFormGroup.name}()`, 'start');
    const form = new FormGroup({
      estado: new FormControl({
        value: '',
        disabled: true
      }),
      titulo: new FormControl('', [
        Validators.required, Validators.maxLength(200)]),
      acronimo: new FormControl('', [Validators.maxLength(50)]),
      codigoExterno: new FormControl(null, [Validators.maxLength(50)]),
      fechaInicio: new FormControl('', [
        Validators.required]),
      fechaFin: new FormControl('', [
        Validators.required]),
      convocatoria: new FormControl(''),
      convocatoriaExterna: new FormControl(null, [Validators.maxLength(200)]),
      unidadGestion: new FormControl('', [
        Validators.required, IsEntityValidator.isValid()]),
      modeloEjecucion: new FormControl('', [
        Validators.required, IsEntityValidator.isValid()]),
      finalidad: new FormControl(''),
      ambitoGeografico: new FormControl(''),
      confidencial: new FormControl(null),
      clasificacionCVN: new FormControl(''),
      colaborativo: new FormControl(null),
      coordinadorExterno: new FormControl(null),
      uniSubcontratada: new FormControl(null),
      timesheet: new FormControl(null),
      paquetesTrabajo: new FormControl(null),
      costeHora: new FormControl(null),
      contratacion: new FormControl(null),
      facturacion: new FormControl(null),
      iva: new FormControl(null),
      plantillaHojaFirma: new FormControl(null),
      justificacion: new FormControl(null),
      horasAnuales: new FormControl(''),
      observaciones: new FormControl(''),
      comentario: new FormControl({
        value: '',
        disabled: true
      }),
    },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin')]
      });

    this.subscriptions.push(
      form.controls.convocatoria.valueChanges.subscribe(
        (convocatoria) => this.onConvocatoriaChange(convocatoria)
      )
    );

    this.logger.debug(ProyectoFichaGeneralFragment.name, `${this.buildFormGroup.name}()`, 'end');

    return form;
  }

  buildPatch(proyecto: IProyecto): { [key: string]: any } {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `${this.buildPatch.name}(proyecto: ${proyecto})`, 'start');
    const result = {
      estado: proyecto.estado?.estado,
      titulo: proyecto.titulo,
      acronimo: proyecto.acronimo,
      codigoExterno: proyecto.codigoExterno,
      fechaInicio: proyecto.fechaInicio,
      fechaFin: proyecto.fechaFin,
      convocatoria: proyecto.convocatoria,
      convocatoriaExterna: proyecto.convocatoriaExterna,
      solicitud: proyecto.solicitud,
      unidadGestion: proyecto.unidadGestion,
      modeloEjecucion: proyecto.modeloEjecucion,
      finalidad: proyecto.finalidad,
      ambitoGeografico: proyecto.ambitoGeografico,
      confidencial: proyecto.confidencial,
      clasificacionCVN: proyecto.clasificacionCVN,
      colaborativo: proyecto.colaborativo,
      coordinadorExterno: proyecto.coordinadorExterno,
      uniSubcontratada: proyecto.uniSubcontratada,
      timesheet: proyecto.timesheet,
      paquetesTrabajo: proyecto.paquetesTrabajo,
      costeHora: proyecto.costeHora,
      contratacion: proyecto.contratos,
      facturacion: proyecto.facturacion,
      iva: proyecto.iva,
      plantillaHojaFirma: proyecto.plantillaHojaFirma,
      justificacion: proyecto.plantillaJustificacion,
      horasAnuales: proyecto.tipoHorasAnuales,
      observaciones: proyecto.observaciones,
      comentario: proyecto.estado?.comentario
    };

    this.actionService.isProyectoColaborativo = proyecto.colaborativo;
    this.checkEstado(this.getFormGroup(), proyecto);

    if (proyecto.convocatoria && this.isEdit()) {
      this.getFormGroup().controls.convocatoriaExterna.disable();
    }

    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `${this.buildPatch.name}(proyecto: ${proyecto})`, 'end');
    return result;
  }

  getValue(): IProyecto {
    this.logger.debug(ProyectoFichaGeneralFragment.name, `${this.getValue.name}()`, 'start');
    const form = this.getFormGroup().controls;
    this.proyecto.titulo = form.titulo.value;
    this.proyecto.acronimo = form.acronimo.value;
    this.proyecto.codigoExterno = form.acronimo.value;
    this.proyecto.fechaInicio = form.fechaInicio.value;
    this.proyecto.fechaFin = form.fechaFin.value;
    this.proyecto.convocatoria = form.convocatoria.value;
    if (form.convocatoria.value) {
      this.proyecto.convocatoriaExterna = undefined;
    } else {
      this.proyecto.convocatoriaExterna = form.convocatoriaExterna.value;
    }
    this.proyecto.solicitud = null;
    this.proyecto.unidadGestion = form.unidadGestion.value;
    this.proyecto.modeloEjecucion = form.modeloEjecucion.value;

    if (form.finalidad.value) {
      this.proyecto.finalidad = form.finalidad.value;
    } else {
      this.proyecto.finalidad = undefined;
    }

    if (form.ambitoGeografico.value) {
      this.proyecto.ambitoGeografico = form.ambitoGeografico.value;
    } else {
      this.proyecto.ambitoGeografico = undefined;
    }
    this.proyecto.confidencial = form.confidencial.value;
    if (form.clasificacionCVN.value) {
      this.proyecto.clasificacionCVN = form.clasificacionCVN.value;
    } else {
      this.proyecto.clasificacionCVN = undefined;
    }
    this.proyecto.colaborativo = form.colaborativo.value;
    this.proyecto.uniSubcontratada = form.uniSubcontratada.value;
    this.proyecto.timesheet = form.timesheet.value;
    this.proyecto.paquetesTrabajo = form.paquetesTrabajo.value;
    this.proyecto.costeHora = form.costeHora.value;
    this.proyecto.contratos = form.contratacion.value;
    this.proyecto.facturacion = form.facturacion.value;
    this.proyecto.iva = form.iva.value;
    this.proyecto.plantillaHojaFirma = form.plantillaHojaFirma.value;
    this.proyecto.plantillaJustificacion = form.justificacion.value;
    if (form.horasAnuales.value?.length > 0) {
      this.proyecto.tipoHorasAnuales = form.horasAnuales.value;
    } else {
      this.proyecto.tipoHorasAnuales = undefined;
    }

    this.proyecto.comentario = form.comentario.value;
    this.proyecto.observaciones = form.observaciones.value;

    this.proyecto.coordinadorExterno = form.coordinadorExterno.value;

    this.logger.debug(ProyectoFichaGeneralFragment.name, `${this.getValue.name}()`, 'end');
    return this.proyecto;
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta
   *
   * @param convocatoria una convocatoria
   */
  private onConvocatoriaChange(convocatoria: IConvocatoria): void {
    this.logger.debug(ProyectoFichaGeneralFragment.name, `setConvocatoria(${convocatoria})`, 'start');

    if (convocatoria) {
      this.getFormGroup().controls.convocatoriaExterna.setValue('', { emitEvent: false });

      this.subscriptions.push(
        this.unidadGestionService.findByAcronimo(convocatoria.unidadGestionRef).subscribe(unidadGestion => {
          this.getFormGroup().controls.unidadGestion.setValue(unidadGestion);
        })
      );
      this.getFormGroup().controls.convocatoriaExterna.disable();
    } else if (!this.isEdit()) {
      // Clean dependencies
      this.getFormGroup().controls.unidadGestion.setValue(null);
      // Enable fields
      this.getFormGroup().controls.unidadGestion.enable();
      this.getFormGroup().controls.convocatoriaExterna.enable();
    }

    this.logger.debug(ProyectoFichaGeneralFragment.name, `setConvocatoria(${convocatoria})`, 'end');
  }

  /**
  * Añade validadores al formulario dependiendo del estado del proyecto
  */
  private checkEstado(formgroup: FormGroup, proyecto: IProyecto): void {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `checkEstado(formgroup: ${formgroup}, proyecto: ${proyecto})`, 'start');
    if (proyecto.estado.estado === TipoEstadoProyecto.ABIERTO) {
      formgroup.get('finalidad').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('ambitoGeografico').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('confidencial').setValidators([
        Validators.required]);
      formgroup.get('colaborativo').setValidators([
        Validators.required]);
      formgroup.get('coordinadorExterno').setValidators([
        Validators.required]);
      formgroup.get('timesheet').setValidators([
        Validators.required]);
      formgroup.get('paquetesTrabajo').setValidators([
        Validators.required]);
      formgroup.get('costeHora').setValidators([
        Validators.required]);
      formgroup.get('contratacion').setValidators([
        Validators.required]);
      formgroup.get('facturacion').setValidators([
        Validators.required]);
      formgroup.get('iva').setValidators([
        Validators.required]);
      this.abiertoRequired = true;
      this.comentarioEstadoCancelado = false;
    } else if (proyecto.estado.estado === TipoEstadoProyecto.CANCELADO) {
      this.comentarioEstadoCancelado = true;
      this.getFormGroup().disable();
    } else if (proyecto.estado.estado === TipoEstadoProyecto.FINALIZADO) {
      this.comentarioEstadoCancelado = false;
      this.getFormGroup().disable();
    } else {
      formgroup.get('finalidad').setValidators(IsEntityValidator.isValid());
      formgroup.get('ambitoGeografico').setValidators(IsEntityValidator.isValid());
      this.abiertoRequired = false;
      this.comentarioEstadoCancelado = false;
    }
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `checkEstado(formgroup: ${formgroup}, proyecto: ${proyecto})`, 'end');
  }

  get requiredAbierto() {
    return this.proyecto.estado.estado === TipoEstadoProyecto.ABIERTO;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoFichaGeneralFragment.name, 'saveOrUpdate()', 'start');
    const fichaGeneral = this.getValue();
    const obs = fichaGeneral.id ? this.update(fichaGeneral) :
      this.create(fichaGeneral);
    return obs.pipe(
      map((value) => {
        this.proyecto = value;
        this.logger.debug(ProyectoFichaGeneralFragment.name, 'saveOrUpdate()', 'end');
        return this.proyecto.id;
      })
    );
  }

  private create(proyecto: IProyecto): Observable<IProyecto> {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `${this.create.name}(proyecto: ${proyecto})`, 'start');
    return this.service.create(proyecto).pipe(
      tap(result => this.proyecto = result),
      tap(() => this.logger.debug(ProyectoFichaGeneralFragment.name,
        `${this.create.name}(proyecto: ${proyecto})`, 'end'))
    );
  }

  private update(proyecto: IProyecto): Observable<IProyecto> {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `${this.update.name}(proyecto: ${proyecto})`, 'start');
    return this.service.update(Number(this.getKey()), proyecto).pipe(
      tap(result => this.proyecto = result),
      tap(() => this.logger.debug(ProyectoFichaGeneralFragment.name,
        `${this.update.name}(proyecto: ${proyecto})`, 'end'))
    );
  }

  /**
   * Carga los datos de la unidad de gestion en la proyecto
   *
   * @param acronimo Identificador de la unidad de gestion
   * @returns observable para recuperar los datos
   */
  private loadUnidadGestion(acronimo: string): Observable<SgiRestListResult<IUnidadGestion>> {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `loadUnidadGestion(acronimo: ${acronimo})`, 'start');
    const options = {
      filters: [
        {
          field: 'acronimo',
          type: SgiRestFilterType.EQUALS,
          value: acronimo,
        } as SgiRestFilter
      ]
    } as SgiRestFindOptions;

    return this.unidadGestionService.findAll(options).pipe(
      tap(result => {
        if (result.items.length > 0) {
          this.proyecto.unidadGestion = result.items[0];
          this.getFormGroup().controls.unidadGestion.setValue(this.proyecto.unidadGestion);
        }
        this.logger.debug(ProyectoFichaGeneralFragment.name,
          `loadUnidadGestion(acronimo: ${acronimo})`, 'end');
      })
    );

  }

  /**
   * Carga los datos de la convocatoria en la solicitud
   *
   * @param solicitanteRef Identificador del solicitante
   * @returns observable para recuperar los datos
   */
  private loadConvocatoria(convocatoriaId: number): Observable<IConvocatoria> {
    this.logger.debug(ProyectoFichaGeneralFragment.name,
      `loadConvocatoria(convocatoriaId: ${convocatoriaId})`, 'start');

    return this.convocatoriaService.findById(convocatoriaId).pipe(
      tap(convocatoria => {
        this.proyecto.convocatoria = convocatoria;
        this.selectedConvocatoria = this.proyecto.convocatoria;
        this.logger.debug(ProyectoFichaGeneralFragment.name,
          `loadConvocatoria(convocatoriaId: ${convocatoriaId})`, 'end');
      })
    );
  }

}
