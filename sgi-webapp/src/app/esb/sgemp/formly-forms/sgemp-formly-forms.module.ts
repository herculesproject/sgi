import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgoSharedModule } from '../../sgo/shared/sgo-shared.module';
import { SharedFormlyFormsModule } from '../../shared/formly-forms/shared-formly-forms.module';
import { EmpresaFormlyModalComponent } from './empresa-formly-modal/empresa-formly-modal.component';

@NgModule({
  declarations: [
    EmpresaFormlyModalComponent
  ],
  imports: [
    SharedModule,
    SgoSharedModule,
    FormlyFormsModule,
    SharedFormlyFormsModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule
  ],
  exports: [
    EmpresaFormlyModalComponent
  ]
})
export class SgempFormlyFormsModule { }
