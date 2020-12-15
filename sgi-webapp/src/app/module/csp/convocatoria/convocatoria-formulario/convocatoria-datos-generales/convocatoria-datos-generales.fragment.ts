import { FormFragment } from '@core/services/action-service';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, EMPTY, BehaviorSubject, of, from, merge } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaEntidadGestoraService } from '@core/services/csp/convocatoria-entidad-gestora.service';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions } from '@sgi/framework/http';
import { EnumValidador } from '@core/validators/enum-validador';
import { TipoEstadoConvocatoria } from '@core/enums/tipo-estado-convocatoria';
import { IsYearPlus } from '@core/validators/is-year-plus';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoDestinatario } from '@core/enums/tipo-destinatario';
import { ConvocatoriaAreaTematicaService } from '@core/services/csp/convocatoria-area-tematica.service';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';

export interface AreaTematicaData {
  padre: IAreaTematica;
  convocatoriaAreaTematica: StatusWrapper<IConvocatoriaAreaTematica>;
  observaciones: string;
}

export class ConvocatoriaDatosGeneralesFragment extends FormFragment<IConvocatoria> {
  areasTematicas$ = new BehaviorSubject<AreaTematicaData[]>([]);
  private convocatoriaAreaTematicaEliminadas: StatusWrapper<IConvocatoriaAreaTematica>[] = [];

  convocatoria: IConvocatoria;
  private convocatoriaEntidadGestora: IConvocatoriaEntidadGestora;

