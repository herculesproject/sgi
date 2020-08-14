import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@shared/shared.module';

import { AgrupacionServicioListadoComponent } from './agrupacion-servicio-listado/agrupacion-servicio-listado.component';
import { AgrupacionServicioRoutingModule } from './agrupacion-servicio-routing.module';

import { TranslateModule } from '@ngx-translate/core';
import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar/agrupacion-servicio-actualizar.component';
import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear/agrupacion-servicio-crear.component';
import { AgrupacionServicioDatosGeneralesComponent } from './agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';
import { AgrupacionServicioGestorComponent } from './agrupacion-servicio-formulario/agrupacion-servicio-gestor/agrupacion-servicio-gestor.component';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    AgrupacionServicioListadoComponent,
    AgrupacionServicioActualizarComponent,
    AgrupacionServicioCrearComponent,
    AgrupacionServicioDatosGeneralesComponent,
    AgrupacionServicioGestorComponent],
  imports: [
    SharedModule,
    CommonModule,
    AgrupacionServicioRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
})
export class AgrupacionServicioModule { }
