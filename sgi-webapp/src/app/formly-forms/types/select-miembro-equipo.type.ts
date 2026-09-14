import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, Subscription } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

const SGP_NOT_FOUND = marker('error.sgp.not-found');

@Component({
  template: `
    <mat-select
      [id]="id"
      [formControl]="formControl"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabIndex]="to.tabindex"
      [required]="to.required"
      [compareWith]="to.compareWith"
      [multiple]="to.multiple"
      (selectionChange)="change($event)"
      [errorStateMatcher]="errorStateMatcher"
      [aria-labelledby]="_getAriaLabelledby()"
      [disableOptionCentering]="to.disableOptionCentering"
    >
      <ng-container *ngFor="let item of selectOptions">
        <mat-option [value]="item.value" [class.missing]="item.missing">
          {{ item.label }}
        </mat-option>
      </ng-container>
    </mat-select>
  `,
  styles: [`
    .mat-option.missing {
      text-decoration: line-through;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectMiembrosEquipoTypeComponent extends FieldType implements OnInit, OnDestroy {

  private readonly subscriptions: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private readonly translate: TranslateService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService,
    private readonly datosAcademicosService: DatosAcademicosService,
  ) {
    super();
  }

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith: (o1: any, o2: any) => o1?.id === o2?.id,
    },
  };

  ngOnInit(): void {
    const peticionEvaluacionId = this.formState?.memoria?.peticionEvaluacion?.id;

    if (!peticionEvaluacionId) {
      this.setOptions([]);
      return;
    }

    this.subscriptions.push(
      this.loadMiembrosEquipoInvestigador(peticionEvaluacionId)
        .pipe(
          map(miembros =>
            miembros.map(miembro => ({
              label: this.getOptionLabel(miembro.persona),
              value: miembro.persona,
              missing: false
            }))
          )
        )
        .subscribe(options => this.setOptions(options))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get selectOptions(): any[] {
    return this.field?.templateOptions?.options as any[];
  }

  private setOptions(options: any[]): void {
    this.to.options = options;
    this.addMissingOptionIfNecessary();
    this.changeDetectorRef.markForCheck();
  }

  /**
   * Añade las opciones correspondientes a los valores seleccionados que no estén entre las recuperadas.
   */
  private addMissingOptionIfNecessary(): void {
    const value = this.formControl?.value;
    const selectedValues: IPersona[] = (Array.isArray(value) ? value : [value]).filter(Boolean);

    selectedValues.forEach(selectedValue => {
      const exists = this.selectOptions.some(option => this.to.compareWith(option.value, selectedValue));
      if (!exists) {
        const missingOption = {
          label: this.getOptionLabel(selectedValue),
          value: selectedValue,
          missing: true
        };

        this.selectOptions.push(missingOption);
      }
    });
  }

  change($event: MatSelectChange) {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

  private loadMiembrosEquipoInvestigador(idPeticionEvaluacion: number): Observable<IEquipoTrabajoWithIsEliminable[]> {
    return this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
      map(res => res.items),
      switchMap(miembros => {
        if (!miembros?.length) {
          return of([]);
        }

        return forkJoin(miembros.map(miembro => this.populateMiembroEquipo(miembro)));
      })
    );
  }

  /**
   * Completa los datos de la persona de un miembro del equipo de trabajo.
   *
   * @param miembro el miembro del equipo de trabajo
   * @returns el miembro con los datos de la persona que se hayan podido recuperar
   */
  private populateMiembroEquipo(miembro: IEquipoTrabajoWithIsEliminable): Observable<IEquipoTrabajoWithIsEliminable> {
    return this.personaService.findById(miembro.persona.id).pipe(
      map(persona => {
        miembro.persona = persona;
        return miembro;
      }),
      catchError(error => {
        this.logger.error(`Error recuperando persona - personaId: ${miembro.persona.id}`, error);
        return of(miembro);
      }),
      switchMap(() => {
        if (!miembro.persona?.nombre) {
          // Si no se pueden recuperar los datos basicos de la persona no se intentan recuperar datos extra
          return of(miembro);
        }

        return forkJoin({
          datosAcademicos: this.datosAcademicosService.findByPersonaId(miembro.persona.id).pipe(
            catchError(error => {
              this.logger.error(`Error recuperando datos academicos - personaId: ${miembro.persona.id}`, error);
              return of(null);
            })
          ),
          vinculacion: this.vinculacionService.findByPersonaId(miembro.persona.id).pipe(
            catchError(error => {
              this.logger.error(`Error recuperando vinculacion - personaId: ${miembro.persona.id}`, error);
              return of(null);
            })
          )
        }).pipe(
          map(({ datosAcademicos, vinculacion }) => {
            miembro.persona.datosAcademicos = datosAcademicos;
            miembro.persona.vinculacion = vinculacion;
            return miembro;
          })
        );
      })
    );
  }

  /**
   * Devuelve el texto para la label de la opción correspondiente a un miembro del equipo.
   *
   * @param persona la persona
   * @returns el nombre y los apellidos si se encuentra la persona o el mensaje de error si no se encuentra la persona en el SGP
   */
  private getOptionLabel(persona: IPersona): string {
    if (persona?.nombre) {
      return `${persona.nombre} ${persona.apellidos ?? ''}`;
    }

    if (persona?.id) {
      return this.translate.instant(SGP_NOT_FOUND, { ids: persona.id, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
    }

    return null;
  }

}
