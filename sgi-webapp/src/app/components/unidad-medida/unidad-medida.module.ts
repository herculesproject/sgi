import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar/unidad-medida-actualizar.component';
import { UnidadMedidaCrearComponent } from './unidad-medida-crear/unidad-medida-crear.component';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { UnidadMedidaRoutingModule } from './unidad-medida-routing.module';

@NgModule({
  declarations: [
    UnidadMedidaListadoComponent,
    UnidadMedidaCrearComponent,
    UnidadMedidaActualizarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    UnidadMedidaRoutingModule,
    TranslateModule,
  ],
  providers: [],
  exports: [TranslateModule],
})
export class UnidadMedidaModule {}
