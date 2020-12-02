import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { TipoEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, BehaviorSubject, from, EMPTY } from 'rxjs';
import { map, tap, switchMap, mergeMap, catchError } from 'rxjs/operators';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { SgiAuthService } from '@sgi/framework/auth';


export interface SolicitudModalidadEntidadConvocanteListado {
  entidadConvocante: IConvocatoriaEntidadConvocante;
  plan: IPrograma;
  modalidad: StatusWrapper<ISolicitudModalidad>;
}

export class SolicitudDatosGeneralesFragment extends FormFragment<ISolicitud> {

  solicitud: ISolicitud;

  selectedSolicitante: IPersona;
  selectedConvocatoria: IConvocatoria;

  entidadesConvocantes = [] as IConvocatoriaEntidadConvocante[];

  entidadesConvocantesModalidad$ = new BehaviorSubject<SolicitudModalidadEntidadConvocanteListado[]>([]);

  public showComentariosEstado$ = new BehaviorSubject<boolean>(false);
  public disabledSolicitante = false;
  convocatoriaRequired = false;
  convocatoriaExternaRequired = false;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: SolicitudService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private convocatoriaService: ConvocatoriaService,
    private empresaEconomicaService: EmpresaEconomicaService,
    private personaFisicaService: PersonaFisicaService,
    private solicitudModalidadService: SolicitudModalidadService,
    private unidadGestionService: UnidadGestionService,
    private sgiAuthService: SgiAuthService
  ) {
    super(key, true);
    this.setComplete(true);
    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'constructor()', 'start');
    this.solicitud = { activo: true } as ISolicitud;
    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected initializer(key: number): Observable<ISolicitud> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `initializer(key: ${key})`, 'start');

    return this.service.findById(key).pipe(
      tap(solicitud => {
        this.solicitud = solicitud;
      }),
      switchMap(() => {
        return this.loadSolicitante(this.solicitud.solicitante.personaRef);
      }),
      switchMap(() => {
        return this.solicitud.convocatoria ? this.loadConvocatoria(this.solicitud.convocatoria.id) : of(EMPTY);
      }),
      switchMap(() => {
        return this.loadUnidadGestion(this.solicitud.unidadGestion.acronimo);
      }),
      switchMap(() => {
        return this.solicitud.convocatoria ?
          this.loadEntidadesConvocantesModalidad(this.solicitud.id, this.solicitud.convocatoria.id) : of(EMPTY);
      }),
      map(() => {
        this.logger.debug(SolicitudDatosGeneralesFragment.name, `initializer(key: ${key})`, 'end');

        if (this.solicitud && this.solicitud.estado) {
          const estadosComentarioVisble = [
            TipoEstadoSolicitud.EXCLUIDA_PROVISIONAL,
            TipoEstadoSolicitud.DESISTIDA,
            TipoEstadoSolicitud.ALEGADA_ADMISION,
            TipoEstadoSolicitud.EXCLUIDA,
            TipoEstadoSolicitud.DENEGADA_PROVISIONAL,
            TipoEstadoSolicitud.ALEGADA_CONCESION,
            TipoEstadoSolicitud.DENEGADA
          ];

          this.showComentariosEstado$.next(estadosComentarioVisble.includes(this.solicitud.estado.estado));
        } else {
          this.showComentariosEstado$.next(false);
        }

        return this.solicitud;
      }),
      catchError(() => {
        this.logger.error(SolicitudDatosGeneralesFragment.name, `initializer(key: ${key})`, 'error');
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `${this.buildFormGroup.name}()`, 'start');
    const form = new FormGroup({
      estado: new FormControl({ value: TipoEstadoSolicitud.BORRADOR, disabled: true }),
      solicitante: new FormControl('', Validators.required),
      convocatoria: new FormControl(''),
      comentariosEstado: new FormControl({ value: '', disabled: true }),
      convocatoriaExterna: new FormControl(''),
      tipoFormulario: new FormControl({ value: '', disabled: this.isEdit() }),
      unidadGestion: new FormControl({ value: '' }),
      codigoExterno: new FormControl('', Validators.maxLength(50)),
      codigoRegistro: new FormControl({ value: '', disabled: true }),
      observaciones: new FormControl('', Validators.maxLength(2000))
    });

    // Se setean los validaores condicionales y se hace una subscripcion a los campos que provocan
    // cambios en los validadores del formulario
    this.setConditionalValidators(form);

    this.subscriptions.push(
      merge(
        form.controls.convocatoria.valueChanges,
        form.controls.convocatoriaExterna.valueChanges
      ).subscribe(_ => {
        this.setConditionalValidators(form);
      })
    );

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `${this.buildFormGroup.name}()`, 'end');

    return form;
  }

  buildPatch(solicitud: ISolicitud): { [key: string]: any } {
    this.logger.debug(SolicitudDatosGeneralesFragment.name,
      `${this.buildPatch.name}(solicitud: ${solicitud})`, 'start');
    const result = {
      estado: solicitud.estado?.estado,
      comentariosEstado: solicitud.estado?.comentario,
      solicitante: solicitud.solicitante,
      convocatoria: solicitud.convocatoria,
      convocatoriaExterna: solicitud.convocatoriaExterna,
      tipoFormulario: solicitud.formularioSolicitud,
      unidadGestion: solicitud.unidadGestion,
      codigoRegistro: solicitud.codigoRegistroInterno,
      codigoExterno: solicitud.codigoExterno,
      observaciones: solicitud.observaciones
    };

    // Validaciones con los datos iniciales del formulario
    this.setConditionalValidators(this.getFormGroup(), solicitud);

    this.logger.debug(SolicitudDatosGeneralesFragment.name,
      `${this.buildPatch.name}(solicitud: ${solicitud})`, 'end');
    return result;
  }

  getValue(): ISolicitud {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `${this.getValue.name}()`, 'start');
    const form = this.getFormGroup().controls;

    this.solicitud.solicitante = form.solicitante.value;
    this.solicitud.convocatoria = form.convocatoria.value;
    this.solicitud.convocatoriaExterna = form.convocatoriaExterna.value;
    this.solicitud.formularioSolicitud = form.tipoFormulario.value;
    this.solicitud.unidadGestion = form.unidadGestion.value;
    this.solicitud.codigoExterno = form.codigoExterno.value;
    this.solicitud.observaciones = form.observaciones.value;

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `${this.getValue.name}()`, 'end');
    return this.solicitud;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      tap((value) => {
        this.solicitud = value;
      }),
      switchMap((solicitud) => {
        return merge(
          this.createSolicitudModalidades(solicitud.id),
          this.deleteSolicitudModalidades(solicitud.id),
          this.updateSolicitudModalidades(solicitud.id));
      }),
      map(() => {
        return this.solicitud.id;
      })
    );
  }

  /**
   * Añada la solicitudModalidad y la marca como creada
   *
   * @param solicitudModalidad ISolicitudModalidad
   */
  public addSolicitudModalidad(solicitudModalidad: ISolicitudModalidad): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'addSolicitudModalidad()', 'start');

    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.entidadConvocante.entidad.personaRef === solicitudModalidad.entidad.personaRef);
    if (index >= 0) {
      const wrapper = new StatusWrapper(solicitudModalidad);
      current[index].modalidad = wrapper;
      wrapper.setCreated();
      this.setChanges(true);
    }

    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'addSolicitudModalidad()', 'end');
  }

  /**
   * Actualiza la solicitudModalidad y la marca como editada
   *
   * @param solicitudModalidad ISolicitudModalidad
   */
  public updateSolicitudModalidad(solicitudModalidad: ISolicitudModalidad): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'updateSolicitudModalidad()', 'start');

    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.modalidad && value.modalidad.value === solicitudModalidad);
    if (index >= 0) {
      current[index].modalidad.value.programa = solicitudModalidad.programa;
      current[index].modalidad.setEdited();
      this.setChanges(true);
    }

    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'updateSolicitudModalidad()', 'end');
  }

  /**
   * Elimina la solicitudModalidad y la marca como eliminada si era una modalidad que ya existia previamente.
   *
   * @param wrapper ISolicitudModalidad
   */
  public deleteSolicitudModalidad(wrapper: StatusWrapper<ISolicitudModalidad>): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'deleteSolicitudModalidad()', 'start');

    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.modalidad && value.modalidad.value === wrapper.value);
    if (index >= 0) {
      if (wrapper.created) {
        current[index].modalidad = undefined;
      } else {
        wrapper.value.programa = undefined;
        wrapper.setDeleted();
        this.setChanges(true);
      }
    }

    this.logger.debug(SolicitudDatosGeneralesFragment.name, 'deleteSolicitudModalidad()', 'end');
  }

  /**
   * Crea las modalidades añadidas.
   *
   * @param solicitudId id de la solicitud
   */
  private createSolicitudModalidades(solicitudId: number): Observable<void> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `createSolicitudModalidades(solicitudId: ${solicitudId})`, 'start');

    const createdSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter((entidadConvocanteModalidad) => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.created)
      .map(entidadConvocanteModalidad => {
        entidadConvocanteModalidad.modalidad.value.solicitud = { id: solicitudId } as ISolicitud;
        return entidadConvocanteModalidad.modalidad;
      });

    if (createdSolicitudModalidades.length === 0) {
      this.logger.debug(SolicitudDatosGeneralesFragment.name, `createSolicitudModalidades(solicitudId: ${solicitudId})`, 'end');
      return of(void 0);
    }

    return from(createdSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService
          .create(wrappedSolicitudModalidad.value).pipe(
            map((createdSolicitudModalidad) => {
              const index = this.entidadesConvocantesModalidad$.value
                .findIndex((currentEntidadConvocanteModalidad) =>
                  currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

              this.entidadesConvocantesModalidad$.value[index].modalidad =
                new StatusWrapper<ISolicitudModalidad>(createdSolicitudModalidad);
            }),
            tap(() => this.logger.debug(SolicitudDatosGeneralesFragment.name,
              `createSolicitudModalidades(solicitudId: ${solicitudId})`, 'end'))
          );
      }));
  }

  /**
   * Elimina las modalidades borradas.
   *
   * @param solicitudId id de la solicitud
   */
  private deleteSolicitudModalidades(solicitudId: number): Observable<void> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `deleteSolicitudModalidades(solicitudId: ${solicitudId})`, 'start');

    const deletedSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter(entidadConvocanteModalidad => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.deleted)
      .map(entidadConvocanteModalidad => entidadConvocanteModalidad.modalidad);

    if (deletedSolicitudModalidades.length === 0) {
      this.logger.debug(SolicitudDatosGeneralesFragment.name, `deleteSolicitudModalidades(solicitudId: ${solicitudId})`, 'end');
      return of(void 0);
    }

    return from(deletedSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService
          .deleteById(wrappedSolicitudModalidad.value.id).pipe(
            map(() => {
              const index = this.entidadesConvocantesModalidad$.value
                .findIndex((currentEntidadConvocanteModalidad) =>
                  currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

              this.entidadesConvocantesModalidad$.value[index].modalidad = undefined;
            }),
            tap(() => this.logger.debug(SolicitudDatosGeneralesFragment.name,
              `deleteSolicitudModalidades(solicitudId: ${solicitudId})`, 'end'))
          );
      }));
  }

  /**
   * Actualiza las modalidades modificadas.
   *
   * @param solicitudId id de la solicitud
   */
  private updateSolicitudModalidades(solicitudId: number): Observable<void> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `updateSolicitudModalidades(solicitudId: ${solicitudId})`, 'start');

    const updatedSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter(entidadConvocanteModalidad => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.edited)
      .map(entidadConvocanteModalidad => entidadConvocanteModalidad.modalidad);

    if (updatedSolicitudModalidades.length === 0) {
      this.logger.debug(SolicitudDatosGeneralesFragment.name, `updateSolicitudModalidades(solicitudId: ${solicitudId})`, 'end');
      return of(void 0);
    }

    return from(updatedSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService
          .update(wrappedSolicitudModalidad.value.id, wrappedSolicitudModalidad.value).pipe(
            map((updatedSolicitudModalidad) => {
              const index = this.entidadesConvocantesModalidad$.value
                .findIndex((currentEntidadConvocanteModalidad) =>
                  currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

              this.entidadesConvocantesModalidad$.value[index].modalidad =
                new StatusWrapper<ISolicitudModalidad>(updatedSolicitudModalidad);
            }),
            tap(() => this.logger.debug(SolicitudDatosGeneralesFragment.name,
              `updateSolicitudModalidades(solicitudId: ${solicitudId})`, 'end'))
          );
      }));
  }

  /**
   * Setea el solicitante seleccionado en el formulario
   *
   * @param solicitante un solicitante
   */
  setSolicitante(solicitante: IPersona): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setSolicitante(${solicitante})`, 'start');

    this.selectedSolicitante = solicitante;
    this.getFormGroup().controls.solicitante.setValue(solicitante);

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setSolicitante(${solicitante})`, 'end');
  }

  get solicitanteText(): string {
    return this.selectedSolicitante ? `${this.selectedSolicitante.nombre} ${this.selectedSolicitante.primerApellido} ${this.selectedSolicitante.segundoApellido}` : '';
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta (tipo formulario, unidad gestión y modalidades)
   *
   * @param convocatoria una convocatoria
   */
  setConvocatoria(convocatoria: IConvocatoria): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setConvocatoria(${convocatoria})`, 'start');

    this.selectedConvocatoria = convocatoria;
    this.getFormGroup().controls.convocatoriaExterna.setValue('', { emitEvent: false });
    this.getFormGroup().controls.convocatoria.setValue(convocatoria);

    this.subscriptions.push(
      this.unidadGestionService.findByAcronimo(convocatoria.unidadGestionRef).subscribe(unidadGestion => {
        this.getFormGroup().controls.unidadGestion.setValue(unidadGestion);
      })
    );

    this.subscriptions.push(
      this.configuracionSolicitudService.findById(convocatoria.id).subscribe(configuracionSolicitud => {
        this.getFormGroup().controls.tipoFormulario.setValue(configuracionSolicitud.formularioSolicitud);
      })
    );

    this.subscriptions.push(
      this.loadEntidadesConvocantesModalidad(this.solicitud?.id, convocatoria.id).subscribe()
    );

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setConvocatoria(${convocatoria})`, 'end');
  }

  get convocatoriaText(): string {
    return this.selectedConvocatoria ? this.selectedConvocatoria.titulo : '';
  }

  /**
   * Carga los datos de la convocatoria en la solicitud
   *
   * @param solicitanteRef Identificador del solicitante
   * @returns observable para recuperar los datos
   */
  private loadConvocatoria(convocatoriaId: number): Observable<IConvocatoria> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name,
      `loadConvocatoria(convocatoriaId: ${convocatoriaId})`, 'start');

    return this.convocatoriaService.findById(convocatoriaId).pipe(
      tap(convocatoria => {
        this.solicitud.convocatoria = convocatoria;
        this.selectedConvocatoria = this.solicitud.convocatoria;
        this.logger.debug(SolicitudDatosGeneralesFragment.name,
          `loadConvocatoria(convocatoriaId: ${convocatoriaId})`, 'end');
      })
    );
  }

  /**
   * Carga los datos del solicitante en la solicitud
   *
   * @param solicitanteRef Identificador del solicitante
   * @returns observable para recuperar los datos
   */
  private loadSolicitante(solicitanteRef: string): Observable<IPersona> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name,
      `loadSolicitante(solicitanteRef: ${solicitanteRef})`, 'start');

    return this.personaFisicaService.getInformacionBasica(solicitanteRef).pipe(
      tap(solicitante => {
        this.solicitud.solicitante = solicitante;
        this.selectedSolicitante = this.solicitud.solicitante;
        this.logger.debug(SolicitudDatosGeneralesFragment.name,
          `loadSolicitante(solicitanteRef: ${solicitanteRef})`, 'end');
      })
    );

  }

  /**
   * Carga los datos de la unidad de gestion en la solicitud
   *
   * @param acronimo Identificador de la unidad de gestion
   * @returns observable para recuperar los datos
   */
  private loadUnidadGestion(acronimo: string): Observable<SgiRestListResult<IUnidadGestion>> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name,
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
          this.solicitud.unidadGestion = result.items[0];
          this.getFormGroup().controls.unidadGestion.setValue(this.solicitud.unidadGestion);
        }
        this.logger.debug(SolicitudDatosGeneralesFragment.name,
          `loadUnidadGestion(acronimo: ${acronimo})`, 'end');
      })
    );

  }


  /**
   * Carga los datos de la tabla de modalidades de la solicitud y emite el cambio para que
   * se pueda actualizar la tabla
   *
   * @param solicitudId Identificador de la solicitud
   * @param convocatoriaId Identificador de la convocatoria
   * @returns observable para recuperar los datos
   */
  private loadEntidadesConvocantesModalidad(solicitudId: number, convocatoriaId: number):
    Observable<SolicitudModalidadEntidadConvocanteListado[]> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name,
      `loadEntidadesConvocantesModalidad(solicitudId: ${solicitudId}), convocatoriaId: ${convocatoriaId})`, 'start');

    return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaId).pipe(
      map(resultEntidadConvocantes => {
        if (resultEntidadConvocantes.total === 0) {
          return [] as SolicitudModalidadEntidadConvocanteListado[];
        }

        const entidadesConvocantesModalidad = resultEntidadConvocantes.items.map(entidadConvocante => {
          return {
            entidadConvocante,
            plan: this.getPlan(entidadConvocante.programa)
          } as SolicitudModalidadEntidadConvocanteListado;
        });

        this.entidadesConvocantesModalidad$.next(entidadesConvocantesModalidad);

        return entidadesConvocantesModalidad;
      }),
      mergeMap(entidadesConvocantesModalidad => {
        return from(entidadesConvocantesModalidad).pipe(
          mergeMap((element) => {
            return this.empresaEconomicaService.findById(element.entidadConvocante.entidad.personaRef).pipe(
              map(empresaEconomica => {
                element.entidadConvocante.entidad = empresaEconomica;
                element.plan = this.getPlan(element.entidadConvocante.programa);
                return element;
              }),
              catchError(() => of(element))
            );
          }),
          switchMap(() => of(entidadesConvocantesModalidad))
        );
      }),
      switchMap(entidadesConvocantesModalidad => {
        if (!solicitudId) {
          return of(entidadesConvocantesModalidad);
        }

        return this.getSolicitudModalidades(solicitudId).pipe(
          switchMap(solicitudModalidades => {
            entidadesConvocantesModalidad.map(element => {
              const solicitudModalidad = solicitudModalidades
                .find(modalidad => modalidad.entidad.personaRef === element.entidadConvocante.entidad.personaRef);
              if (solicitudModalidad) {
                element.modalidad = new StatusWrapper(solicitudModalidad);
              }
            });

            return of(entidadesConvocantesModalidad);
          })
        );
      }),
      tap(() => {
        this.logger.debug(SolicitudDatosGeneralesFragment.name,
          `loadEntidadesConvocantesModalidad(solicitudId: ${solicitudId}), convocatoriaId: ${convocatoriaId})`, 'end');
      })
    );
  }

  /**
   * Recupera las modalidades de la solicitud
   *
   * @param solicitudId Identificador de la solicitud
   * @returns observable para recuperar los datos
   */
  private getSolicitudModalidades(solicitudId: number): Observable<ISolicitudModalidad[]> {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `getSolicitudModalidades(solicitudId: ${solicitudId})`, 'start');

    return this.service.findAllSolicitudModalidades(solicitudId).pipe(
      switchMap(res => {
        return of(res.items);
      }),
      tap(() => {
        this.logger.debug(SolicitudDatosGeneralesFragment.name,
          `getSolicitudModalidades(solicitudId: ${solicitudId})`, 'end');
      })
    );
  }

  /**
   * Recupera el plan de un programa (el programa que no tenga padre)
   *
   * @param programa un IPrograma
   * @returns el plan
   */
  private getPlan(programa: IPrograma): IPrograma {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `getPlan(programa: ${programa})`, 'start');
    let programaRaiz = programa;

    while (programaRaiz.padre) {
      programaRaiz = programaRaiz.padre;
    }

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `getPlan(programa: ${programa})`, 'end');

    return programaRaiz;
  }

  /**
   * Añade/elimina los validadores del formulario que solo son necesarios si se cumplen ciertas condiciones.
   *
   * @param form el formulario
   * @param solicitud datos de la solicitud utilizados para determinar los validadores que hay que añadir.
   *  Si no se indica la evaluacion se hace con los datos rellenos en el formulario.
   */
  private setConditionalValidators(form: FormGroup, solicitud?: ISolicitud): void {
    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setConditionalValidators(form: ${form}, solicitud: ${solicitud})`, 'start');

    const convocatoriaControl = form.controls.convocatoria;
    const convocatoriaExternaControl = form.controls.convocatoriaExterna;
    const tipoFormularioControl = form.controls.tipoFormulario;
    const unidadGestionControl = form.controls.unidadGestion;

    const convocatoriaSolicitud = solicitud ? solicitud.convocatoria : convocatoriaControl.value;
    const convocatoriaExternaSolicitud = solicitud ? solicitud.convocatoriaExterna : convocatoriaExternaControl.value;

    if (!this.isEdit()) {
      if (!convocatoriaSolicitud) {
        convocatoriaExternaControl.setValidators([Validators.required, Validators.maxLength(50)]);
        tipoFormularioControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required, IsEntityValidator.isValid()]);

        convocatoriaControl.setValidators(null);
      } else {
        convocatoriaControl.setValidators([Validators.required]);

        convocatoriaExternaControl.setValidators(null);
        tipoFormularioControl.setValidators(null);
        unidadGestionControl.setValidators(null);

        convocatoriaExternaControl.disable({ emitEvent: false });
        tipoFormularioControl.disable({ emitEvent: false });
        unidadGestionControl.disable({ emitEvent: false });
      }

      convocatoriaControl.updateValueAndValidity({ emitEvent: false });
      convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
      tipoFormularioControl.updateValueAndValidity({ emitEvent: false });
      unidadGestionControl.updateValueAndValidity({ emitEvent: false });
    } else {
      // Investigador solo puede modificar en los estados Borrador, Excluida provisional, Denegada provisional
      // Gestor o administrador solo pued modificar en los estados Borrador,  Presentada, Admitida provisional
      // Alegada admisión, Admitida definitiva, Concedida provisional, Alegada concesión
      if (solicitud && ((this.sgiAuthService.hasAuthorityForAnyUO('CSP-SOL-C-INV') &&
        (solicitud.estado.estado !== TipoEstadoSolicitud.BORRADOR
          && solicitud.estado.estado !== TipoEstadoSolicitud.EXCLUIDA_PROVISIONAL
          && solicitud.estado.estado !== TipoEstadoSolicitud.DENEGADA_PROVISIONAL))
        || (this.sgiAuthService.hasAuthorityForAnyUO('CSP-SOL-C') &&
          (solicitud.estado.estado !== TipoEstadoSolicitud.BORRADOR
            && solicitud.estado.estado !== TipoEstadoSolicitud.PRESENTADA
            && solicitud.estado.estado !== TipoEstadoSolicitud.ADMITIDA_PROVISIONAL
            && solicitud.estado.estado !== TipoEstadoSolicitud.ADMITIDA_DEFINITIVA
            && solicitud.estado.estado !== TipoEstadoSolicitud.CONCECIDA_PROVISIONAL
            && solicitud.estado.estado !== TipoEstadoSolicitud.ALEGADA_CONCESION)))) {

        form.controls.estado.disable({ emitEvent: false });
        form.controls.estado.updateValueAndValidity({ emitEvent: false });
        form.controls.solicitante.disable({ emitEvent: false });
        form.controls.solicitante.updateValueAndValidity({ emitEvent: false });
        convocatoriaControl.disable({ emitEvent: false });
        convocatoriaControl.updateValueAndValidity({ emitEvent: false });
        convocatoriaExternaControl.disable({ emitEvent: false });
        convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
        tipoFormularioControl.disable({ emitEvent: false });
        tipoFormularioControl.updateValueAndValidity({ emitEvent: false });
        form.controls.codigoExterno.disable({ emitEvent: false });
        form.controls.codigoExterno.updateValueAndValidity({ emitEvent: false });
        form.controls.codigoRegistro.disable({ emitEvent: false });
        form.controls.codigoRegistro.updateValueAndValidity({ emitEvent: false });
        form.controls.observaciones.disable({ emitEvent: false });
        form.controls.observaciones.updateValueAndValidity({ emitEvent: false });
        unidadGestionControl.disable({ emitEvent: false });
        unidadGestionControl.updateValueAndValidity({ emitEvent: false });

        this.disabledSolicitante = true;


      } else if (convocatoriaSolicitud) {
        unidadGestionControl.disable({ emitEvent: false });
        unidadGestionControl.updateValueAndValidity({ emitEvent: false });
        convocatoriaExternaControl.disable({ emitEvent: false });
        convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
      }
    }

    this.convocatoriaRequired = !convocatoriaExternaSolicitud;
    this.convocatoriaExternaRequired = !convocatoriaSolicitud;

    this.logger.debug(SolicitudDatosGeneralesFragment.name, `setConditionalValidators(form: ${form}, solicitud: ${solicitud})`, 'end');
  }

}
