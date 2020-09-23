import { FormFragment } from '@core/services/action-service';
import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Observable, of, EMPTY } from 'rxjs';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { switchMap, catchError, map } from 'rxjs/operators';
import { SgiAuthService } from '@sgi/framework/auth/public-api';

export class PeticionEvaluacionDatosGeneralesFragment extends FormFragment<PeticionEvaluacion> {

  private peticionEvaluacion: PeticionEvaluacion;
  public readonly: boolean;

  constructor(private fb: FormBuilder, key: number, private service: PeticionEvaluacionService,
    private sgiAuthService: SgiAuthService, readonly: boolean) {
    super(key);
    this.peticionEvaluacion = new PeticionEvaluacion();
    this.peticionEvaluacion.activo = true;
    this.peticionEvaluacion.externo = false;
    this.peticionEvaluacion.tieneFondosPropios = false;
    this.peticionEvaluacion.personaRef = sgiAuthService.authStatus$.getValue().userRefId;
    this.readonly = readonly;
  }


  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      titulo: new FormControl({ value: '', disabled: this.isEdit() }, [Validators.required]),
      tipoActividad: new FormControl({ value: '', disabled: this.readonly }, [new NullIdValidador().isValid()]),
      financiacion: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      fechaInicio: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      fechaFin: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      resumen: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      valorSocial: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      objetivosCientificos: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
      disenioMetodologico: new FormControl({ value: '', disabled: this.readonly }, [Validators.required]),
    });
  }

  protected initializer(key: number): Observable<PeticionEvaluacion> {
    return this.service.findById(key).pipe(
      switchMap((value: PeticionEvaluacion) => {
        this.peticionEvaluacion = value;
        return of(value);
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  protected buildPatch(value: PeticionEvaluacion): { [key: string]: any; } {
    return {
      titulo: value.titulo,
      tipoActividad: value.tipoActividad,
      financiacion: value.fuenteFinanciacion,
      fechaInicio: value.fechaInicio,
      fechaFin: value.fechaFin,
      resumen: value.resumen,
      valorSocial: value.valorSocial,
      objetivosCientificos: value.objetivos,
      disenioMetodologico: value.disMetodologico


    };
  }

  getValue(): PeticionEvaluacion {
    const form = this.getFormGroup().value;
    this.peticionEvaluacion.titulo = form.titulo;
    this.peticionEvaluacion.tipoActividad = form.tipoActividad;
    this.peticionEvaluacion.fuenteFinanciacion = form.financiacion;
    this.peticionEvaluacion.fechaInicio = form.fechaInicio;
    this.peticionEvaluacion.fechaFin = form.fechaFin;
    this.peticionEvaluacion.resumen = form.resumen;
    this.peticionEvaluacion.valorSocial = form.valorSocial;
    this.peticionEvaluacion.objetivos = form.objetivosCientificos;
    this.peticionEvaluacion.disMetodologico = form.disenioMetodologico;

    return this.peticionEvaluacion;
  }

  saveOrUpdate(): Observable<number | void> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.peticionEvaluacion = value;
        return this.peticionEvaluacion.id;
      })
    );
  }


}

