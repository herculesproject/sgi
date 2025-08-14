import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoSocioPeriodoJustificacionCrearComponent } from './proyecto-socio-periodo-justificacion-crear/proyecto-socio-periodo-justificacion-crear.component';
import { ProyectoSocioPeriodoJustificacionDataResolver } from './proyecto-socio-periodo-justificacion-data.resolver';
import { ProyectoSocioPeriodoJustificacionEditarComponent } from './proyecto-socio-periodo-justificacion-editar/proyecto-socio-periodo-justificacion-editar.component';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-datos-generales/proyecto-socio-periodo-justificacion-datos-generales.component';
import { ProyectoSocioPeriodoJustificacionDocumentosComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-documentos/proyecto-socio-periodo-justificacion-documentos.component';
import { ProyectoSocioPeriodoJustificacionRouting } from './proyecto-socio-periodo-justificacion-routing.module';

@NgModule({
  declarations: [
    ProyectoSocioPeriodoJustificacionCrearComponent,
    ProyectoSocioPeriodoJustificacionEditarComponent,
    ProyectoSocioPeriodoJustificacionDatosGeneralesComponent,
    ProyectoSocioPeriodoJustificacionDocumentosComponent],
  imports: [
    CspSharedModule,
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoSocioPeriodoJustificacionRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ProyectoSocioPeriodoJustificacionDataResolver
  ]
})
export class ProyectoSocioPeriodoJustificacionModule { }
