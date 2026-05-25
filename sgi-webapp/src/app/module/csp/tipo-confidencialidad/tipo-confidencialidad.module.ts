import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoConfidencialidadListadoComponent } from './tipo-confidencialidad-listado/tipo-confidencialidad-listado.component';
import { TipoConfidencialidadModalComponent } from './tipo-confidencialidad-modal/tipo-confidencialidad-modal.component';
import { TipoConfidencialidadRoutingModule } from './tipo-confidencialidad-routing.module';

@NgModule({
  declarations: [
    TipoConfidencialidadListadoComponent,
    TipoConfidencialidadModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    TipoConfidencialidadRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class TipoConfidencialidadModule { }
