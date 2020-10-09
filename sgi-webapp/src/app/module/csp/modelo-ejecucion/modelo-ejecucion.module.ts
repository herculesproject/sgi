import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModeloEjecucionListadoComponent } from './modelo-ejecucion-listado/modelo-ejecucion-listado.component';
import { ModeloEjecuccionRoutingModule } from './modelo-ejecucion-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

@NgModule({
  declarations: [
    ModeloEjecucionListadoComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ModeloEjecuccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule
  ]
})
export class ModeloEjecucionModule { }
