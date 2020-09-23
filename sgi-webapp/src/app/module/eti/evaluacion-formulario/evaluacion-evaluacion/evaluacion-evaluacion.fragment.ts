import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { IMemoria } from '@core/models/eti/memoria';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { Persona } from '@core/models/sgp/persona';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { NGXLogger } from 'ngx-logger';
import { StatusWrapper } from '@core/utils/status-wrapper';

interface MemoriaWithPersona extends IMemoria {
  solicitante: Persona;
}

export class EvaluacionEvaluacionFragment extends FormFragment<MemoriaWithPersona> {

  private memoria: MemoriaWithPersona;
  dictamenListado: IDictamen[] = [];
  filteredDictamenes: Observable<IDictamen[]>;
  evaluacion$: BehaviorSubject<IEvaluacion> = new BehaviorSubject<IEvaluacion>(null);
  evaluacion: IEvaluacion;
  evaluacionActualizada: StatusWrapper<IEvaluacion> = new StatusWrapper<IEvaluacion>(null);

  constructor(
    private readonly logger: NGXLogger,
    private fb: FormBuilder, key: number,
    private service: EvaluacionService,
    private personaFisicaService: PersonaFisicaService) {
    super(key);
    this.memoria = {} as MemoriaWithPersona;
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'buildFormGroup()', 'start');
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'buildFormGroup()', 'end');

    return this.fb.group({
      comite: [{ value: '', disabled: true }],
      fechaEvaluacion: [{ value: '', disabled: true }],
      referenciaMemoria: [{ value: '', disabled: true }],
      solicitante: [{ value: '', disabled: true }],
      version: [{ value: '', disabled: true }],
      dictamen: ['', [Validators.required, new NullIdValidador().isValid()]]
    });
  }

  protected initializer(key: number): Observable<MemoriaWithPersona> {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'initializer()', 'start');
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'initializer()', 'end');

    return this.service.findById(key).pipe(
      map((evaluacion) => {
        this.memoria = evaluacion.memoria as MemoriaWithPersona;
        this.evaluacion$.next(evaluacion);
        this.evaluacion = evaluacion;
        return this.memoria;
      }),
      switchMap((memoria) => {
        if (memoria.personaRef) {
          return this.personaFisicaService.getInformacionBasica(memoria.peticionEvaluacion.personaRef).pipe(
            map((persona) => {
              memoria.solicitante = persona;
              return memoria;
            }),
            catchError((e) => of(memoria)),
          );
        }
        else {
          return of(memoria);
        }
      })
    );
  }

  buildPatch(value: MemoriaWithPersona): { [key: string]: any } {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'buildPatch()', 'start');
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'buildPatch()', 'end');
    return {
      comite: value.comite.comite,
      fechaEvaluacion: value.fechaEnvioSecretaria,
      referenciaMemoria: value.numReferencia,
      version: value.version,
      solicitante: `${value?.solicitante?.nombre} ${value?.solicitante?.primerApellido} ${value?.solicitante?.segundoApellido}`
    };
  }

  getValue(): MemoriaWithPersona {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getValue()', 'start');
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getValue()', 'end');
    return this.memoria;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'saveOrUpdate()', 'start');

    this.evaluacion = this.getValueFormDictamen();
    // Si es Favorable pendiente de revisión mínima o Pendiente de correcciones
    // comprobar si hay comentarios o no, en el caso de que si se hace el update
    const obs = this.isEdit() ? this.service.update(this.evaluacion.id, this.evaluacion) : this.service.create(this.evaluacion);

    this.logger.debug(EvaluacionEvaluacionFragment.name, 'saveOrUpdate()', 'end');
    return obs.pipe(
      map((value) => {
        this.evaluacion = value;
        return this.evaluacion.id;
      })
    );
  }

  getValueFormDictamen(): IEvaluacion {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getValueFormDictamen()', 'start');
    const form = this.getFormGroup().value;
    this.evaluacion.dictamen = form.dictamen;
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getValueFormDictamen()', 'end');
    return this.evaluacion;
  }

}

