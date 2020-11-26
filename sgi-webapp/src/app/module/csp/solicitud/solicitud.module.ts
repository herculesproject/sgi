import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SolicitudCrearComponent } from './solicitud-crear/solicitud-crear.component';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudListadoComponent } from './solicitud-listado/solicitud-listado.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SharedModule } from '@shared/shared.module';
import { SolicitudRoutingModule } from './solicitud-routing.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { SolicitudResolver } from './solicitud.resolver';
import { SolicitudModalidadEntidadConvocanteModalComponent } from './modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';



@NgModule({
  declarations: [
    SolicitudCrearComponent,
    SolicitudEditarComponent,
    SolicitudListadoComponent,
    SolicitudDatosGeneralesComponent,
    SolicitudModalidadEntidadConvocanteModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SolicitudRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule
  ],
  providers: [
    SolicitudResolver
  ]
})
export class SolicitudModule { }
