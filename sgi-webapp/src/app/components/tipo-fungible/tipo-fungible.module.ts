import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TipoFungibleRoutingModule } from './tipo-fungible-routing.module';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [
    TipoFungibleListadoComponent],
  imports: [
    SharedModule,
    CommonModule,
    TipoFungibleRoutingModule,
    TranslateModule
  ],
  providers: [],
  exports: [
    TranslateModule
  ]
})
export class TipoFungibleModule { }
