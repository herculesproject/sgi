import { Fragment } from '@core/services/action-service';
import { Observable, of, BehaviorSubject, from } from 'rxjs';
import { map, switchMap, catchError, mergeMap, takeLast, tap } from 'rxjs/operators';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';


export class ConvocatoriaReunionListadoMemoriasFragment extends Fragment {

  evaluaciones$: BehaviorSubject<StatusWrapper<IEvaluacion>[]> = new BehaviorSubject<StatusWrapper<IEvaluacion>[]>([]);
  private deleted: StatusWrapper<IEvaluacion>[] = [];
  private convocatoriaReunion: ConvocatoriaReunion;

  constructor(key: number, private service: EvaluacionService, private personaFisicaService: PersonaFisicaService) {
    super(key);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadEvaluaciones(this.getKey() as number);
    }
  }

  setConvocatoriaReunion(value: ConvocatoriaReunion) {
    if (!value || value.id !== this.getKey()) {
      Error('Value mistmatch');
    }
    this.convocatoriaReunion = value;
  }

  saveOrUpdate(): Observable<void> {
    return this.createEvaluacion().pipe(
      tap(() => this.evaluaciones$.next(this.evaluaciones$.value))
    );
  }

  public addEvaluacion(evaluacion: IEvaluacion) {
    const wrapped = new StatusWrapper<IEvaluacion>(evaluacion);
    wrapped.setCreated();
    const current = this.evaluaciones$.value;
    current.push(wrapped);
    this.evaluaciones$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  public deleteEvaluacion(evaluacion: StatusWrapper<IEvaluacion>) {
    const current = this.evaluaciones$.value;
    const index = current.findIndex((value) => value === evaluacion);
    if (index >= 0) {
      if (!evaluacion.created) {
        this.deleted.push(current[index]);
      }
      current.splice(index, 1);
      this.evaluaciones$.next(current);
      this.setChanges(true);
    }
    if (current.length === 0) {
      this.setComplete(false);
    }
  }

  private loadEvaluaciones(idConvocatoria: number): void {
    this.service
      .findAllByConvocatoriaReunionIdAndNoEsRevMinima(
        idConvocatoria
      ).pipe(
        map((response) => {
          // Return the values
          return response.items;
        }),
        switchMap((evaluaciones) => {
          if (!evaluaciones || evaluaciones.length === 0) {
            return of([]);
          }

          const personaRefsEvaluadores = new Set<string>();

          evaluaciones.forEach((evaluacion: IEvaluacion) => {
            personaRefsEvaluadores.add(evaluacion?.evaluador1?.personaRef);
            personaRefsEvaluadores.add(evaluacion?.evaluador2?.personaRef);
          });

          return this.personaFisicaService.findByPersonasRefs([...personaRefsEvaluadores]).pipe(
            map((result) => {
              const personas = result.items;

              evaluaciones.forEach((evaluacion: IEvaluacion) => {
                const datosPersonaEvaluador1 = personas.find((persona) =>
                  evaluacion.evaluador1.personaRef === persona.personaRef);
                evaluacion.evaluador1.nombre = datosPersonaEvaluador1?.nombre;
                evaluacion.evaluador1.primerApellido = datosPersonaEvaluador1?.primerApellido;
                evaluacion.evaluador1.segundoApellido = datosPersonaEvaluador1?.segundoApellido;

                const datosPersonaEvaluador2 = personas.find((persona) =>
                  evaluacion.evaluador2.personaRef === persona.personaRef);
                evaluacion.evaluador2.nombre = datosPersonaEvaluador2?.nombre;
                evaluacion.evaluador2.primerApellido = datosPersonaEvaluador2?.primerApellido;
                evaluacion.evaluador2.segundoApellido = datosPersonaEvaluador2?.segundoApellido;
              });

              return evaluaciones;
            }));
        }),
        catchError(() => {
          // On error reset pagination values
          // this.snackBarService.showError('eti.convocatoriaReunion.listado.error');
          return of([]);
        })
      ).subscribe(
        (evaluaciones) => {
          this.evaluaciones$.next(evaluaciones.map((ev) => new StatusWrapper<IEvaluacion>(ev)));
        }
      );
  }

  private createEvaluacion(): Observable<void> {
    const evaluacionesCreadas = this.evaluaciones$.value.filter((evaluacion) => evaluacion.created);
    if (evaluacionesCreadas.length === 0) {
      return of(void 0);
    }
    return from(evaluacionesCreadas).pipe(
      mergeMap((evaluacion) => {
        this.fillEvaluacionOnSave(evaluacion.value);
        return this.service.create(evaluacion.value).pipe(
          map((savedEvaluacion) => {
            const index = this.evaluaciones$.value.findIndex((wrapped) => wrapped === evaluacion);
            this.evaluaciones$[index] = new StatusWrapper<IEvaluacion>(savedEvaluacion);
          })
        );
      }),
      takeLast(1)
    );
  }

  private fillEvaluacionOnSave(evaluacion: IEvaluacion): void {
    evaluacion.convocatoriaReunion = this.convocatoriaReunion;
    evaluacion.version = evaluacion.memoria.version + 1;
    evaluacion.activo = true;
    evaluacion.esRevMinima = false;
    evaluacion.fechaDictamen = this.convocatoriaReunion.fechaEvaluacion;
    evaluacion.tipoEvaluacion = new TipoEvaluacion();
  }
}
