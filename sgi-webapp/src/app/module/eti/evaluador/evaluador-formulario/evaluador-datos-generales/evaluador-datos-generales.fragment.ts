import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class EvaluadorDatosGeneralesFragment extends FormFragment<IEvaluador> {

  private evaluador: IEvaluador;
  private selectedPersona: IPersona;
  private initialPersonaRef: string;

  constructor(private fb: FormBuilder, key: number, private service: EvaluadorService, private personaService: PersonaService) {
    super(key, true);
    this.evaluador = {} as IEvaluador;
    this.evaluador.activo = true;
  }

  setPersona(value: IPersona): void {
    const id = value?.personaRef;
    if (this.isEdit()) {
      this.setChanges(this.initialPersonaRef !== id);
    }
    else if (!this.isEdit()) {
      this.setComplete(value ? true : false);
      this.setChanges(value ? true : false);
    }
    this.selectedPersona = value;
  }

  getPersona(): IPersona {
    return this.selectedPersona;
  }

  get personaText(): string {
    if (this.selectedPersona) {
      const p = this.selectedPersona;
      return `${p.nombre} ${p.primerApellido} ${p.segundoApellido} (${p.identificadorNumero}${p.identificadorLetra})`;
    }
    else {
      return '';
    }
  }

  protected buildFormGroup(): FormGroup {

    return this.fb.group({
      comite: new FormControl({ value: '', disabled: this.isEdit() }, [new NullIdValidador().isValid()]),
      fechaAlta: [null, Validators.required],
      fechaBaja: [null],
      cargoComite: new FormControl({ value: null }, [new NullIdValidador().isValid()]),
      resumen: ['']
    }, {
      validator: [DateValidator.isAfterOrEqual('fechaAlta', 'fechaBaja')]
    });
  }

  protected initializer(key: number): Observable<IEvaluador> {
    return this.service.findById(key).pipe(
      switchMap((value) => {
        this.evaluador = value;
        this.loadPersona(value.persona.personaRef);
        this.initialPersonaRef = this.evaluador.persona.personaRef;
        return of(this.evaluador);
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  buildPatch(value: IEvaluador): { [key: string]: any } {
    return {
      comite: value.comite,
      fechaAlta: value.fechaAlta,
      fechaBaja: value.fechaBaja,
      cargoComite: value.cargoComite,
      resumen: value.resumen
    };
  }

  getValue(): IEvaluador {
    const form = this.getFormGroup().value;
    if (!this.isEdit()) {
      this.evaluador.comite = form.comite;
    }
    this.evaluador.fechaAlta = form.fechaAlta;
    this.evaluador.fechaBaja = form.fechaBaja;
    this.evaluador.cargoComite = form.cargoComite === '' ? null : form.cargoComite;
    this.evaluador.resumen = form.resumen;
    this.evaluador.persona = this.selectedPersona;
    return this.evaluador;
  }

  private loadPersona(personaRef: string) {
    this.personaService
      .findById(personaRef)
      .subscribe(
        (persona) => {
          this.selectedPersona = persona;
        }
      );
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.evaluador = value;
        return this.evaluador.id;
      })
    );
  }
}
