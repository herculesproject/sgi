import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, toArray } from 'rxjs/operators';

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
        <mat-option [value]="item.value">
          {{ item.label }}
        </mat-option>
      </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectMiembrosEquipoTypeComponent extends FieldType implements OnInit {

  constructor(
    private peticionEvaluacionService: PeticionEvaluacionService,
    private personaService: PersonaService,
    private vinculacionService: VinculacionService,
    private datosAcademicosService: DatosAcademicosService,
  ) {
    super();
  }

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith: (o1: any, o2: any) => o1 === o2,
    },
  };

  ngOnInit(): void {
    const idPeticionEvaluacion = this.formState?.memoria?.peticionEvaluacion?.id;

    if (!idPeticionEvaluacion) {
      this.setOptions([]);
      return;
    }

    this.loadMiembrosEquipoInvestigador(idPeticionEvaluacion)
      .pipe(
        map(miembros =>
          miembros.map(eq => ({
            label: `${eq.persona?.nombre ?? ''} ${eq.persona?.apellidos ?? ''}`,
            value: eq.persona
          }))
        )
      )
      .subscribe(options => this.setOptions(options));
  }

  get selectOptions(): any[] {
    return this.field?.templateOptions?.options as any[];
  }

  private setOptions(options: any[]) {
    this.field.templateOptions = {
      ...this.field.templateOptions,
      options
    };
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

  private loadMiembrosEquipoInvestigador(idPeticionEvaluacion: number): Observable<any[]> {

    return this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
      map(res => res.items),
      mergeMap(miembros => {
        if (!miembros?.length) return of([]);

        return from(miembros).pipe(
          mergeMap(element =>
            this.personaService.findById(element.persona.id).pipe(
              map(persona => {
                element.persona = persona;
                return element;
              }),
              catchError(() => of(element)),
              switchMap(() => {
                if (!element.persona?.nombre) return of(element);

                return this.vinculacionService.findByPersonaId(element.persona.id).pipe(
                  map(v => {
                    element.persona.vinculacion = v;
                    return element;
                  }),
                  catchError(() => of(element))
                );
              }),
              switchMap(() => {
                if (!element.persona?.nombre) return of(element);

                return this.datosAcademicosService.findByPersonaId(element.persona.id).pipe(
                  map(d => {
                    element.persona.datosAcademicos = d;
                    return element;
                  }),
                  catchError(() => of(element))
                );
              })
            )
          ),
          toArray(),
          map(() => miembros)
        );
      })
    );
  }
}