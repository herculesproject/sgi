import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { PersonaService } from '@core/services/sgp/persona.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/internal/operators/catchError';
import { map } from 'rxjs/operators';

const SGP_NOT_FOUND = marker("error.sgp.not-found");
@Component({
  template: `
      <input
        matInput 
        type="text"
        [id]="id"
        [formControl]="formControl"
        [formlyAttributes]="field"
        [placeholder]="to.placeholder"
        [tabIndex]="to.tabindex"
        [required]="to.required"
        [errorStateMatcher]="errorStateMatcher"
      >
 `
})
export class InputPersonaNombreCompletoTypeComponent extends FieldType implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private readonly personaService: PersonaService,
    private readonly translate: TranslateService
  ) {
    super();
  }

  ngOnInit(): void {
    if (!this.value) {
      return;
    }

    this.subscriptions.push(
      this.personaService.findById(this.value).pipe(
        map(persona => `${persona.nombre} ${persona.apellidos}`),
        catchError((err) => {
          this.logger.error(err);
          return of(this.getErrorMsg(this.value));
        })
      ).subscribe(value => this.value = value)
    );

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  private getErrorMsg(id: string): string {
    return this.translate.instant(SGP_NOT_FOUND, { ids: id, ...MSG_PARAMS.CARDINALIRY.SINGULAR })
  }

}
