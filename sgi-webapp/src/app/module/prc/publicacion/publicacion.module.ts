import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PrcReportService } from '@core/services/prc/report/prc-report.service';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { PublicacionEditarComponent } from './publicacion-editar/publicacion-editar.component';
import { PublicacionDatosGeneralesComponent } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.component';
import { PublicacionListadoComponent } from './publicacion-listado/publicacion-listado.component';
import { PublicacionRoutingModule } from './publicacion-routing.module';

@NgModule({
  declarations: [PublicacionListadoComponent, PublicacionDatosGeneralesComponent, PublicacionEditarComponent],
  imports: [
    CommonModule,
    SharedModule,
    PublicacionRoutingModule,
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
    ProduccionCientificaResolver,
    PrcReportService,
    DecimalPipe
  ]
})
export class PublicacionModule { }
