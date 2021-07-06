import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { InvencionCrearComponent } from './invencion-crear/invencion-crear.component';
import { InvencionDatosGeneralesComponent } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.component';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';
import { InvencionRoutingModule } from './invencion-routing.module';
import { InvencionEditarComponent } from './invencion-editar/invencion-editar.component';
import { InvencionResolver } from './invencion.resolver';

@NgModule({
  declarations: [InvencionListadoComponent, InvencionCrearComponent, InvencionDatosGeneralesComponent, InvencionEditarComponent],
  imports: [
    CommonModule,
    SharedModule,
    InvencionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule
  ],
  providers: [
    InvencionResolver
  ]
})
export class InvencionModule { }
