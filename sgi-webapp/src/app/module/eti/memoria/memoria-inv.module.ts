import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@sgi/framework/auth';
import { MemoriaDatosGeneralesComponent } from '../memoria/memoria-formulario/memoria-datos-generales/memoria-datos-generales.component';
import { MemoriaEditarComponent } from '../memoria/memoria-editar/memoria-editar.component';
import { MemoriaResolver } from './memoria.resolver';
import { MemoriaRoutingInvModule } from './memoria-routing-inv.module';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaDocumentacionComponent } from './memoria-formulario/memoria-documentacion/memoria-documentacion.component';
import { MemoriaDocumentacionMemoriaModalComponent } from './modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';
import { MemoriaDocumentacionSeguimientosModalComponent } from './modals/memoria-documentacion-seguimientos-modal/memoria-documentacion-seguimientos-modal.component';
import { MemoriaListadoGesComponent } from './memoria-listado-ges/memoria-listado-ges.component';
import { MemoriaCrearComponent } from './memoria-crear/memoria-crear.component';
import { MemoriaEvaluacionesComponent } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.component';
import { MemoriaListadoInvComponent } from './memoria-listado-inv/memoria-listado-inv.component';


@NgModule({
  declarations: [
    MemoriaListadoInvComponent,
    MemoriaCrearComponent,
    MemoriaDatosGeneralesComponent,
    MemoriaEditarComponent,
    MemoriaDocumentacionComponent,
    MemoriaDocumentacionMemoriaModalComponent,
    MemoriaDocumentacionSeguimientosModalComponent,
    MemoriaEvaluacionesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MemoriaRoutingInvModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ],
  providers: [
    MemoriaResolver,
    MemoriaCrearGuard
  ]
})
export class MemoriaInvModule { }