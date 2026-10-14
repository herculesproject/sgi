import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SharedFormlyFormsModule } from 'src/app/esb/shared/formly-forms/shared-formly-forms.module';
import { MemoriaEditarComponent } from '../memoria/memoria-editar/memoria-editar.component';
import { MemoriaDatosGeneralesComponent } from '../memoria/memoria-formulario/memoria-datos-generales/memoria-datos-generales.component';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { MemoriaCrearComponent } from './memoria-crear/memoria-crear.component';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaEvaluacionesListadoExportService } from './memoria-evaluaciones-listado-export.service';
import { MemoriaDocumentacionComponent } from './memoria-formulario/memoria-documentacion/memoria-documentacion.component';
import { MemoriaEvaluacionesComponent } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.component';
import { MemoriaFormularioComponent } from './memoria-formulario/memoria-formulario/memoria-formulario.component';
import { MemoriaInformesComponent } from './memoria-formulario/memoria-informes/memoria-informes.component';
import { MemoriaRetrospectivaComponent } from './memoria-formulario/memoria-retrospectiva/memoria-retrospectiva.component';
import { MemoriaSeguimientoAnualComponent } from './memoria-formulario/memoria-seguimiento-anual/memoria-seguimiento-anual.component';
import { MemoriaSeguimientoFinalComponent } from './memoria-formulario/memoria-seguimiento-final/memoria-seguimiento-final.component';
import { MemoriaGeneralListadoExportService } from './memoria-general-listado-export.service';
import { MemoriaListadoExportService } from './memoria-listado-export.service';
import { MemoriaListadoInvComponent } from './memoria-listado-inv/memoria-listado-inv.component';
import { MemoriaRoutingInvModule } from './memoria-routing-inv.module';
import { MemoriaUnidadVinculacionListadoExportService } from './memoria-unidad-vinculacion-listado-export.service';
import { MemoriaResolver } from './memoria.resolver';
import { MemoriaDocumentacionMemoriaModalComponent } from './modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';

@NgModule({
  declarations: [
    MemoriaCrearComponent,
    MemoriaDatosGeneralesComponent,
    MemoriaDocumentacionComponent,
    MemoriaDocumentacionMemoriaModalComponent,
    MemoriaEditarComponent,
    MemoriaEvaluacionesComponent,
    MemoriaFormularioComponent,
    MemoriaInformesComponent,
    MemoriaListadoInvComponent,
    MemoriaRetrospectivaComponent,
    MemoriaSeguimientoAnualComponent,
    MemoriaSeguimientoFinalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MemoriaRoutingInvModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    FormlyFormsModule,
    SharedFormlyFormsModule,
    EtiSharedModule
  ],
  providers: [
    MemoriaCrearGuard,
    MemoriaEvaluacionesListadoExportService,
    MemoriaGeneralListadoExportService,
    MemoriaListadoExportService,
    MemoriaResolver,
    MemoriaUnidadVinculacionListadoExportService
  ]
})
export class MemoriaInvModule { }
