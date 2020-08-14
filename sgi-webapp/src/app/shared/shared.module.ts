import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';

import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { FooterCrearComponent } from './footers/footer-crear/footer-crear.component';
import { FooterGuardarComponent } from './footers/footer-guardar/footer-guardar.component';
import { GenericTabLabelComponent } from './generic-tab-label/generic-tab-label.component';
import { RootComponent } from './root/root.component';
import { CommonModule } from '@angular/common';
import { DatepickerFormatDirective } from './directivas/datepicker-format/datepicker-format.directive';

@NgModule({
  declarations: [
    BreadcrumbComponent,
    FooterCrearComponent,
    FooterGuardarComponent,
    GenericTabLabelComponent,
    DatepickerFormatDirective,
    RootComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialDesignModule,
    TranslateModule,
  ],
  exports: [
    BreadcrumbComponent,
    FooterCrearComponent,
    FooterGuardarComponent,
    GenericTabLabelComponent,
    RootComponent
  ]
})
export class SharedModule {
}
