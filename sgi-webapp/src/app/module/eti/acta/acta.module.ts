import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaRoutingModule } from './acta-routing.module';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { ActaDatosGeneralesComponent } from './acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaAsistentesComponent } from './acta-formulario/acta-asistentes/acta-asistentes.component';
import { ActaMemoriasComponent } from './acta-formulario/acta-memorias/acta-memorias.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    ActaCrearComponent,
    ActaDatosGeneralesComponent,
    ActaMemoriasComponent,
    ActaListadoComponent,
    ActaAsistentesComponent,
    ActaEditarComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ActaRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class ActaModule { }
