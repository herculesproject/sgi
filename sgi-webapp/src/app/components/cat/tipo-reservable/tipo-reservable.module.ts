import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {TipoReservableRoutingModule} from './tipo-reservable-routing.module';
import {TipoReservableListadoComponent} from './tipo-reservable-listado/tipo-reservable-listado.component';
import {SharedModule} from '@shared/shared.module';
import {TranslateModule} from '@ngx-translate/core';
import {MatRadioModule} from '@angular/material/radio';
import {TipoReservableCrearComponent} from './tipo-reservable-crear/tipo-reservable-crear.component';
import {TipoReservableActualizarComponent} from './tipo-reservable-actualizar/tipo-reservable-actualizar.component';


@NgModule({
  declarations: [
    TipoReservableListadoComponent,
    TipoReservableCrearComponent,
    TipoReservableActualizarComponent],
  imports: [
    SharedModule,
    CommonModule,
    TipoReservableRoutingModule,
    TranslateModule,
    MatRadioModule
  ],
  providers: [],
  exports: [
    TranslateModule
  ]
})
export class TipoReservableModule { }
