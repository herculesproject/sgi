import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { Memoria } from '@core/models/eti/memoria';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { Persona } from '@core/models/sgp/persona';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

interface MemoriaWithPersona extends Memoria {
  solicitante: Persona;
}

export class EvaluacionDatosMemoriaFragment extends FormFragment<MemoriaWithPersona> {

  private memoria: MemoriaWithPersona;

  constructor(private fb: FormBuilder, key: number, private service: EvaluacionService, private personaFisicaService: PersonaFisicaService) {
    super(key);
    this.memoria = new Memoria() as MemoriaWithPersona;
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      comite: [{ value: '', disabled: true }],
      fechaEvaluacion: [{ value: '', disabled: true }],
      referenciaMemoria: [{ value: '', disabled: true }],
      solicitante: [{ value: '', disabled: true }],
      version: [{ value: '', disabled: true }]
    });
  }

  protected initializer(key: number): Observable<MemoriaWithPersona> {
    return this.service.findById(key).pipe(
      map((evaluacion) => {
        this.memoria = evaluacion.memoria as MemoriaWithPersona;
        return this.memoria;
      }),
      switchMap((memoria) => {
        if (memoria.personaRef) {
          return this.personaFisicaService.getInformacionBasica(memoria.personaRef).pipe(
            map((persona) => {
              memoria.solicitante = persona;
              return memoria;
            })
          );
        }
        else {
          return of(memoria);
        }
      })
    );
  }

  buildPatch(value: MemoriaWithPersona): { [key: string]: any } {
    return {
      comite: value.comite.comite,
      fechaEvaluacion: value.fechaEnvioSecretaria,
      referenciaMemoria: value.numReferencia,
      version: value.version,
      solicitante: `${value?.solicitante?.nombre} ${value?.solicitante?.primerApellido} ${value?.solicitante?.segundoApellido}`
    };
  }

  getValue(): MemoriaWithPersona {
    return this.memoria;
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
