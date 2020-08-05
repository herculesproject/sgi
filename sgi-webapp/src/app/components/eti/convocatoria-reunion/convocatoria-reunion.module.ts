import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { SharedModule } from '@shared/shared.module';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { ComponentsModule } from '../../components.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { ConvocatoriaReunionRoutingModule } from './convocatoria-reunion-routing.module';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { SgiAuthModule } from '@sgi/framework/auth';

@NgModule({
  declarations: [
    ConvocatoriaReunionCrearComponent,
    ConvocatoriaReunionListadoComponent,
    ConvocatoriaReunionDatosGeneralesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ConvocatoriaReunionRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
    PerfectScrollbarModule,
    SgiAuthModule
  ]
})
export class ConvocatoriaReunionModule { }
