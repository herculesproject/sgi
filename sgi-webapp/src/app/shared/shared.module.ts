import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SelectorModuloComponent } from '@shared/componentes-layout/selector-modulo/selector-modulo.component';

import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { HeaderComponent } from './componentes-layout/header/header.component';
import { NavbarComponent } from './componentes-layout/navbar/navbar.component';
import { DialogComponent } from './dialog/dialog.component';
import { SnackBarComponent } from './snack-bar/snack-bar.component';
import { FooterCrearComponent } from './footers/footer-crear/footer-crear.component';
import { FooterGuardarComponent } from './footers/footer-guardar/footer-guardar.component';
import { GenericTabLabelComponent } from './formularios-tabs/generic-tab-label/generic-tab-label.component';
import { MAT_DATE_LOCALE, DateAdapter, MAT_DATE_FORMATS } from '@angular/material/core';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { APP_DATE_FORMATS } from '@core/utils/material-design/date-format/format-datepicker';
import { DatepickerFormatDirective } from './directivas/datepicker-format/datepicker-format.directive';

@NgModule({
  declarations: [
    HeaderComponent,
    NavbarComponent,
    DialogComponent,
    SnackBarComponent,
    SelectorModuloComponent,
    BreadcrumbComponent,
    FooterCrearComponent,
    FooterGuardarComponent,
    GenericTabLabelComponent,
    DatepickerFormatDirective,
  ],
  imports: [
    RouterModule,
    FlexLayoutModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
  ],
  exports: [
    MaterialDesignModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    HeaderComponent,
    NavbarComponent,
    BreadcrumbComponent,
    FooterCrearComponent,
    FooterGuardarComponent,
    GenericTabLabelComponent,
  ],
  providers: [
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: { useUtc: true }
    },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: MAT_DATE_FORMATS,
      useValue: APP_DATE_FORMATS
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'es-ES'
    }
  ]
})
export class SharedModule {
}
