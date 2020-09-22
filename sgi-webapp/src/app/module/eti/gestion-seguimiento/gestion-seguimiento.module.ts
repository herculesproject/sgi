import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SgiAuthModule } from '@sgi/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GestionSeguimientoRoutingModule } from './gestion-seguimiento-routing.module';
import { GestionSeguimientoListadoComponent } from './gestion-seguimiento-listado/gestion-seguimiento-listado.component';/*  */


@NgModule({
  declarations: [GestionSeguimientoListadoComponent],
  imports: [
    CommonModule,
    SgiAuthModule,
    GestionSeguimientoRoutingModule,
    SharedModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class GestionSeguimientoModule { }
