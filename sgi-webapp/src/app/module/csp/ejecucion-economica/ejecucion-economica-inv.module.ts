import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { EjecucionEconomicaCspService } from './ejecucion-economica-csp.service';
import { EjecucionEconomicaDataResolver } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaListadoInvComponent } from './ejecucion-economica-listado-inv/ejecucion-economica-listado-inv.component';
import { EjecucionEconomicaRoutingInvModule } from './ejecucion-economica-routing-inv.module';
import { ProyectoSgeCspService } from './proyecto-sge-csp.service';

@NgModule({
  declarations: [
    EjecucionEconomicaListadoInvComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    EjecucionEconomicaRoutingInvModule,
    FormsModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    LuxonDatePipe,
    EjecucionEconomicaDataResolver,
    // En modo investigador las peticiones al SGE se enrutan al proxy de CSP, que autoriza
    // por persona. Estos overrides sustituyen, en el injector de este módulo (y por tanto
    // para los componentes/fragments del gestor reutilizados bajo estas rutas), los servicios
    // SGE por sus variantes que apuntan al proxy.
    { provide: EjecucionEconomicaService, useClass: EjecucionEconomicaCspService },
    { provide: ProyectoSgeService, useClass: ProyectoSgeCspService }
  ]
})
export class EjecucionEconomicaInvModule { }
