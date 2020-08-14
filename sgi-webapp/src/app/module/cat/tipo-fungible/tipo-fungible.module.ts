import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { TipoFungibleActualizarComponent } from './tipo-fungible-actualizar/tipo-fungible-actualizar.component';
import { TipoFungibleCrearComponent } from './tipo-fungible-crear/tipo-fungible-crear.component';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { TipoFungibleRoutingModule } from './tipo-fungible-routing.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    TipoFungibleListadoComponent,
    TipoFungibleCrearComponent,
    TipoFungibleActualizarComponent],
  imports: [
    SharedModule,
    CommonModule,
    TipoFungibleRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
})
export class TipoFungibleModule { }
