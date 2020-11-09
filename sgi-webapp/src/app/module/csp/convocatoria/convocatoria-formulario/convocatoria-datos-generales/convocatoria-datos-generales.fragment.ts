import { FormFragment } from '@core/services/action-service';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, EMPTY, BehaviorSubject, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
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

export class ConvocatoriaDatosGeneralesFragment extends FormFragment<IConvocatoria> {
  convocatoriaAreaTematicas$ = new BehaviorSubject<StatusWrapper<IConvocatoriaAreaTematica>[]>([]);

  convocatoria: IConvocatoria;
  selectedEmpresaEconomica: IEmpresaEconomica;
  convocatoriaEntidadGestora: IConvocatoriaEntidadGestora;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly empresaEconomicaService: EmpresaEconomicaService,
    private readonly convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    private readonly unidadGestionService: UnidadGestionService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'constructor()', 'start');
    this.convocatoria = {
      activo: true,
      estadoActual: TipoEstadoConvocatoria.BORRADOR
    } as IConvocatoria;
    this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, 'constructor()', 'start');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.buildFormGroup.name}()`, 'start');
    const form = new FormGroup({
      codigo: new FormControl('', [Validators.required, Validators.maxLength(50)]),
      estadoActual: new FormControl({
        value: TipoEstadoConvocatoria.BORRADOR,
        disabled: true
      }),
      unidadGestion: new FormControl('', [Validators.required, IsEntityValidator.isValid()]),
      anio: new FormControl(new Date().getFullYear(), [
        Validators.required,
        Validators.min(1000),
        Validators.max(9999),
        IsYearPlus.isValid(1)
      ]),
      titulo: new FormControl('', [Validators.required, Validators.maxLength(250)]),
      modeloEjecucion: new FormControl(''),
      finalidad: new FormControl(''),
      duracion: new FormControl('', [Validators.min(1), Validators.max(9999)]),
      ambitoGeografico: new FormControl(''),
      clasificacionCVN: new FormControl('', EnumValidador.isValid(ClasificacionCVN)),
      regimenConcurrencia: new FormControl(''),
      colaborativos: new FormControl(''),
      destinatarios: new FormControl('', [Validators.required, EnumValidador.isValid(TipoDestinatario)]),
      entidadGestora: new FormControl(''),
      objeto: new FormControl('', Validators.maxLength(2000)),
      observaciones: new FormControl('', Validators.maxLength(2000)),
      entidadGestoraRef: new FormControl('')
    });
    this.checkEstado(form, this.convocatoria);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.buildFormGroup.name}()`, 'start');
    return form;
  }

  buildPatch(convocatoria: IConvocatoria): { [key: string]: any } {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.buildPatch.name}(convocatoria: ${convocatoria})`, 'start');
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
      `${this.buildPatch.name}(convocatoria: ${convocatoria})`, 'end');
    return result;
  }

  /**
   * Añade validadores al formulario dependiendo del estado de la convocatoria
   */
  private checkEstado(formgroup: FormGroup, convocatoria: IConvocatoria): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.checkEstado.name}(formgroup: ${formgroup}, convocatoria: ${convocatoria})`, 'start');
    if (convocatoria.estadoActual === TipoEstadoConvocatoria.BORRADOR) {
      formgroup.get('modeloEjecucion').setValidators(IsEntityValidator.isValid());
      formgroup.get('finalidad').setValidators(IsEntityValidator.isValid());
      formgroup.get('ambitoGeografico').setValidators(IsEntityValidator.isValid());
    } else {
      formgroup.get('modeloEjecucion').setValidators([Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('finalidad').setValidators([Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('ambitoGeografico').setValidators([Validators.required, IsEntityValidator.isValid()]);
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.checkEstado.name}(formgroup: ${formgroup}, convocatoria: ${convocatoria})`, 'end');
  }

  isEstadoRegistrada() {
    return this.convocatoria.estadoActual === TipoEstadoConvocatoria.REGISTRADA;
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.initializer.name}(key: ${key})`, 'start');
    return this.convocatoriaService.findById(key).pipe(
      switchMap((value) => {
        this.convocatoria = value;
        this.loadUnidadGestion();
        return this.convocatoriaService.findAllConvocatoriaEntidadGestora(key).pipe(
          switchMap((listResult) => {
            const convocatoriasEntidadConvocantes = listResult.items;
            if (convocatoriasEntidadConvocantes.length > 0) {
              const entidadRef = convocatoriasEntidadConvocantes[0].entidadRef;
              this.getFormGroup().controls.entidadGestoraRef.setValue(entidadRef);
              this.loadEmpresaEconomica(entidadRef);
              this.convocatoriaEntidadGestora = convocatoriasEntidadConvocantes[0];
            }
            return of(this.convocatoria);
          })
        );
      }),
      // tap(() => this.loadAreasTematicas(key)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `${this.initializer.name}(key: ${key})`, 'end')),
      catchError(() => {
        this.logger.error(ConvocatoriaDatosGeneralesFragment.name,
          `${this.initializer.name}(key: ${key})`, 'error');
        return EMPTY;
      })
    );
  }

  private loadEmpresaEconomica(personaRef: string) {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.loadEmpresaEconomica.name}(personaRef: ${personaRef})`, 'start');
    const subscription = this.empresaEconomicaService.findById(personaRef).subscribe(
      empresaEconomica => {
        this.selectedEmpresaEconomica = empresaEconomica;
        this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
          `${this.loadEmpresaEconomica.name}(personaRef: ${personaRef})`, 'end');
      },
      () => {
        this.logger.error(ConvocatoriaDatosGeneralesFragment.name,
          `${this.loadEmpresaEconomica.name}(personaRef: ${personaRef})`, 'error');
      }
    );
    this.subscriptions.push(subscription);
  }

  private loadUnidadGestion() {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.loadUnidadGestion.name}()`, 'start');
    const options = {
      filters: [
        {
          field: 'acronimo',
          type: SgiRestFilterType.LIKE,
          value: this.convocatoria.unidadGestionRef,
        } as SgiRestFilter
      ]
    } as SgiRestFindOptions;
    const subscription = this.unidadGestionService.findAll(options).subscribe(
      result => {
        if (result.items.length > 0) {
          this.getFormGroup().get('unidadGestion').setValue(result.items[0]);
        }
        this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
          `${this.loadUnidadGestion.name}()`, 'end');
      },
      () => {
        this.logger.error(ConvocatoriaDatosGeneralesFragment.name,
          `${this.loadUnidadGestion.name}()`, 'error');
      }
    );
    this.subscriptions.push(subscription);
  }

  private loadAreasTematicas(id: number): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.loadAreasTematicas.name}(id: ${id})`, 'start');
    const subscription = this.convocatoriaService.findAreaTematicas(id).pipe(
      map(response => response.items)
    ).subscribe(areas => {
      this.convocatoriaAreaTematicas$.next(
        areas.map(area => new StatusWrapper<IConvocatoriaAreaTematica>(area))
      );
      this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.loadAreasTematicas.name}(id: ${id})`, 'end');
    });
    this.subscriptions.push(subscription);
  }

  getValue(): IConvocatoria {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.getValue.name}()`, 'start');
    const form = this.getFormGroup().value;
    this.convocatoria.unidadGestionRef = form.unidadGestion.acronimo;
    if (form.modeloEjecucion) {
      this.convocatoria.modeloEjecucion = form.modeloEjecucion;
    }
    this.convocatoria.codigo = form.codigo;
    this.convocatoria.anio = form.anio;
    this.convocatoria.titulo = form.titulo;
    this.convocatoria.objeto = form.objeto;
    this.convocatoria.observaciones = form.observaciones;
    if (typeof form.finalidad === 'string') {
      this.convocatoria.finalidad = undefined;
    } else {
      this.convocatoria.finalidad = form.finalidad;
    }
    if (typeof form.regimenConcurrencia === 'string') {
      this.convocatoria.regimenConcurrencia = undefined;
    } else {
      this.convocatoria.regimenConcurrencia = form.regimenConcurrencia;
    }
    if (form.destinatarios?.length > 0) {
      this.convocatoria.destinatarios = form.destinatarios;
    }
    this.convocatoria.colaborativos = form.colaborativos;
    this.convocatoria.duracion = form.duracion;
    if (typeof form.ambitoGeografico === 'string') {
      this.convocatoria.ambitoGeografico = undefined;
    } else {
      this.convocatoria.ambitoGeografico = form.ambitoGeografico;
    }
    if (form.clasificacionCVN?.length > 0) {
      this.convocatoria.clasificacionCVN = form.clasificacionCVN;
    }
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.getValue.name}()`, 'end');
    return this.convocatoria;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.saveOrUpdate.name}()`, 'start');
    const convocatoria = this.getValue();
    const observable$ = this.isEdit() ? this.update(convocatoria) : this.create(convocatoria);
    return observable$.pipe(
      map(value => {
        this.convocatoria = value;
        return this.convocatoria.id;
      }),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name, `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private create(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.create.name}(convocatoria: ${convocatoria})`, 'start');
    return this.convocatoriaService.create(convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => {
        return this.saveOrUpdateConvocatoriaEntidadConvocante(result);
      }),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `${this.create.name}(convocatoria: ${convocatoria})`, 'end'))
    );
  }

  private update(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.update.name}(convocatoria: ${convocatoria})`, 'start');
    return this.convocatoriaService.update(convocatoria.id, convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => this.saveOrUpdateConvocatoriaEntidadConvocante(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `${this.update.name}(convocatoria: ${convocatoria})`, 'end'))
    );
  }

  private saveOrUpdateConvocatoriaEntidadConvocante(result: IConvocatoria): Observable<IConvocatoria> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.saveOrUpdateConvocatoriaEntidadConvocante.name}(result: ${result})`, 'start');
    let observable$ = of({});
    const entidadRef = this.getFormGroup().controls.entidadGestoraRef.value;
    this.convocatoriaEntidadGestora.convocatoria = result;
    if (entidadRef) {
      observable$ = this.convocatoriaEntidadGestora.id ?
        this.updateConvocatoriaEntidadConvocante() : this.createConvocatoriaEntidadConvocante();
    }
    return observable$.pipe(
      switchMap(() => of(result)),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `${this.saveOrUpdateConvocatoriaEntidadConvocante.name}(result: ${result})`, 'end'))
    );
  }

  private createConvocatoriaEntidadConvocante(): Observable<IConvocatoriaEntidadGestora> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.createConvocatoriaEntidadConvocante.name}()`, 'start');
    return this.convocatoriaEntidadGestoraService.create(this.convocatoriaEntidadGestora).pipe(
      tap(result => this.convocatoriaEntidadGestora = result),
      tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
        `${this.createConvocatoriaEntidadConvocante.name}()`, 'end'))
    );
  }

  private updateConvocatoriaEntidadConvocante(): Observable<IConvocatoriaEntidadGestora> {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.updateConvocatoriaEntidadConvocante.name}()`, 'start');
    return this.convocatoriaEntidadGestoraService.update(
      this.convocatoriaEntidadGestora.id, this.convocatoriaEntidadGestora).pipe(
        tap(result => this.convocatoriaEntidadGestora = result),
        tap(() => this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
          `${this.updateConvocatoriaEntidadConvocante.name}()`, 'end'))
      );
  }

  setEmpresaEconomica(empresaEconomica: IEmpresaEconomica): void {
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.setEmpresaEconomica.name}(value: ${empresaEconomica})`, 'start');
    const entidadRef = empresaEconomica.personaRef;
    if (this.isEdit()) {
      const changed = this.getFormGroup().controls.entidadGestoraRef.value !== entidadRef;
      this.setChanges(changed);
    } else if (!this.isEdit()) {
      this.setComplete(!!empresaEconomica);
      this.setChanges(!!empresaEconomica);
    }
    this.selectedEmpresaEconomica = empresaEconomica;
    this.convocatoriaEntidadGestora.entidadRef = entidadRef;
    this.getFormGroup().controls.entidadGestoraRef.setValue(entidadRef);
    this.logger.debug(ConvocatoriaDatosGeneralesFragment.name,
      `${this.setEmpresaEconomica.name}(value: ${empresaEconomica})`, 'end');
  }

  getEmpresaEconomica(): IEmpresaEconomica {
    return this.selectedEmpresaEconomica;
  }

  get empresaEconomicaText(): string {
    return this.selectedEmpresaEconomica ? this.selectedEmpresaEconomica.razonSocial : '';
  }
}
