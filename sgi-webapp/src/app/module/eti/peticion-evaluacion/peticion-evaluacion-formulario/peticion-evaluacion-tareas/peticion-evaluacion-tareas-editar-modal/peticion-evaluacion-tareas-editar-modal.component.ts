import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ITarea } from '@core/models/eti/tarea';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Subscription, Observable, of, BehaviorSubject } from 'rxjs';
import { TareaService } from '@core/services/eti/tarea.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormacionEspecifica } from '@core/models/eti/formacion-especifica';
import { FormacionEspecificaService } from '@core/services/eti/formacion-especifica.service';
import { startWith, map } from 'rxjs/operators';
import { IMemoria } from '@core/models/eti/memoria';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { Persona } from '@core/models/sgp/persona';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

const MSG_SUCCESS = marker('eti.acta.asistentes.correcto');
const MSG_ERROR = marker('eti.acta.asistentes.error');
const MSG_ERROR_FORM = marker('form-group.error');

@Component({
  selector: 'sgi-peticion-evaluacion-tareas-editar-modal',
  templateUrl: './peticion-evaluacion-tareas-editar-modal.component.html',
  styleUrls: ['./peticion-evaluacion-tareas-editar-modal.component.scss']
})
export class PeticionEvaluacionTareasEditarModalComponent implements OnInit, OnDestroy {

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  tareaSuscripcion: Subscription;

  formacionListado: FormacionEspecifica[];
  formacionesSubscription: Subscription;
  filteredFormaciones: Observable<FormacionEspecifica[]>;

  memoriaListado: IMemoria[];
  memoriasSubscription: Subscription;
  filteredMemorias: Observable<IMemoria[]>;

  equipoTrabajoListado: IEquipoTrabajo[];
  equiposTrabajoSubscription: Subscription;
  filteredEquiposTrabajo: Observable<IEquipoTrabajo[]>;

  personaServiceOneSubscritpion: Subscription;

