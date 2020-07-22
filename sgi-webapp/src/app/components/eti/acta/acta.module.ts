import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';

import { ComponentsModule } from '../../components.module';
import { ActaCrearAsistentesComponent } from './acta-crear/acta-crear-asistentes/acta-crear-asistentes.component';
import {
  ActaCrearDatosGeneralesComponent,
} from './acta-crear/acta-crear-datos-generales/acta-crear-datos-generales.component';
import { ActaCrearMemoriasComponent } from './acta-crear/acta-crear-memorias/acta-crear-memorias.component';
import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaRoutingModule } from './acta-routing.module';

@NgModule({
  declarations: [
    ActaCrearComponent,
    ActaCrearDatosGeneralesComponent,
    ActaCrearMemoriasComponent,
    ActaCrearAsistentesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ActaRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
    PerfectScrollbarModule,
  ]
})
export class ActaModule { }
