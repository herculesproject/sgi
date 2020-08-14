import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SolicitudRoutingModule } from './solicitud-routing.module';
import { SolicitudActualizarComponent } from './solicitud-actualizar/solicitud-actualizar.component';
import { SolicitudListadoComponent } from './solicitud-listado/solicitud-listado.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    SolicitudActualizarComponent,
    SolicitudListadoComponent],
  imports: [
    SharedModule,
    CommonModule,
    SolicitudRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: []
})
export class SolicitudModule { }
