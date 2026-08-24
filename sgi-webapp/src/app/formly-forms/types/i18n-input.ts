import { Component, OnInit } from '@angular/core';
import { i18nMaxLength } from '@formly-forms/validators/i18n-max-length.validator';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { FieldType } from '@ngx-formly/material/form-field';
import { TranslateService } from '@ngx-translate/core';

@Component({
  template: `
    <sgi-i18n-input
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
    </sgi-i18n-input>
  `
})
export class I18nInputTypeComponent extends FieldType implements OnInit {

  defaultOptions: FormlyFieldConfig = {
    defaultValue: [],
    validators: {
      'i18n-max-length': i18nMaxLength(this.translate)
    }
  };

  constructor(
    private readonly translate: TranslateService
  ) {
    super();
  }

}
