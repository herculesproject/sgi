import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { I18nTextareaComponent } from './i18n-textarea/i18n-textarea.component';
import { TranslateModule } from '@ngx-translate/core';
import { I18nCkeditorComponent } from './i18n-ckeditor/i18n-ckeditor.component';
import { I18nInputComponent } from './i18n-input/i18n-input.component';
import { I18nFieldValuePipe } from './i18n-field-value.pipe';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';

@NgModule({
  declarations: [
    I18nCkeditorComponent,
    I18nFieldValuePipe,
    I18nInputComponent,
    I18nTextareaComponent
  ],
  imports: [
    CommonModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    CKEditorModule
  ],
  exports: [
    I18nCkeditorComponent,
    I18nFieldValuePipe,
    I18nInputComponent,
    I18nTextareaComponent
  ]
})
export class I18nComponentsModule { }