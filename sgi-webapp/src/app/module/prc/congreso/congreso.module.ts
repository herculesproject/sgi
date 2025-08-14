import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { CongresoEditarComponent } from './congreso-editar/congreso-editar.component';
import { CongresoDatosGeneralesComponent } from './congreso-formulario/congreso-datos-generales/congreso-datos-generales.component';
import { CongresoListadoComponent } from './congreso-listado/congreso-listado.component';
import { CongresoRoutingModule } from './congreso-routing.module';

@NgModule({
  declarations: [CongresoListadoComponent, CongresoEditarComponent, CongresoDatosGeneralesComponent],
  imports: [
    CommonModule,
    SharedModule,
    CongresoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    PrcSharedModule,
    CspSharedModule
  ],
  providers: [
    ProduccionCientificaResolver
  ]
})
export class CongresoModule { }
