import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoFacturacionListadoComponent } from './tipo-facturacion-listado/tipo-facturacion-listado.component';
import { TipoFacturacionModalComponent } from './tipo-facturacion-modal/tipo-facturacion-modal.component';
import { TipoFacturacionRoutingModule } from './tipo-facturacion-routing.module';


@NgModule({
  declarations: [TipoFacturacionListadoComponent, TipoFacturacionModalComponent],
  imports: [
    CommonModule,
    TipoFacturacionRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoFacturacionModule { }
