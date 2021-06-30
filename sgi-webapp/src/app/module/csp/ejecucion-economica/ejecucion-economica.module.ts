import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { EjecucionEconomicaDataResolver } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';
import { EjecucionEconomicaRoutingModule } from './ejecucion-economica-routing.module';

@NgModule({
  declarations: [
    EjecucionEconomicaListadoComponent,
    EjecucionEconomicaEditarComponent,
    ProyectosComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    EjecucionEconomicaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgoSharedModule,
    FormlyModule.forRoot(),
    FormlyMaterialModule
  ],
  providers: [
    EjecucionEconomicaDataResolver
  ]
})
export class EjecucionEconomicaModule { }
