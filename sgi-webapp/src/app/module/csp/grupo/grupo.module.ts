import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { GrupoDataResolver } from './grupo-data.resolver';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';
import { GrupoRoutingModule } from './grupo-routing.module';

@NgModule({
  declarations: [
    GrupoListadoComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    GrupoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule
  ],
  providers: [
    GrupoDataResolver
  ]
})
export class GrupoModule { }
