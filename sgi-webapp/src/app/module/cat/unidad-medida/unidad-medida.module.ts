import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar/unidad-medida-actualizar.component';
import { UnidadMedidaCrearComponent } from './unidad-medida-crear/unidad-medida-crear.component';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { UnidadMedidaRoutingModule } from './unidad-medida-routing.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

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
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: []
})
export class UnidadMedidaModule { }
