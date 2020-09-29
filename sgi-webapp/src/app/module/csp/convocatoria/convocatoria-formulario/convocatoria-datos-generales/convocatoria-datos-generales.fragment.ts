import { FormFragment } from '@core/services/action-service';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, of, EMPTY } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { NullIdValidador } from '@core/validators/null-id-validador';

export class ConvocatoriaDatosGeneralesFragment extends FormFragment<IConvocatoria> {

  private convocatoria: IConvocatoria;

  constructor(private fb: FormBuilder, key: number, private service: ConvocatoriaService) {
    super(key);
    this.convocatoria = {} as IConvocatoria;

  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      referencia: ['', Validators.required],
      estado: ['', Validators.required],
      unidadGestion: ['', new NullIdValidador().isValid()],
      anio: ['', Validators.required],
      titulo: ['', Validators.required],
      modeloEjecucion: ['', new NullIdValidador().isValid()],
      finalidad: ['', new NullIdValidador().isValid()],
      duracionMeses: [''],
      ambitoGeografico: ['', new NullIdValidador().isValid()],
      clasificacionProduccion: [''],
      regimenConcurrencia: [''],
      proyectoColaborativo: [''],
      destinatarios: [''],
      entidadGestora: [''],
      descripcionConvocatoria: [''],
      observaciones: [''],

    });
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    return this.service.findById(key).pipe(
      switchMap((value) => {
        this.convocatoria = value;

        return of(this.convocatoria);
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }


  buildPatch(value: IConvocatoria): { [key: string]: any } {
    return {
      referencia: value.referencia,
      estado: value.estado,
      unidadGestion: value.unidadGestion,
      anio: value.anio,
      titulo: value.titulo,
      modeloEjecucion: value.modeloEjecucion,
      finalidad: value.finalidad,
      duracionMeses: value.duracionMeses,
      ambitoGeografico: value.ambitoGeografico,
      clasificacionProduccion: value.clasificacionProduccion,
      regimenConcurrencia: value.regimenConcurrencia,
      proyectoColaborativo: value.proyectoColaborativo,
      destinatarios: value.destinatarios,
      entidadGestora: value.entidadGestora,
      descripcionConvocatoria: value.descripcionConvocatoria,
      observaciones: value.observaciones,
    };
  }

  getValue(): IConvocatoria {
    const form = this.getFormGroup().value;

    return this.convocatoria;
  }


  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.convocatoria = value;
        return this.convocatoria.id;
      })
    );
  }
}


