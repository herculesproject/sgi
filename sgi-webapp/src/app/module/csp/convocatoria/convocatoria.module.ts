import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspModalsModule } from '../modals/csp-modals.module';
import { ConfiguracionSolicitudResolver } from './configuracion-solicitud.resolver';
import { ConvocatoriaCrearComponent } from './convocatoria-crear/convocatoria-crear.component';
import { ConvocatoriaEditarComponent } from './convocatoria-editar/convocatoria-editar.component';
import { ConvocatoriaConceptoGastoComponent } from './convocatoria-formulario/convocatoria-concepto-gasto/convocatoria-concepto-gasto.component';
import { ConvocatoriaConfiguracionSolicitudesComponent } from './convocatoria-formulario/convocatoria-configuracion-solicitudes/convocatoria-configuracion-solicitudes.component';
import { ConvocatoriaDatosGeneralesComponent } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { ConvocatoriaDocumentosComponent } from './convocatoria-formulario/convocatoria-documentos/convocatoria-documentos.component';
import { ConvocatoriaEnlaceComponent } from './convocatoria-formulario/convocatoria-enlace/convocatoria-enlace.component';
import { ConvocatoriaEntidadesConvocantesComponent } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.component';
import { ConvocatoriaEntidadesFinanciadorasComponent } from './convocatoria-formulario/convocatoria-entidades-financiadoras/convocatoria-entidades-financiadoras.component';
import { ConvocatoriaHitosComponent } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.component';
import { ConvocatoriaPeriodosJustificacionComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodos-justificacion.component';
import { ConvocatoriaPlazosFasesComponent } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.component';
import { ConvocatoriaRequisitosEquipoComponent } from './convocatoria-formulario/convocatoria-requisitos-equipo/convocatoria-requisitos-equipo.component';
import { ConvocatoriaRequisitosIPComponent } from './convocatoria-formulario/convocatoria-requisitos-ip/convocatoria-requisitos-ip.component';
import { ConvocatoriaSeguimientoCientificoComponent } from './convocatoria-formulario/convocatoria-seguimiento-cientifico/convocatoria-seguimiento-cientifico.component';
import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { ConvocatoriaResolver } from './convocatoria.resolver';
import { ConvocatoriaAreaTematicaModalComponent } from './modals/convocatoria-area-tematica-modal/convocatoria-area-tematica-modal.component';
import { ConvocatoriaConfiguracionSolicitudesModalComponent } from './modals/convocatoria-configuracion-solicitudes-modal/convocatoria-configuracion-solicitudes-modal.component';
import { ConvocatoriaEnlaceModalComponent } from './modals/convocatoria-enlace-modal/convocatoria-enlace-modal.component';
import { ConvocatoriaEntidadConvocanteModalComponent } from './modals/convocatoria-entidad-convocante-modal/convocatoria-entidad-convocante-modal.component';
import { ConvocatoriaHitosModalComponent } from './modals/convocatoria-hitos-modal/convocatoria-hitos-modal.component';
import { ConvocatoriaPeriodosJustificacionModalComponent } from './modals/convocatoria-periodos-justificacion-modal/convocatoria-periodos-justificacion-modal.component';
import { ConvocatoriaPlazosFaseModalComponent } from './modals/convocatoria-plazos-fase-modal/convocatoria-plazos-fase-modal.component';
import { ConvocatoriaSeguimientoCientificoModalComponent } from './modals/convocatoria-seguimiento-cientifico-modal/convocatoria-seguimiento-cientifico-modal.component';
import { ModeloEjecucionIdResolver } from './modelo-ejecucion-id.resolver';

@NgModule({
  declarations: [
    ConvocatoriaListadoComponent,
    ConvocatoriaCrearComponent,
    ConvocatoriaDatosGeneralesComponent,
    ConvocatoriaPeriodosJustificacionComponent,
    ConvocatoriaEditarComponent,
    ConvocatoriaPlazosFasesComponent,
    ConvocatoriaHitosComponent,
    ConvocatoriaEntidadesConvocantesComponent,
    ConvocatoriaSeguimientoCientificoComponent,
    ConvocatoriaEntidadesFinanciadorasComponent,
    ConvocatoriaEnlaceComponent,
    ConvocatoriaHitosModalComponent,
    ConvocatoriaPeriodosJustificacionModalComponent,
    ConvocatoriaEnlaceModalComponent,
    ConvocatoriaEntidadConvocanteModalComponent,
    ConvocatoriaPlazosFaseModalComponent,
    ConvocatoriaSeguimientoCientificoModalComponent,
    ConvocatoriaAreaTematicaModalComponent,
    ConvocatoriaRequisitosIPComponent,
    ConvocatoriaConceptoGastoComponent,
    ConvocatoriaRequisitosEquipoComponent,
    ConvocatoriaDocumentosComponent,
    ConvocatoriaConfiguracionSolicitudesComponent,
    ConvocatoriaConfiguracionSolicitudesModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspModalsModule
  ],
  providers: [
    ConvocatoriaResolver,
    ConfiguracionSolicitudResolver,
    ModeloEjecucionIdResolver
  ]
})
export class ConvocatoriaModule { }
