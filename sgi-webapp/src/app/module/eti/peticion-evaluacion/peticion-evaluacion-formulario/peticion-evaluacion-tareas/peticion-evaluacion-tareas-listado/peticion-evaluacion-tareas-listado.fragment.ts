import { Fragment } from '@core/services/action-service';
import { Observable, of, BehaviorSubject, from } from 'rxjs';
import { map, mergeMap, endWith } from 'rxjs/operators';
import { ITarea } from '@core/models/eti/tarea';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';

export class PeticionEvaluacionTareasFragment extends Fragment {

  tareas$: BehaviorSubject<StatusWrapper<ITarea>[]> = new BehaviorSubject<StatusWrapper<ITarea>[]>([]);
  filter: SgiRestFilter[];

  constructor(key: number,
    private personaService: PersonaFisicaService,
    private tareaService: TareaService) {
    super(key);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadTareas(this.getKey() as number);
    }
  }

  loadTareas(idPeticionEvaluacion: number): void {
    this.tareaService.findAll({
      filters: this.buildFiltersTarea(idPeticionEvaluacion)
    }).pipe(
      map((response) => {
        if (response.items) {
          response.items.forEach((tarea) => {
            this.personaService.getInformacionBasica(tarea.equipoTrabajo.personaRef).pipe(
              map((usuarioInfo) => {
                tarea.equipoTrabajo.identificadorNumero = usuarioInfo.identificadorNumero;
                tarea.equipoTrabajo.nombre = usuarioInfo.nombre;
                tarea.equipoTrabajo.primerApellido = usuarioInfo.primerApellido;
                tarea.equipoTrabajo.segundoApellido = usuarioInfo.segundoApellido;
              })
            ).subscribe();
          });
          return response.items.map((tarea) => new StatusWrapper<ITarea>(tarea));
        }
        else {
          return [];
        }
      })
    ).subscribe((tareas) => {
      this.tareas$.next(tareas);
    });
  }

  private buildFiltersTarea(idPeticionEvaluacion: number): SgiRestFilter[] {

    this.filter = [];

    if (idPeticionEvaluacion) {
      const filterMemoriaPeticionEvaluacion: SgiRestFilter = {
        field: 'equipoTrabajo.peticionEvaluacion.id',
        type: SgiRestFilterType.EQUALS,
        value: idPeticionEvaluacion.toString(),
      };
      this.filter.push(filterMemoriaPeticionEvaluacion);
    }

    return this.filter;
  }

  /**
   * Añade una nueva tarea
   *
   * @param tarea una tarea
   */
  addTarea(tarea: ITarea): void {
    const wrapped = new StatusWrapper<ITarea>(tarea);
    wrapped.setCreated();
    const current = this.tareas$.value;
    current.push(wrapped);
    this.tareas$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  saveOrUpdate(): Observable<void> {
    const editedTareas = this.tareas$.value.filter((tarea) => tarea.edited || tarea.created);
    if (editedTareas.length === 0) {
      return of(void 0);
    }
    return from(editedTareas).pipe(
      mergeMap((wrappedTarea) => {

        wrappedTarea.value.equipoTrabajo.peticionEvaluacion.id = this.getKey() as number;

        if (wrappedTarea.value.id) {
          return this.tareaService.update(wrappedTarea.value.id, wrappedTarea.value).pipe(
            map((updatedTarea) => {
              const index = this.tareas$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
              this.tareas$[index] = new StatusWrapper<ITarea>(updatedTarea);
            })
          );
        } else {
          return this.tareaService.create(wrappedTarea.value).pipe(
            map((savedTarea) => {
              const index = this.tareas$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
              this.tareas$[index] = new StatusWrapper<ITarea>(savedTarea);
            })
          );
        }
      }),
      endWith()
    );
  }
}
