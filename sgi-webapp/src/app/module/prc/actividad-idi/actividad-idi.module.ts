import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ActividadIdiRoutingModule } from './actividad-idi-routing.module';
import { ActividadIdiListadoComponent } from './actividad-idi-listado/actividad-idi-listado.component';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';

@NgModule({
  declarations: [ActividadIdiListadoComponent],
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
  ]
})
export class ActividadIdiModule { }
