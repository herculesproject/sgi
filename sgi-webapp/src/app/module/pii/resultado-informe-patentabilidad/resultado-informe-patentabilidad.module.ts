import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ResultadoInformePatentabilidadListadoComponent } from './resultado-informe-patentabilidad-listado/resultado-informe-patentabilidad-listado.component';
import { ResultadoInformePatentabilidadModalComponent } from './resultado-informe-patentabilidad-modal/resultado-informe-patentabilidad-modal.component';
import { ResultadoInformePatentabilidadRoutingModule } from './resultado-informe-patentabilidad-routing.module';

@NgModule({
  declarations: [ResultadoInformePatentabilidadListadoComponent, ResultadoInformePatentabilidadModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    ResultadoInformePatentabilidadRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
})
export class ResultadoInformePatentabilidadModule { }
