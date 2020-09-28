import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IAsistente } from '@core/models/eti/asistente';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { FormFragment } from '@core/services/action-service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { HoraValidador } from '@core/validators/hora-validator';
import { MinutoValidador } from '@core/validators/minuto-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { EMPTY, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

export class ConvocatoriaReunionDatosGeneralesFragment extends FormFragment<ConvocatoriaReunion> {

  private convocatoriaReunion: ConvocatoriaReunion;
  convocantes: IEvaluador[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private service: ConvocatoriaReunionService,
    private asistenteService: AsistenteService
  ) {
    super(key);
    this.convocatoriaReunion = new ConvocatoriaReunion();
    this.convocatoriaReunion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      comite: ['', new NullIdValidador().isValid()],
      fechaEvaluacion: ['', Validators.required],
      fechaLimite: ['', Validators.required],
      tipoConvocatoriaReunion: ['', new NullIdValidador().isValid()],
      horaInicio: ['', new HoraValidador().isValid()],
      minutoInicio: ['', new MinutoValidador().isValid()],
      lugar: ['', Validators.required],
      ordenDia: ['', Validators.required],
      convocantes: ['']
    });
  }

  protected initializer(key: number): Observable<ConvocatoriaReunion> {
    return this.service.findById(key).pipe(
      switchMap((value) => {
        this.convocatoriaReunion = value;
        return of(this.convocatoriaReunion);
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  buildPatch(value: ConvocatoriaReunion): { [key: string]: any } {
    return {
      comite: value.comite,
      fechaEvaluacion: value.fechaEvaluacion,
      fechaLimite: value.fechaLimite,
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      lugar: value.lugar,
      ordenDia: value.ordenDia
    };
  }

  getValue(): ConvocatoriaReunion {
    const form = this.getFormGroup().value;
    this.convocatoriaReunion.comite = form.comite;
    this.convocatoriaReunion.fechaEvaluacion = form.fechaEvaluacion;
    this.convocatoriaReunion.fechaLimite = form.fechaLimite;
    this.convocatoriaReunion.tipoConvocatoriaReunion = form.tipoConvocatoriaReunion;
    this.convocatoriaReunion.horaInicio = form.horaInicio;
    this.convocatoriaReunion.minutoInicio = form.minutoInicio;
    this.convocatoriaReunion.lugar = form.lugar;
    this.convocatoriaReunion.ordenDia = form.ordenDia;
    return this.convocatoriaReunion;
  }

  saveOrUpdate(): Observable<number> {
    return this.create().pipe(
      map((convocatoria) => {
        this.convocatoriaReunion = convocatoria;
        return convocatoria.id;
      })
    );
  }

  private createAsistentes(convocatoriaReunion: ConvocatoriaReunion): Observable<ConvocatoriaReunion> {
    const convocantes: IEvaluador[] = this.getFormGroup().controls.convocantes.value;
    const asistentes: IAsistente[] = convocantes.map((convocante) => {
      return {
        id: null,
        convocatoriaReunion,
        asistencia: true,
        evaluador: convocante,
        motivo: null
      };
    });
    return from(asistentes).pipe(
      mergeMap((asistente) => {
        return this.asistenteService.create(asistente).pipe(
          map((createdAsistente) => {
            convocatoriaReunion.convocantes.push(createdAsistente);
          })
        );
      }),
      takeLast(1),
      map(() => convocatoriaReunion)
    );
  }

  private create(): Observable<ConvocatoriaReunion> {
    return this.service.create(this.getValue()).pipe(
      switchMap((convocatoriaReunion) => {
        return this.createAsistentes(convocatoriaReunion);
      })
    );
  }
}
