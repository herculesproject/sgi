import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { ConvocatoriaReunionRoutingModule } from './convocatoria-reunion-routing.module';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ConvocatoriaReunionListadoMemoriasComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-listado-memorias/convocatoria-reunion-listado-memorias.component';
import { ConvocatoriaReunionAsignacionMemoriasComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias.component';


@NgModule({
  declarations: [
    ConvocatoriaReunionCrearComponent,
    ConvocatoriaReunionListadoComponent,
    ConvocatoriaReunionDatosGeneralesComponent,
    ConvocatoriaReunionListadoMemoriasComponent,
    ConvocatoriaReunionAsignacionMemoriasComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ConvocatoriaReunionRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class ConvocatoriaReunionModule { }
