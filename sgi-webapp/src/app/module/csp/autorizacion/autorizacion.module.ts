import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { CspSharedModule } from '../shared/csp-shared.module';
import { AutorizacionRoutingModule } from './autorizacion-routing.module';
import { AutorizacionListadoComponent } from './autorizacion-listado/autorizacion-listado.component';
import { AutorizacionCrearComponent } from './autorizacion-crear/autorizacion-crear.component';
import { AutorizacionDatosGeneralesComponent } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.component';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { AutorizacionActionService } from './autorizacion.action.service';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { AutorizacionEditarComponent } from './autorizacion-editar/autorizacion-editar.component';

@NgModule({
  declarations: [
    AutorizacionListadoComponent,
    AutorizacionCrearComponent,
    AutorizacionDatosGeneralesComponent,
    AutorizacionEditarComponent],
  imports: [
    CommonModule,
    SharedModule,
    AutorizacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgempSharedModule,
    SgpSharedModule
  ]
})
export class AutorizacionModule { }
