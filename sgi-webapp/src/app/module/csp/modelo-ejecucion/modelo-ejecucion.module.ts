import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ModeloEjecucionTipoDocumentoModalComponent } from './modals/modelo-ejecucion-tipo-documento-modal/modelo-ejecucion-tipo-documento-modal.component';
import { ModeloEjecucionTipoEnlaceModalComponent } from './modals/modelo-ejecucion-tipo-enlace-modal/modelo-ejecucion-tipo-enlace-modal.component';
import { ModeloEjecucionTipoFaseModalComponent } from './modals/modelo-ejecucion-tipo-fase-modal/modelo-ejecucion-tipo-fase-modal.component';
import { ModeloEjecucionTipoFinalidadModalComponent } from './modals/modelo-ejecucion-tipo-finalidad-modal/modelo-ejecucion-tipo-finalidad-modal.component';
import { ModeloEjecucionTipoHitoModalComponent } from './modals/modelo-ejecucion-tipo-hito-modal/modelo-ejecucion-tipo-hito-modal.component';
import { ModeloEjecucionTipoUnidadGestionModalComponent } from './modals/modelo-ejecucion-tipo-unidad-gestion-modal/modelo-ejecucion-tipo-unidad-gestion-modal.component';
import { ModeloEjecucionCrearComponent } from './modelo-ejecucion-crear/modelo-ejecucion-crear.component';
import { ModeloEjecucionEditarComponent } from './modelo-ejecucion-editar/modelo-ejecucion-editar.component';
import { ModeloEjecucionDatosGeneralesComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.component';
import { ModeloEjecucionTipoDocumentoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-documento/modelo-ejecucion-tipo-documento.component';
import { ModeloEjecucionTipoEnlaceComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-enlace/modelo-ejecucion-tipo-enlace.component';
import { ModeloEjecucionTipoFaseComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-fase/modelo-ejecucion-tipo-fase.component';
import { ModeloEjecucionTipoFinalidadComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-finalidad/modelo-ejecucion-tipo-finalidad.component';
import { ModeloEjecucionTipoHitoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-hito/modelo-ejecucion-tipo-hito.component';
import { ModeloEjecucionTipoUnidadGestionComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-unidad-gestion/modelo-ejecucion-tipo-unidad-gestion.component';
import { ModeloEjecucionListadoComponent } from './modelo-ejecucion-listado/modelo-ejecucion-listado.component';
import { ModeloEjecuccionRoutingModule } from './modelo-ejecucion-routing.module';
import { ModeloEjecucionResolver } from './modelo-ejecucion.resolver';

@NgModule({
  declarations: [
    ModeloEjecucionListadoComponent,
    ModeloEjecucionCrearComponent,
    ModeloEjecucionDatosGeneralesComponent,
    ModeloEjecucionEditarComponent,
    ModeloEjecucionTipoEnlaceComponent,
    ModeloEjecucionTipoEnlaceModalComponent,
    ModeloEjecucionTipoFinalidadComponent,
    ModeloEjecucionTipoFinalidadModalComponent,
    ModeloEjecucionTipoFaseModalComponent,
    ModeloEjecucionTipoFaseComponent,
    ModeloEjecucionTipoDocumentoComponent,
    ModeloEjecucionTipoDocumentoModalComponent,
    ModeloEjecucionTipoHitoComponent,
    ModeloEjecucionTipoHitoModalComponent,
    ModeloEjecucionTipoUnidadGestionComponent,
    ModeloEjecucionTipoUnidadGestionModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ModeloEjecuccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    ModeloEjecucionResolver
  ]
})
export class ModeloEjecucionModule { }
