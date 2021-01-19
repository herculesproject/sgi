import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SolicitudProyectoSocioCrearComponent } from './solicitud-proyecto-socio-crear/solicitud-proyecto-socio-crear.component';
import { SolicitudProyectoSocioEditarComponent } from './solicitud-proyecto-socio-editar/solicitud-proyecto-socio-editar.component';
import { SolicitudProyectoSocioRouting } from './solicitud-proyecto-socio-routing.module';
import { SolicitudProyectoSocioDatosGeneralesComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.component';
import { SolicitudProyectoSocioGuard } from './solicitud-proyecto-socio.guard';

@NgModule({
  declarations: [
    SolicitudProyectoSocioCrearComponent,
    SolicitudProyectoSocioEditarComponent,
    SolicitudProyectoSocioDatosGeneralesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SolicitudProyectoSocioRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [
    SolicitudProyectoSocioGuard
  ]
})
export class SolicitudProyectoSocioModule { }
