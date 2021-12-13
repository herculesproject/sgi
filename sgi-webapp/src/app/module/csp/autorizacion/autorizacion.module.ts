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

@NgModule({
  declarations: [AutorizacionListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    AutorizacionRoutingModule
  ]
})
export class AutorizacionModule { }
