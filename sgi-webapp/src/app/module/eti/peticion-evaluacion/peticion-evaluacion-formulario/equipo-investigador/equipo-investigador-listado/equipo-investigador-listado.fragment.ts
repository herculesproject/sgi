import { Fragment } from '@core/services/action-service';
import { Observable, of, BehaviorSubject, from } from 'rxjs';
import { map, mergeMap, endWith, switchMap, tap } from 'rxjs/operators';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { Persona } from '@core/models/sgp/persona';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';

export class EquipoInvestigadorListadoFragment extends Fragment {

  equiposTrabajo$: BehaviorSubject<StatusWrapper<IEquipoTrabajo>[]> = new BehaviorSubject<StatusWrapper<IEquipoTrabajo>[]>([]);
  private deleted: StatusWrapper<IEquipoTrabajo>[] = [];

  private peticionEvaluacion: PeticionEvaluacion;

  private selectedIdPeticionEvaluacion: number;

  constructor(
    key: number,
    private logger: NGXLogger,
    private personaFisicaService: PersonaFisicaService,
    private equipoTrabajoService: EquipoTrabajoService,
    private peticionEvaluacionService: PeticionEvaluacionService,
    private sgiAuthService: SgiAuthService
  ) {
    super(key);
    this.selectedIdPeticionEvaluacion = key;
  }

