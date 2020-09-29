import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { ConvocatoriaCrearComponent } from './convocatoria-crear/convocatoria-crear.component';
import { ConvocatoriaResolver } from './convocatoria.resolver';
import { ConvocatoriaDatosGeneralesComponent } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';

import { ConvocatoriaPeriodosJustificacionComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodos-justificacion.component';


@NgModule({
  declarations: [
    ConvocatoriaListadoComponent,
    ConvocatoriaCrearComponent,
    ConvocatoriaDatosGeneralesComponent,
    ConvocatoriaPeriodosJustificacionComponent],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule
  ],
  providers: [
    ConvocatoriaResolver
  ]
})
export class ConvocatoriaModule { }
