import { FormFragment } from '@core/services/action-service';
import { IMemoria } from '@core/models/eti/memoria';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { Observable, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { IPersona } from '@core/models/sgp/persona';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

export class MemoriaDatosGeneralesFragment extends FormFragment<IMemoria> {
  private memoria: IMemoria;
  private selectedPersona: IPersona;
  public readonly: boolean;

  constructor(private fb: FormBuilder, readonly: boolean, key: number, private service: MemoriaService,
    private personaFisicaService: PersonaFisicaService) {
    super(key);
    this.memoria = {} as IMemoria;
    this.readonly = readonly;
  }

  public setPeticionEvaluacion(peticionEvaluacion: IPeticionEvaluacion) {
    if (!this.getKey()) {
      this.memoria.peticionEvaluacion = peticionEvaluacion;
    }
    else {
      Error('Value mismatch. Cannot change the value when editing');
    }
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      comite: [{ value: '', disabled: this.readonly }, new NullIdValidador().isValid()],
      tipoEstadoMemoria: [{ value: '', disabled: this.readonly }],
      titulo: [{ value: this.isEdit() ? this.memoria.titulo : '', disabled: this.readonly }],
      personaRef: [{ value: '', disabled: this.readonly }],
    });
  }

  buildPatch(value: IMemoria): { [key: string]: any } {
    return {
      comite: value.comite,
      tipoEstadoMemoria: value.tipoMemoria,
      titulo: value.titulo,
      personaRef: value.personaRef
    };
  }

  getValue(): IMemoria {
    const form = this.getFormGroup().value;
    this.memoria.comite = form.comite;
    this.memoria.tipoMemoria = form.tipoEstadoMemoria;
    this.memoria.titulo = form.titulo;
    this.memoria.personaRef = form.personaRef;
    return this.memoria;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.memoria = value;
        return this.memoria.id;
      })
    );
  }

  protected initializer(key: number): Observable<IMemoria> {
    if (this.getKey()) {
      return this.service.findById(key).pipe(
        switchMap((memoria) => {
          return this.personaFisicaService.getInformacionBasica(memoria.personaRef).pipe(
            map((persona) => {
              this.selectedPersona = persona;
              return memoria;
            }),
            catchError((e) => of(memoria)),
          );
        })
      );
    }
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

  /**
   * Setea el persona seleccionado a través del componente
   * @param persona Persona seleccionada
   */
  public setPersona(persona: IPersona) {
    this.selectedPersona = persona;
    this.getFormGroup().controls.personaRef.setValue(persona.personaRef);
  }
}