  onInitialize(): void {
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'onInitialize()', 'start');
    this.loadEquiposTrabajo(this.getKey() as number);
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'onInitialize()', 'end');
  }

  setPeticionEvaluacion(value: PeticionEvaluacion) {
    if (!value || value.id !== this.getKey()) {
      Error('Value mistmatch');
    }
    this.peticionEvaluacion = value;
  }

  loadEquiposTrabajo(idPeticionEvaluacion: number): void {
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'loadEquiposTrabajo(idPeticionEvaluacion: number)', 'start');
    if (!this.isInitialized() || this.selectedIdPeticionEvaluacion !== idPeticionEvaluacion) {
      this.selectedIdPeticionEvaluacion = idPeticionEvaluacion;

      let equiposTrabajoRecuperados$: Observable<StatusWrapper<IEquipoTrabajo>[]>;

      // Si es una petición de evaluación nueva se añade el usuario actual a la lista
      if (!this.selectedIdPeticionEvaluacion) {
        equiposTrabajoRecuperados$ = this.getInvestigadorActual()
          .pipe(
            map((equipoTrabajo: IEquipoTrabajo) => {
              const wrapper = new StatusWrapper<IEquipoTrabajo>(equipoTrabajo);
              wrapper.setCreated();
              return [wrapper];
            }),
            tap(_ => this.setChanges(true))
          );
      } else {
        equiposTrabajoRecuperados$ = this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
          switchMap((response) => {
            const equiposTrabajo = response.items;

            if (response.items) {
              const personaRefsEquiposTrabajo = equiposTrabajo.map((equipoTrabajo: IEquipoTrabajo) => equipoTrabajo.personaRef);
              const equiposTrabajoWithDatosPersona$ = this.personaFisicaService.findByPersonasRefs([...personaRefsEquiposTrabajo]).pipe(
                map((result: SgiRestListResult<Persona>) => {
                  const personas = result.items;

                  equiposTrabajo.forEach((equipoTrabajo: IEquipoTrabajo) => {
                    const datosPersona = personas.find((persona: Persona) =>
                      equipoTrabajo.personaRef === persona.personaRef);

                    equipoTrabajo.nombre = datosPersona?.nombre;
                    equipoTrabajo.primerApellido = datosPersona?.primerApellido;
                    equipoTrabajo.segundoApellido = datosPersona?.segundoApellido;
                    equipoTrabajo.identificadorNumero = datosPersona?.identificadorNumero;
                    equipoTrabajo.identificadorLetra = datosPersona?.identificadorLetra;
                    equipoTrabajo.isEliminable = equipoTrabajo.personaRef !==
                      this.sgiAuthService.authStatus$.getValue().userRefId && equipoTrabajo.isEliminable;
                  });

                  return equiposTrabajo.map((equipoTrabajo) => new StatusWrapper<IEquipoTrabajo>(equipoTrabajo));
                })
              );

              return equiposTrabajoWithDatosPersona$;
            }
            else {
              return of([]);
            }
          })
        );
      }

      equiposTrabajoRecuperados$.subscribe((equiposTrabajo) => {
        this.setComplete(true);
        this.equiposTrabajo$.next(equiposTrabajo);
        this.logger.debug(EquipoInvestigadorListadoFragment.name, 'loadEquiposTrabajo(idPeticionEvaluacion: number)', 'start');
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    const createdEquiposTrabajo = this.equiposTrabajo$.value.filter((equipoTrabajo) => equipoTrabajo.created);
    if (createdEquiposTrabajo.length === 0) {
      return of(void 0);
    }

    return from(createdEquiposTrabajo).pipe(
      mergeMap((wrappedEquipoTrabajo) => {

        if (this.isEdit()) {
          wrappedEquipoTrabajo.value.peticionEvaluacion.id = +this.getKey();
        } else {
          wrappedEquipoTrabajo.value.peticionEvaluacion = this.peticionEvaluacion;
        }

        return this.equipoTrabajoService.create(wrappedEquipoTrabajo.value).pipe(
          map((savedEquipoTrabajo) => {
            const index = this.equiposTrabajo$.value.findIndex((currentEquipoTrabajo) => currentEquipoTrabajo === wrappedEquipoTrabajo);
            this.equiposTrabajo$[index] = new StatusWrapper<IEquipoTrabajo>(savedEquipoTrabajo);
          })
        );
      }),
      endWith()
    );
  }

  /**
   * Añade un nuevo miembro al equipo investigador
   *
   * @param equipoTrabajo un equipoTrabajo
   */
  addEquipoTrabajo(equipoTrabajo: IEquipoTrabajo): void {
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'addEquipoTrabajo(equipoTrabajo: IEquipoTrabajo)', 'start');
    const wrapped = new StatusWrapper<IEquipoTrabajo>(equipoTrabajo);
    wrapped.setCreated();
    const current = this.equiposTrabajo$.value;
    current.push(wrapped);
    this.equiposTrabajo$.next(current);
    this.setChanges(true);
    this.setComplete(true);
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'addEquipoTrabajo(equipoTrabajo: IEquipoTrabajo)', 'end');
  }

  /**
   * Elimina un miembro del equipo investigador
   *
   * @param equipoTrabajo un equipoTrabajo
   */
  deleteEquipoTrabajo(equipoTrabajo: StatusWrapper<IEquipoTrabajo>): void {
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'deleteEquipoTrabajo(equipoTrabajo: StatusWrapper<IEquipoTrabajo>)', 'start');
    const current = this.equiposTrabajo$.value;
    const index = current.findIndex((value) => value === equipoTrabajo);
    if (index >= 0) {
      if (!equipoTrabajo.created) {
        this.deleted.push(current[index]);
      }
      current.splice(index, 1);
      this.equiposTrabajo$.next(current);
      this.setChanges(true);
    }
    if (current.length === 0) {
      this.setComplete(false);
    }

    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'deleteEquipoTrabajo(equipoTrabajo: StatusWrapper<IEquipoTrabajo>)', 'end');
  }

  /**
   * Devuelve un observable con el investigador actual como miembro del equipo de trabajo.
   *
   * @return observable con el investigador actual.
   */
  private getInvestigadorActual(): Observable<IEquipoTrabajo> {
    this.logger.debug(EquipoInvestigadorListadoFragment.name, 'getInvestigadorActual()', 'start');

    return this.personaFisicaService.getInformacionBasica(this.sgiAuthService.authStatus$.getValue().userRefId)
      .pipe(
        map((persona: Persona) => {
          this.logger.debug(EquipoInvestigadorListadoFragment.name, 'getInvestigadorActual()', 'end');
          return {
            id: null,
            peticionEvaluacion: {
              id: this.getKey() as number,
              solicitudConvocatoriaRef: null,
              codigo: null,
              titulo: null,
              tipoActividad: null,
              fuenteFinanciacion: null,
              fechaInicio: null,
              fechaFin: null,
              resumen: null,
              valorSocial: null,
              otroValorSocial: null,
              objetivos: null,
              disMetodologico: null,
              externo: null,
              tieneFondosPropios: null,
              personaRef: null,
              nombre: null,
              primerApellido: null,
              segundoApellido: null,
              identificadorNumero: null,
              identificadorLetra: null,
              nivelAcademico: null,
              vinculacion: null,
              activo: null
            },
            personaRef: persona.personaRef,
            nombre: persona.nombre,
            primerApellido: persona.primerApellido,
            segundoApellido: persona.segundoApellido,
            identificadorNumero: persona.identificadorNumero,
            identificadorLetra: persona.identificadorLetra,
            nivelAcademico: 'Licenciado',
            vinculacion: 'PDI',
            isEliminable: false
          };
        })
      );
  }
}
