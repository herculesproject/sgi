import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SeguimientoJustificacionRequerimientoCrearComponent } from './seguimiento-justificacion-requerimiento-crear/seguimiento-justificacion-requerimiento-crear.component';
import { SeguimientoJustificacionRequerimientoDataResolver } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoEditarComponent } from './seguimiento-justificacion-requerimiento-editar/seguimiento-justificacion-requerimiento-editar.component';
import { SeguimientoJustificacionRequerimientoRouting } from './seguimiento-justificacion-requerimiento-routing.module';
import { SeguimientoJustificacionRequerimientoDatosGeneralesComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.component';
import { CspSharedModule } from '../shared/csp-shared.module';
import { IncidenciaDocumentoRequerimientoModalComponent } from './modals/incidencia-documento-requerimiento-modal/incidencia-documento-requerimiento-modal.component';
import { SeguimientoJustificacionRequerimientoGastosComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.component';

@NgModule({
  declarations: [
    SeguimientoJustificacionRequerimientoEditarComponent,
    SeguimientoJustificacionRequerimientoCrearComponent,
    SeguimientoJustificacionRequerimientoDatosGeneralesComponent,
    IncidenciaDocumentoRequerimientoModalComponent,
    SeguimientoJustificacionRequerimientoGastosComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    SeguimientoJustificacionRequerimientoRouting,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    SeguimientoJustificacionRequerimientoDataResolver,
  ]
})
export class SeguimientoJustificacionRequerimientoModule { }