  constructor(
    private logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private empresaEconomicaService: EmpresaEconomicaService,
    private convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    private unidadGestionService: UnidadGestionService,
    private convocatoriaAreaTematicaService: ConvocatoriaAreaTematicaService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'constructor()', 'start');
    this.convocatoria = {
      activo: true,
      estadoActual: TipoEstadoConvocatoria.BORRADOR
    } as IConvocatoria;
    this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'constructor()', 'start');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup({
      codigo: new FormControl('', [
        Validators.required, Validators.maxLength(50)]),
      estadoActual: new FormControl({
        value: TipoEstadoConvocatoria.BORRADOR,
        disabled: true
      }),
      unidadGestion: new FormControl('', [
        Validators.required, IsEntityValidator.isValid()]),
      anio: new FormControl(new Date().getFullYear(), [
        Validators.required,
        Validators.min(1000),
        Validators.max(9999),
        IsYearPlus.isValid(1)
      ]),
      titulo: new FormControl('', [
        Validators.required, Validators.maxLength(250)]),
      modeloEjecucion: new FormControl(''),
      finalidad: new FormControl(''),
      duracion: new FormControl('', [
        Validators.min(1), Validators.max(9999)]),
      ambitoGeografico: new FormControl(''),
      clasificacionCVN: new FormControl('',
        EnumValidador.isValid(ClasificacionCVN)),
      regimenConcurrencia: new FormControl(''),
      colaborativos: new FormControl(null),
      destinatarios: new FormControl(
        '', [Validators.required,
        EnumValidador.isValid(TipoDestinatario)]),
      entidadGestora: new FormControl(''),
      objeto: new FormControl('',
        Validators.maxLength(2000)),
      observaciones: new FormControl('',
        Validators.maxLength(2000))
    });
    if (this.readonly) {
      form.disable();
    }
    this.checkEstado(form, this.convocatoria);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  buildPatch(convocatoria: IConvocatoria): { [key: string]: any } {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `buildPatch(convocatoria: ${convocatoria})`, 'start');
    const result = {
      modeloEjecucion: convocatoria.modeloEjecucion,
      codigo: convocatoria.codigo,
      anio: convocatoria.anio,
      titulo: convocatoria.titulo,
      objeto: convocatoria.objeto,
      observaciones: convocatoria.observaciones,
      finalidad: convocatoria.finalidad,
      regimenConcurrencia: convocatoria.regimenConcurrencia,
      destinatarios: convocatoria.destinatarios,
      colaborativos: convocatoria.colaborativos,
      estadoActual: convocatoria.estadoActual,
      duracion: convocatoria.duracion,
      ambitoGeografico: convocatoria.ambitoGeografico,
      clasificacionCVN: convocatoria.clasificacionCVN,
    };
    this.checkEstado(this.getFormGroup(), convocatoria);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `buildPatch(convocatoria: ${convocatoria})`, 'end');
    return result;
  }

  /**
   * Añade validadores al formulario dependiendo del estado de la convocatoria
   */
  private checkEstado(formgroup: FormGroup, convocatoria: IConvocatoria): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `checkEstado(formgroup: ${formgroup}, convocatoria: ${convocatoria})`, 'start');
    if (convocatoria.estadoActual === TipoEstadoConvocatoria.BORRADOR) {
      formgroup.get('modeloEjecucion').setValidators(IsEntityValidator.isValid());
      formgroup.get('finalidad').setValidators(IsEntityValidator.isValid());
      formgroup.get('ambitoGeografico').setValidators(IsEntityValidator.isValid());
    } else {
      formgroup.get('modeloEjecucion').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('finalidad').setValidators([Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('ambitoGeografico').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `checkEstado(formgroup: ${formgroup}, convocatoria: ${convocatoria})`, 'end');
  }

  isEstadoRegistrada() {
    return this.convocatoria.estadoActual === TipoEstadoConvocatoria.REGISTRADA;
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.convocatoriaService.findById(key).pipe(
      switchMap((value) => {
        this.convocatoria = value;
        return this.getUnidadGestion(value.unidadGestionRef).pipe(
          map(unidadGestion => {
            this.getFormGroup().get('unidadGestion').setValue(unidadGestion);
            return value;
          })
        );
      }),
      switchMap((convocatoria) => {
        return this.convocatoriaService.findAllConvocatoriaEntidadGestora(key).pipe(
          switchMap((listResult) => {
            const convocatoriasEntidadGestoras = listResult.items;
            if (convocatoriasEntidadGestoras.length > 0) {
              this.convocatoriaEntidadGestora = convocatoriasEntidadGestoras[0];
              return this.empresaEconomicaService.findById(this.convocatoriaEntidadGestora.empresaEconomica.personaRef).pipe(
                map((empresaEconomica) => {
                  this.getFormGroup().get('entidadGestora').setValue(empresaEconomica);
                  return convocatoria;
                })
              );
            }
            else {
              return of(convocatoria);
            }
          })
        );
      }),
      tap(() => this.loadAreasTematicas(key)),
      tap(() => this.checkVinculaciones(key)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `initializer(key: ${key})`, 'end')),
      catchError((error) => {
        this.logger.error(ConvocatoriaDatosGeneralesFragment.name,
          `initializer(key: ${key})`, error);
        return EMPTY;
      })
    );
  }

  private checkVinculaciones(id: number): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `checkUnidadModelo(key: ${id})`, 'start');
    if (id) {
      const subscription = this.convocatoriaService.vinculaciones(id).subscribe(
        status => {
          if (status) {
            this.getFormGroup().controls.unidadGestion.disable();
            this.getFormGroup().controls.modeloEjecucion.disable();
            this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
              `checkUnidadModelo(key: ${id})`, 'end');
          }
        },
        error => {
          this.logger.error(ConvocatoriaDatosGeneralesFragment.name,
            `checkUnidadModelo(key: ${id})`, error);
        }
      );
      this.subscriptions.push(subscription);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `checkUnidadModelo(key: ${id})`, 'end');
  }

