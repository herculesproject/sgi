import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { PeticionEvaluacionListadoExportModalComponent } from './modals/peticion-evaluacion-listado-export-modal/peticion-evaluacion-listado-export-modal.component';
import { PeticionEvaluacionUnidadVinculacionModalComponent } from './modals/peticion-evaluacion-unidad-vinculacion-modal/peticion-evaluacion-unidad-vinculacion-modal.component';
import { PeticionEvaluacionAsignacionTareasListadoExportService } from './peticion-evaluacion-asignacion-tareas-listado-export.service';
import { PeticionEvaluacionEquipoInvestigadorListadoExportService } from './peticion-evaluacion-equipo-investigador-listado-export.service';
import { PeticionEvaluacionUnidadesVinculacionComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-unidades-vinculacion/peticion-evaluacion-unidades-vinculacion.component';
import { PeticionEvaluacionGeneralListadoExportService } from './peticion-evaluacion-general-listado-export.service';
import { PeticionEvaluacionGesRoutingModule } from './peticion-evaluacion-ges-routing.module';
import { PeticionEvaluacionListadoExportService } from './peticion-evaluacion-listado-export.service';
import { PeticionEvaluacionListadoGesComponent } from './peticion-evaluacion-listado-ges/peticion-evaluacion-listado-ges.component';
import { PeticionEvaluacionMemoriasListadoExportService } from './peticion-evaluacion-memorias-listado-export.service';
import { PeticionEvaluacionUnidadVinculacionListadoExportService } from './peticion-evaluacion-unidad-vinculacion-listado-export.service';
import { PeticionEvaluacionUnidadesVinculacionEnabledResolver } from './peticion-evaluacion-unidades-vinculacion-enabled.resolver';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';

@NgModule({
  declarations: [
    PeticionEvaluacionListadoExportModalComponent,
    PeticionEvaluacionListadoGesComponent,
    PeticionEvaluacionUnidadesVinculacionComponent,
    PeticionEvaluacionUnidadVinculacionModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionGesRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    EtiSharedModule,
    SgoSharedModule
  ],
  providers: [
    PeticionEvaluacionAsignacionTareasListadoExportService,
    PeticionEvaluacionEquipoInvestigadorListadoExportService,
    PeticionEvaluacionGeneralListadoExportService,
    PeticionEvaluacionListadoExportService,
    PeticionEvaluacionMemoriasListadoExportService,
    PeticionEvaluacionResolver,
    PeticionEvaluacionUnidadesVinculacionEnabledResolver,
    PeticionEvaluacionUnidadVinculacionListadoExportService
  ]
})
export class PeticionEvaluacionGesModule { }
