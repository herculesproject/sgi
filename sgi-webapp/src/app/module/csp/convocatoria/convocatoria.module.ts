import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';



@NgModule({
  declarations: [ConvocatoriaListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule
  ]
})
export class ConvocatoriaModule { }