  mostrarOrganismo: boolean;
  mostrarOrganismo$ = new BehaviorSubject(false);
  mostrarOrganismoSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    public readonly matDialogRef: MatDialogRef<PeticionEvaluacionTareasEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tarea: ITarea,
    private readonly snackBarService: SnackBarService,
    protected readonly tareaService: TareaService,
    protected readonly formacionService: FormacionEspecificaService,
    protected readonly memoriaService: MemoriaService,
    protected readonly equipoTrabajoService: EquipoTrabajoService,
    protected readonly personaFisicaService: PersonaFisicaService
  ) {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'ngOnInit()', 'start');
    this.mostrarOrganismoSubscription = this.mostrarOrganismo$.subscribe(mostrar => {
      this.mostrarOrganismo = mostrar;
    });
    this.initFormGroup();
    this.getFormaciones();
    this.getMemorias();
    this.getEquiposTrabajo();

    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'ngOnInit()', 'end');

  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'initFormGroup()', 'start');
    this.formGroup = new FormGroup({
      tarea: new FormControl(this.tarea.tarea, [Validators.required]),
      organismo: new FormControl(this.tarea.organismo),
      anio: new FormControl(this.tarea.anio),
      formacionEspecifica: new FormControl(this.tarea.formacionEspecifica, [Validators.required]),
      memoria: new FormControl(this.tarea.memoria, [Validators.required]),
      equipoTrabajo: new FormControl(this.tarea.equipoTrabajo, [Validators.required])
    });
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'initFormGroup()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve el asistencia si se ha modificado
   *
   * @param tarea asistencia modificada
   */
  closeModal(tarea?: ITarea): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(tarea);
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'closeModal()', 'end');
  }

  /**
   * Comprueba el formulario y envia la tarea resultante
   */
  editTarea() {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'editTarea()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'editTarea()', 'end');
  }

  /**
   * Devuelve el nombre de una formación específica.
   * @param formacion formación específica
   * returns nombre de la formación específica
   */
  getFormacion(formacion: FormacionEspecifica): string {

    return formacion?.nombre;

  }

  /**
   * Recupera un listado de las formaciones escíficas que hay en el sistema.
   */
  getFormaciones(): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getFormaciones()',
      'start');

    this.formacionesSubscription = this.formacionService.findAll().subscribe(
      (response) => {
        this.formacionListado = response.items;

        this.filteredFormaciones = this.formGroup.controls.formacionEspecifica.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterFormacion(value))
          );
      });

    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getFormaciones()',
      'end');
  }

  /**
   * Filtro de campo autocompletable formación específica
   * @param value value a filtrar (string o nombre formación).
   * @returns lista de formaciones filtradas.
   */
  private filterFormacion(value: string | FormacionEspecifica): FormacionEspecifica[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.formacionListado.filter
      (formacion => formacion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el titulo de una memoria
   * @param memoria memoria
   * returns título de la memoria
   */
  getMemoria(memoria: IMemoria): string {

    // if (memoria.comite.comite === 'CEIAB' || memoria.comite.comite === 'CEEA') {
    //   this.mostrarOrganismo$.next(true);
    //   this.mostrarAnio = true;
    // } else {
    //   this.mostrarOrganismo$.next(false);
    //   this.mostrarAnio = false;
    // }

    // if (memoria.comite.comite === 'CEISH' || memoria.comite.comite === 'CEIAB') {
    //   this.tareaTexto$.next(true);
    //   this.formacionTexto = true;

    // } else {
    //   this.tareaTexto$.next(false);
    //   this.formacionTexto = false;
    // }

    return memoria?.titulo;

  }

  /**
   * Recupera un listado de las memorias que hay en el sistema.
   */
  getMemorias(): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getMemorias()',
      'start');

    this.memoriasSubscription = this.memoriaService.findAll().subscribe(
      (response) => {
        this.memoriaListado = response.items;

        this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterMemoria(value))
          );
      });

    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getMemorias()',
      'end');
  }

  /**
   * Filtro de campo autocompletable de memoria
   * @param value value a filtrar (string o título memoria).
   * @returns lista de memorias filtradas.
   */
  private filterMemoria(value: string | IMemoria): IMemoria[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.titulo.toLowerCase();
    }

    return this.memoriaListado.filter
      (memoria => memoria.titulo.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve la persona del equipo de trabajo
   * @param equipoTrabajo el equipo de trabajo
   * returns persona del equipo de trabajo
   */
  getEquipoTrabajo(equipoTrabajo: IEquipoTrabajo): string {

    return equipoTrabajo?.nombre + ' ' + equipoTrabajo?.primerApellido + ' ' + equipoTrabajo?.segundoApellido;

  }

  /**
   * Recupera un listado de los equipo de trabajo que hay en el sistema.
   */
  getEquiposTrabajo(): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getEquiposTrabajo()',
      'start');

    this.equiposTrabajoSubscription = this.equipoTrabajoService.findAll().subscribe(
      (response) => {
        this.loadDatosUsuario(response.items);

        this.filteredEquiposTrabajo = this.formGroup.controls.equipoTrabajo.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterEquipoTrabajo(value))
          );
      });

    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name,
      'getEquiposTrabajo()',
      'end');
  }

  /**
   * Devuelve los datos de persona de los equipos de trabajo
   * @param equiposTrabajo listado de equipos de trabajo
   * returns los equipos de trabajo con los datos de persona
   */
  loadDatosUsuario(equiposTrabajo: IEquipoTrabajo[]) {
    this.equipoTrabajoListado = [];
    equiposTrabajo.forEach(equipoTrabajo => {
      this.personaServiceOneSubscritpion = this.personaFisicaService.getInformacionBasica(equipoTrabajo.personaRef)
        .subscribe(
          (persona: Persona) => {
            equipoTrabajo.nombre = persona.nombre;
            equipoTrabajo.primerApellido = persona.primerApellido;
            equipoTrabajo.segundoApellido = persona.segundoApellido;
            equipoTrabajo.identificadorNumero = persona.identificadorNumero;
            equipoTrabajo.identificadorLetra = persona.identificadorLetra;
            this.equipoTrabajoListado.push(equipoTrabajo);
          },
          () => {
            this.snackBarService.showError('eti.peticionEvaluacion.tareas.personas.no-encontrado');
            this.logger.debug(
              PeticionEvaluacionTareasEditarModalComponent.name,
              'loadDatosUsuario()',
              'end'
            );
          }
        );
    });
  }

  /**
   * Filtro de campo autocompletable del equipo de trabajo
   * @param value value a filtrar (string o persona equipo de trabajo).
   * @returns lista de memorias filtradas.
   */
  private filterEquipoTrabajo(value: string | IEquipoTrabajo): IEquipoTrabajo[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase() + ' ' + value.primerApellido.toLowerCase() + ' ' + value.segundoApellido.toLowerCase();
    }

    return this.equipoTrabajoListado.filter
      (equipoTrabajo => (equipoTrabajo.nombre.toLowerCase() + ' ' + equipoTrabajo.primerApellido.toLowerCase() + ' ' +
        equipoTrabajo.segundoApellido.toLowerCase()).includes(filterValue));
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): ITarea {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'getDatosForm()', 'start');
    const tarea = this.tarea;
    tarea.tarea = FormGroupUtil.getValue(this.formGroup, 'tarea');
    tarea.organismo = FormGroupUtil.getValue(this.formGroup, 'organismo');
    tarea.anio = FormGroupUtil.getValue(this.formGroup, 'anio');
    tarea.formacionEspecifica = FormGroupUtil.getValue(this.formGroup, 'formacionEspecifica');
    tarea.memoria = FormGroupUtil.getValue(this.formGroup, 'memoria');
    tarea.equipoTrabajo = FormGroupUtil.getValue(this.formGroup, 'equipoTrabajo');
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'getDatosForm()', 'end');
    return tarea;
  }

  ngOnDestroy(): void {
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'ngOnDestroy()', 'start');
    this.tareaSuscripcion?.unsubscribe();
    this.mostrarOrganismo$?.unsubscribe();
    this.logger.debug(PeticionEvaluacionTareasEditarModalComponent.name, 'ngOnDestroy()', 'end');
  }

}
