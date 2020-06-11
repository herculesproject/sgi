import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnidadMedidaRoutingModule } from './unidad-medida-routing.module';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [UnidadMedidaListadoComponent],
  imports: [
    SharedModule,
    CommonModule,
    UnidadMedidaRoutingModule,
    TranslateModule
  ],
  providers: [],
  exports: []
})
export class UnidadMedidaModule { }
