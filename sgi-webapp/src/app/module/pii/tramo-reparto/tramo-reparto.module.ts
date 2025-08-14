import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TramoRepartoTramoPipe } from './pipes/tramo-reparto-tramo.pipe';
import { TramoRepartoListadoComponent } from './tramo-reparto-listado/tramo-reparto-listado.component';
import { TramoRepartoModalComponent } from './tramo-reparto-modal/tramo-reparto-modal.component';
import { TramoRepartoRoutingModule } from './tramo-reparto-routing.module';



@NgModule({
  declarations: [TramoRepartoListadoComponent, TramoRepartoModalComponent, TramoRepartoTramoPipe],
  imports: [
    CommonModule,
    TramoRepartoRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
  ],
  exports: [
    TramoRepartoTramoPipe
  ],
  providers: [DecimalPipe]
})
export class TramoRepartoModule { }
