import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnidadMedidaRoutingModule } from './unidad-medida-routing.module';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { SharedModule } from '@shared/shared.module';
import { UnidadMedidaAgregarActualizarComponent } from './unidad-medida-agregar-actualizar/unidad-medida-agregar-actualizar.component';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [
    UnidadMedidaListadoComponent,
    UnidadMedidaAgregarActualizarComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    UnidadMedidaRoutingModule,
    TranslateModule
  ],
  providers: [],
  exports: [
    TranslateModule
  ]
})
export class UnidadMedidaModule { }
