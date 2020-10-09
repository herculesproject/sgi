import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@sgi/framework/auth';
import { MemoriaResolver } from './memoria.resolver';
import { MemoriaRoutingGesModule } from './memoria-routing-ges.module';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaListadoGesComponent } from './memoria-listado-ges/memoria-listado-ges.component';
import { MemoriaEditarComponent } from './memoria-editar/memoria-editar.component';
import { MemoriaDatosGeneralesComponent } from './memoria-formulario/memoria-datos-generales/memoria-datos-generales.component';
import { MemoriaDocumentacionComponent } from './memoria-formulario/memoria-documentacion/memoria-documentacion.component';
import { MemoriaDocumentacionMemoriaModalComponent } from './modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';
import { MemoriaCrearComponent } from './memoria-crear/memoria-crear.component';
import { MemoriaDocumentacionSeguimientosModalComponent } from './modals/memoria-documentacion-seguimientos-modal/memoria-documentacion-seguimientos-modal.component';


@NgModule({
  declarations: [
    MemoriaListadoGesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MemoriaRoutingGesModule,
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
export class MemoriaGesModule { }