private getUnidadGestion(unidadGestionRef: string): Observable<IUnidadGestion> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `loadUnidadGestion()`, 'start');
    const options = {
      filters: [
        {
          field: 'acronimo',
          type: SgiRestFilterType.LIKE,
          value: unidadGestionRef,
        } as SgiRestFilter
      ]
    } as SgiRestFindOptions;
    return this.unidadGestionService.findAll(options).pipe(
      map(list => {
        if (list.items.length === 1) {
          return list.items[0];
        }
        else {
          return undefined;
        }
      })
    );
  }

  private loadAreasTematicas(id: number): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `loadAreasTematicas(id: ${id})`, 'start');
    const subscription = this.convocatoriaService.findAreaTematicas(id).pipe(
      map(response => response.items),
      map(convocatoriasAreaTematicas => {
        const list: AreaTematicaData[] = [];
        convocatoriasAreaTematicas.forEach(
          convocatoriaAreaTematica => {
            const element: AreaTematicaData = {
              padre: undefined,
              convocatoriaAreaTematica: new StatusWrapper<IConvocatoriaAreaTematica>(convocatoriaAreaTematica),
              observaciones: convocatoriaAreaTematica.observaciones
            };
            list.push(this.loadAreaData(element));
          }
        );
        return list;
      })
    ).subscribe(areas => {
      this.areasTematicas$.next(areas);
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `loadAreasTematicas(id: ${id})`, 'end');
    });
    this.subscriptions.push(subscription);
  }

  private getSecondLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `getSecondLevelAreaTematica(areaTematica: ${areaTematica})`, 'start');
    if (areaTematica?.padre?.padre) {
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `getSecondLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
      return this.getSecondLevelAreaTematica(areaTematica.padre);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `getSecondLevelPrograma(areaTematica: ${areaTematica})`, 'end');
    return areaTematica;
  }

  private loadAreaData(data: AreaTematicaData): AreaTematicaData {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `loadData(data: ${data})`, 'start');
    const areaTematica = data.convocatoriaAreaTematica.value.areaTematica;
    if (areaTematica) {
      const result = this.getSecondLevelAreaTematica(areaTematica);
      const padre = result.padre ? result.padre : areaTematica;
      const element: AreaTematicaData = {
        padre,
        observaciones: data.convocatoriaAreaTematica.value.observaciones,
        convocatoriaAreaTematica: data.convocatoriaAreaTematica,
      };
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `loadData(data: ${data})`, 'end');
      return element;
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `loadData(data: ${data})`, 'end');
    return data;
  }

  getValue(): IConvocatoria {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.convocatoria.unidadGestionRef = form.unidadGestion.value.acronimo;
    if (typeof form.modeloEjecucion.value === 'string') {
      this.convocatoria.modeloEjecucion = undefined;
    } else {
      this.convocatoria.modeloEjecucion = form.modeloEjecucion.value;
    }
    this.convocatoria.codigo = form.codigo.value;
    this.convocatoria.anio = form.anio.value;
    this.convocatoria.titulo = form.titulo.value;
    this.convocatoria.objeto = form.objeto.value;
    this.convocatoria.observaciones = form.observaciones.value;
    if (typeof form.finalidad.value === 'string') {
      this.convocatoria.finalidad = undefined;
    } else {
      this.convocatoria.finalidad = form.finalidad.value;
    }
    if (typeof form.regimenConcurrencia.value === 'string') {
      this.convocatoria.regimenConcurrencia = undefined;
    } else {
      this.convocatoria.regimenConcurrencia = form.regimenConcurrencia.value;
    }
    if (form.destinatarios.value?.length > 0) {
      this.convocatoria.destinatarios = form.destinatarios.value;
    } else {
      this.convocatoria.destinatarios = undefined;
    }
    this.convocatoria.colaborativos = form.colaborativos.value;
    this.convocatoria.duracion = form.duracion.value;
    if (typeof form.ambitoGeografico.value === 'string') {
      this.convocatoria.ambitoGeografico = undefined;
    } else {
      this.convocatoria.ambitoGeografico = form.ambitoGeografico.value;
    }
    if (form.clasificacionCVN.value?.length > 0) {
      this.convocatoria.clasificacionCVN = form.clasificacionCVN.value;
    } else {
      this.convocatoria.clasificacionCVN = undefined;
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.convocatoria;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const convocatoria = this.getValue();
    const observable$ = this.isEdit() ? this.update(convocatoria) :
      this.create(convocatoria);
    return observable$.pipe(
      map(value => {
        this.convocatoria = value;
        return this.convocatoria.id;
      }),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `create(convocatoria: ${convocatoria})`, 'start');
    return this.convocatoriaService.create(convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => this.saveOrUpdateConvocatoriaEntidadGestora(result)),
      switchMap((result) => this.saveOrUpdateAreasTematicas(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `create(convocatoria: ${convocatoria})`, 'end'))
    );
  }

  private update(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `update(convocatoria: ${convocatoria})`, 'start');
    return this.convocatoriaService.update(convocatoria.id, convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => this.saveOrUpdateConvocatoriaEntidadGestora(result)),
      switchMap((result) => this.saveOrUpdateAreasTematicas(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `update(convocatoria: ${convocatoria})`, 'end'))
    );
  }

  private saveOrUpdateConvocatoriaEntidadGestora(result: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `saveOrUpdateConvocatoriaEntidadGestora(result: ${result})`, 'start');
    let observable$ = of();
    const entidadRef = this.getFormGroup().controls.entidadGestora.value?.personaRef;
    this.convocatoriaEntidadGestora.convocatoria = result;
    if (entidadRef !== this.convocatoriaEntidadGestora.empresaEconomica?.personaRef) {
      if (!entidadRef) {
        observable$ = this.deleteConvocatoriaEntidadGestora();
      }
      else {
        observable$ = this.convocatoriaEntidadGestora.id ?
          this.updateConvocatoriaEntidadGestora() : this.createConvocatoriaEntidadGestora();
      }
    }
    return observable$.pipe(
      switchMap(() => of(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `saveOrUpdateConvocatoriaEntidadGestora(result: ${result})`, 'end'))
    );
  }

  private createConvocatoriaEntidadGestora(): Observable<IConvocatoriaEntidadGestora> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `createConvocatoriaEntidadGestora()`, 'start');
    this.convocatoriaEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.create(this.convocatoriaEntidadGestora).pipe(
      tap(result => {
        this.convocatoriaEntidadGestora = result;
        this.convocatoriaEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
      }),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `createConvocatoriaEntidadGestora()`, 'end'))
    );
  }

  private updateConvocatoriaEntidadGestora(): Observable<IConvocatoriaEntidadGestora> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `updateConvocatoriaEntidadGestora()`, 'start');
    this.convocatoriaEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.update(
      this.convocatoriaEntidadGestora.id, this.convocatoriaEntidadGestora).pipe(
        tap(result => {
          this.convocatoriaEntidadGestora = result;
          this.convocatoriaEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
        }),
        tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
          `updateConvocatoriaEntidadGestora()`, 'end'))
      );
  }

  private deleteConvocatoriaEntidadGestora(): Observable<void> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `deleteConvocatoriaEntidadGestora()`, 'start');
    this.convocatoriaEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.deleteById(
      this.convocatoriaEntidadGestora.id).pipe(
        tap(() => {
          this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
          this.convocatoriaEntidadGestora.empresaEconomica = {} as IEmpresaEconomica;
        }),
        tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
          `deleteConvocatoriaEntidadGestora()`, 'end'))
      );
  }

  deleteConvocatoriaAreaTematica(data: AreaTematicaData) {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `deleteConvocatoriaAreaTematica(wrapper: ${data})`, 'start');
    const current = this.areasTematicas$.value;
    const index = current.findIndex(element =>
      element.convocatoriaAreaTematica.value === data.convocatoriaAreaTematica.value);
    if (index >= 0) {
      this.convocatoriaAreaTematicaEliminadas.push(current[index].convocatoriaAreaTematica);
      current.splice(index, 1);
      this.areasTematicas$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `deleteConvocatoriaAreaTematica(wrapper: ${data})`, 'end');
  }

  updateConvocatoriaAreaTematica(data: AreaTematicaData) {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `updateConvocatoriaAreaTematica(wrapper: ${data})`, 'start');
    const element = this.loadAreaData(data);
    const wrapper = new StatusWrapper<AreaTematicaData>(element);
    const current = this.areasTematicas$.value;
    const index = current.findIndex(value =>
      value.convocatoriaAreaTematica.value === data.convocatoriaAreaTematica.value);
    if (index >= 0) {
      wrapper.setEdited();
      this.areasTematicas$.value[index] = wrapper.value;
      this.areasTematicas$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.updateConvocatoriaAreaTematica.name}(wrapper: ${data})`, 'end');
  }

  addConvocatoriaAreaTematica(data: AreaTematicaData) {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `addConvocatoriaAreaTematica(areaTematica: ${data})`, 'start');
    const element = this.loadAreaData(data);
    const wrapper = new StatusWrapper<AreaTematicaData>(element);
    wrapper.setCreated();
    const current = this.areasTematicas$.value;
    current.push(wrapper.value);
    this.areasTematicas$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `addConvocatoriaAreaTematica(areaTematica: ${data})`, 'end');
  }

  saveOrUpdateAreasTematicas(result: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `saveOrUpdateAreasTematicas()`, 'start');
    return merge(
      this.deleteConvocatoriaAreaTematicas(),
      this.updateConvocatoriaAreaTematicas(),
      this.createConvocatoriaAreaTematicas()
    ).pipe(
      takeLast(1),
      switchMap(() => of(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `saveOrUpdateAreasTematicas()`, 'end'))
    );
  }

  private deleteConvocatoriaAreaTematicas(): Observable<void> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `deleteConvocatoriaAreaTematicas()`, 'start');
    const deleteEntidades = this.convocatoriaAreaTematicaEliminadas.filter((value) => value.value.id);
    if (deleteEntidades.length === 0) {
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `deleteConvocatoriaAreaTematicas()`, 'end');
      return of(void 0);
    }
    return from(deleteEntidades).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaAreaTematicaService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.convocatoriaAreaTematicaEliminadas =
                this.convocatoriaAreaTematicaEliminadas.filter(deletedModelo =>
                  deletedModelo.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
              `deleteConvocatoriaAreaTematicas()`, 'end'))
          );
      })
    );
  }

  private updateConvocatoriaAreaTematicas(): Observable<void> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `updateConvocatoriaAreaTematicas()`, 'start');
    const editedAreas = this.areasTematicas$.value.filter(
      (wrapper) => wrapper.convocatoriaAreaTematica.value.id);
    if (editedAreas.length === 0) {
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `updateConvocatoriaAreaTematicas()`, 'end');
      return of(void 0);
    }
    return from(editedAreas).pipe(
      mergeMap((data) => {
        return this.convocatoriaAreaTematicaService.update(
          data.convocatoriaAreaTematica.value.id, data.convocatoriaAreaTematica.value).pipe(
            map((updated) => {
              const index = this.areasTematicas$.value.findIndex(
                (current) => current === data);
              const element: AreaTematicaData = {
                padre: undefined,
                convocatoriaAreaTematica: new StatusWrapper<IConvocatoriaAreaTematica>(updated),
                observaciones: updated.observaciones
              };
              this.areasTematicas$.value[index] = this.loadAreaData(element);
            }),
            tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
              `updateConvocatoriaAreaTematicas()`, 'end')
            )
          );
      })
    );
  }

  private createConvocatoriaAreaTematicas(): Observable<void> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `createConvocatoriaAreaTematicas()`, 'start');
    const createdAreas = this.areasTematicas$.value.filter((wrapper) => !wrapper.convocatoriaAreaTematica.value.id);
    if (createdAreas.length === 0) {
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `createConvocatoriaAreaTematicas()`, 'end');
      return of(void 0);
    }
    createdAreas.forEach(
      (wrapper) => wrapper.convocatoriaAreaTematica.value.convocatoria = this.convocatoria
    );
    return from(createdAreas).pipe(
      mergeMap((data) => {
        return this.convocatoriaAreaTematicaService.create(data.convocatoriaAreaTematica.value).pipe(
          map((createdEntidad) => {
            const index = this.areasTematicas$.value.findIndex(
              (currentEntidad) => currentEntidad === data);
            this.areasTematicas$[index] =
              new StatusWrapper<IConvocatoriaAreaTematica>(createdEntidad);
          }),
          tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
            `createConvocatoriaAreaTematicas()`, 'end'))
        );
      })
    );
  }
}
