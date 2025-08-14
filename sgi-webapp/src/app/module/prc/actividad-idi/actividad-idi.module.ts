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
import { ActividadIdiEditarComponent } from './actividad-idi-editar/actividad-idi-editar.component';
import { ActividadIdiDatosGeneralesComponent } from './actividad-idi-formulario/actividad-idi-datos-generales/actividad-idi-datos-generales.component';
import { ActividadIdiListadoComponent } from './actividad-idi-listado/actividad-idi-listado.component';
import { ActividadIdiRoutingModule } from './actividad-idi-routing.module';

@NgModule({
  declarations: [ActividadIdiListadoComponent, ActividadIdiEditarComponent, ActividadIdiDatosGeneralesComponent],
  imports: [
    CommonModule,
    SharedModule,
    ActividadIdiRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    CspSharedModule,
    PrcSharedModule,
  ],
  providers: [
    ProduccionCientificaResolver
  ]
})
export class ActividadIdiModule { }
