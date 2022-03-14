import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CongresoRoutingModule } from './congreso-routing.module';
import { CongresoListadoComponent } from './congreso-listado/congreso-listado.component';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';

@NgModule({
  declarations: [CongresoListadoComponent],
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
  ]
})
export class CongresoModule { }
