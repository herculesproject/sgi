import { FormFragment } from '@core/services/action-service';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable, of, EMPTY, BehaviorSubject } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { SgiRestListResult } from '@sgi/framework/http';

export class ConvocatoriaDatosGeneralesFragment extends FormFragment<IConvocatoria> {

  convocatoriaAreaTematicas$ = new BehaviorSubject<StatusWrapper<IConvocatoriaAreaTematica>[]>([]);

  private convocatoria: IConvocatoria;
  private selectedEmpresaEconomica: IEmpresaEconomica;
  private initialPersonaRef: string;

  constructor(
    private fb: FormBuilder,
    key: number,
    private service: ConvocatoriaService,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
    super(key);
    this.convocatoria = {} as IConvocatoria;
  }

  setEmpresaEconomica(value: IEmpresaEconomica): void {
    const id = value?.personaRef;
    if (this.isEdit()) {
      this.setChanges(this.initialPersonaRef !== id);
    } else if (!this.isEdit()) {
      this.setComplete(!!value);
      this.setChanges(!!value);
    }

    this.selectedEmpresaEconomica = value;
  }

  getPersona(): IEmpresaEconomica {
    return this.selectedEmpresaEconomica;
  }

  get empresaEconomicaText(): string {
    if (this.selectedEmpresaEconomica) {
      return this.selectedEmpresaEconomica.razonSocial;
    } else {
      return '';
    }
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      referencia: [''],
      estado: [''],
      unidadGestion: ['', IsEntityValidator.isValid()],
      anio: [''],
      titulo: [''],
      modeloEjecucion: ['', IsEntityValidator.isValid()],
      finalidad: ['', IsEntityValidator.isValid()],
      duracionMeses: [''],
      tipoAmbitoGeografico: ['', new NullIdValidador().isValid()],
      clasificacionProduccion: [''],
      tipoRegimenConcurrencia: [''],
      proyectoColaborativo: [''],
      destinatarios: [''],
      entidadGestora: [''],
      descripcionConvocatoria: [''],
      observaciones: [''],
    });
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    if (this.getKey()) {
      this.loadAreasTematicas(this.getKey() as number);
      return this.service.findById(key).pipe(
        switchMap((value) => {
          this.convocatoria = value;
          this.loadEmpresaEconomica(value.entidadGestora);
          this.initialPersonaRef = this.convocatoria.entidadGestora;
          return of(this.convocatoria);
        }),
        catchError(() => {
          return EMPTY;
        })
      );
    }
  }

  loadAreasTematicas(id: number): void {
    this.service.findAreaTematicas(id).pipe(
      map((response: SgiRestListResult<IConvocatoriaAreaTematica>) => response.items)
    ).subscribe((areasTematicas: IConvocatoriaAreaTematica[]) => {
      this.convocatoriaAreaTematicas$.next(
        areasTematicas.map(areaTematica =>
          new StatusWrapper<IConvocatoriaAreaTematica>(areaTematica)
        )
      );
    });
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
      tipoAmbitoGeografico: value.tipoAmbitoGeografico,
      clasificacionProduccion: value.clasificacionProduccion,
      tipoRegimenConcurrencia: value.tipoRegimenConcurrencia,
      proyectoColaborativo: value.proyectoColaborativo,
      destinatarios: value.destinatarios,
      entidadGestora: value.entidadGestora,
      descripcionConvocatoria: value.descripcionConvocatoria,
      observaciones: value.observaciones,
    };
  }

  getValue(): IConvocatoria {
    const form = this.getFormGroup().value;

    this.convocatoria.entidadGestora = this.selectedEmpresaEconomica?.personaRef;

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

  private loadEmpresaEconomica(personaRef: string) {
    this.empresaEconomicaService
      .findById(personaRef)
      .subscribe(
        (empresaEconomica) => {
          this.selectedEmpresaEconomica = empresaEconomica;
        }
      );
  }

}
