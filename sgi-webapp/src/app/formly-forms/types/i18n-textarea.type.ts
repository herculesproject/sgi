import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { I18nTextareaComponent } from '@components/i18n/i18n-textarea/i18n-textarea.component';
import { LanguageService } from '@core/services/language.service';
import { i18nMaxLength } from '@formly-forms/validators/i18n-max-length.validator';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { FieldType } from '@ngx-formly/material/form-field';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  template: `
    <sgi-i18n-textarea
      [id]="id"
      [required]="to.required"
      [formControl]="formControl"
      [errorStateMatcher]="errorStateMatcher"
      [plainValue]="true"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabindex]="to.tabindex"
      [matTooltip]="to.tooltip"
      >
    </sgi-i18n-textarea>
  `
})
export class I18nTextareaTypeComponent extends FieldType implements OnInit, OnDestroy {

  @ViewChild(I18nTextareaComponent, { static: true })
  private readonly textarea: I18nTextareaComponent;

  private readonly destroy$ = new Subject<void>();

  defaultOptions: FormlyFieldConfig = {
    defaultValue: [],
    validators: {
      'i18n-max-length': i18nMaxLength(this.translate)
    },
    hooks: {
      onInit: (field) => this.updateDescription(field, this.languageService.getFieldValue(field.formControl.value))
    }
  };

  constructor(
    private readonly translate: TranslateService,
    private readonly languageService: LanguageService
  ) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.textarea.editValueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(
      (value) => this.updateDescription(this.field, value)
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    super.ngOnDestroy();
  }

  /**
   * Actualiza la descripción del campo con el contador de caracteres del idioma editado.
   * El wrapper `form-field` muestra esta descripción como `mat-hint`.
   *
   * Solo se aplica a los campos que declaran `maxLength`.
   */
  private updateDescription(field: FormlyFieldConfig, value: string): void {
    if (!field.templateOptions.maxLength) {
      return;
    }

    field.templateOptions.description = `${value?.length ?? 0} / ${field.templateOptions.maxLength}`;
  }

}
