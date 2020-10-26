import { FormFragment } from '@core/services/action-service';
import { IMemoria } from '@core/models/eti/memoria';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap, catchError, tap } from 'rxjs/operators';
import { IPersona } from '@core/models/sgp/persona';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { OnDestroy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

export class MemoriaDatosGeneralesFragment extends FormFragment<IMemoria> implements OnDestroy {
  private memoria: IMemoria;
  public readonly: boolean;
  public personasResponsable$: BehaviorSubject<IPersona[]> = new BehaviorSubject<IPersona[]>([]);
  private subscriptionsFragment: Subscription[] = [];
  public mostrarCodOrgano = false;

  constructor(
    private fb: FormBuilder, private logger: NGXLogger, readonly: boolean, key: number, private service: MemoriaService,
    private personaFisicaService: PersonaFisicaService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService) {
    super(key);
    this.memoria = {} as IMemoria;
    this.readonly = readonly;
  }

  public setPeticionEvaluacion(peticionEvaluacion: IPeticionEvaluacion) {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'setPeticionEvaluacion(peticionEvaluacion: IPeticionEvaluacion)', 'start');
    if (!this.getKey()) {
      this.memoria.peticionEvaluacion = peticionEvaluacion;
      if (this.memoria.comite.comite === 'CEEA') {
        this.mostrarCodOrgano = true;
      }
      this.loadResponsable(peticionEvaluacion.id);
    }
    else {
      Error('Value mismatch. Cannot change the value when editing');
    }
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'setPeticionEvaluacion(peticionEvaluacion: IPeticionEvaluacion)', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return this.fb.group({
      comite: [{ value: this.isEdit() ? this.memoria.comite : '', disabled: (this.isEdit() || this.readonly) },
      new NullIdValidador().isValid()],
      tipoMemoria: [{ value: this.isEdit() ? this.memoria.tipoMemoria : '', disabled: (this.isEdit() || this.readonly) }],
      titulo: [{ value: this.isEdit() ? this.memoria.titulo : '', disabled: this.readonly }],
      personaResponsable: [{ value: '', disabled: this.readonly }, Validators.required],
      codOrganoCompetente: [{ value: this.isEdit() ? this.memoria.codOrganoCompetente : '', disabled: this.readonly }]
    });
  }

  buildPatch(value: IMemoria): { [key: string]: any } {
    return {
      comite: value.comite,
      tipoMemoria: value.tipoMemoria,
      titulo: value.titulo,
      personaRef: value.personaRef,
      codOrganoCompetente: value.codOrganoCompetente
    };
  }

  getValue(): IMemoria {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'getValue()', 'start');
    const form = this.getFormGroup().value;
    if (!this.isEdit()) {
      this.memoria.comite = form.comite;
      this.memoria.tipoMemoria = form.tipoMemoria;
    }
    this.memoria.titulo = form.titulo;
    this.memoria.personaRef = form.personaResponsable.personaRef;
    if (this.memoria.comite.comite === 'CEEA') {
      this.memoria.codOrganoCompetente = form.codOrganoCompetente;
    } else {
      this.memoria.codOrganoCompetente = null;
    }
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'getValue()', 'end');
    return this.memoria;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'saveOrUpdate()', 'start');
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(this.getKey() as number, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.memoria = value;
        this.logger.debug(MemoriaDatosGeneralesFragment.name, 'saveOrUpdate()', 'end');
        return this.memoria.id;
      })
    );
  }

  protected initializer(key: number): Observable<IMemoria> {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'initializer(key: number)', 'start');
    if (this.getKey()) {
      return this.service.findById(key).pipe(
        switchMap((memoria) => {
          return this.personaFisicaService.getInformacionBasica(memoria.personaRef).pipe(
            map((persona) => {
              this.getFormGroup().controls.personaResponsable.setValue(persona);
              this.memoria = memoria;
              return memoria;
            }),
            catchError(() => of(memoria)),
          );
        }),
        tap((memoria) => {
          this.loadResponsable(memoria.peticionEvaluacion.id);
        })
      );
    }
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'initializer(key: number)', 'end');
  }

  loadResponsable(idPeticionEvaluacion: number): void {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'loadResponsable(idPeticionEvaluacion: number)', 'start');
    this.subscriptionsFragment.push(
      this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
        map((response) => {
          const equiposTrabajo = response.items;
          if (response.items) {
            const personaRefsEquiposTrabajo = equiposTrabajo.map((equipoTrabajo: IEquipoTrabajo) => equipoTrabajo.personaRef);
            this.personaFisicaService.findByPersonasRefs([...personaRefsEquiposTrabajo]).pipe(
              map(
                responsePersonas => {
                  return responsePersonas.items;
                }
              )
            )
              .subscribe((persona) => {
                this.personasResponsable$.next(persona);
              });
          }
        })
      ).subscribe());
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'loadResponsable(idPeticionEvaluacion: number)', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptionsFragment.forEach(x => x.unsubscribe());
    this.logger.debug(MemoriaDatosGeneralesFragment.name, 'ngOnDestroy()', 'end');
  }
}
