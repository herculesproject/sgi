import { NgxMatDatetimePickerModule } from '@angular-material-components/datetime-picker';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DateTimePickerTypeComponent } from '@formly-forms/types/datetimepicker.type';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SelectObjectTypeComponent } from './types/select-object.type';
import { GlobalWrapperComponent } from './wrappers/global/global.wrapper';
import { MatCardGroupWrapperComponent } from './wrappers/mat-card-group/mat-card-group.wrapper';

@NgModule({
  declarations: [
    SelectObjectTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMatDatetimePickerModule,
    FormlyMatDatepickerModule,
    FormlySelectModule,
    FormlyMaterialModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'select-object',
          component: SelectObjectTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'dateTimePicker',
          component: DateTimePickerTypeComponent,
          wrappers: ['form-field'],
        }
      ],
      wrappers: [
        {
          name: 'mat-card-group',
          component: MatCardGroupWrapperComponent
        },
        {
          name: 'global',
          component: GlobalWrapperComponent
        }
      ],
      validationMessages: [
        { name: 'required', message: 'El campo es obligatorio' },
      ],
    }),
  ],
  exports: [
    SelectObjectTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ]
})
export class SharedFormlyFormsModule { }
