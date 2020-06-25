import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TipoReservableRoutingModule } from './tipo-reservable-routing.module';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [
    TipoReservableListadoComponent],
  imports: [
    SharedModule,
    CommonModule,
    TipoReservableRoutingModule,
    TranslateModule
  ],
  providers: [],
  exports: [
    TranslateModule
  ]
})
export class TipoReservableModule { }
