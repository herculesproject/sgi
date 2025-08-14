import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ViaProteccionListadoComponent } from './via-proteccion-listado/via-proteccion-listado.component';
import { ViaProteccionModalComponent } from './via-proteccion-modal/via-proteccion-modal.component';
import { ViaProteccionRoutingModule } from './via-proteccion-routing.module';

@NgModule({
  declarations: [ViaProteccionListadoComponent, ViaProteccionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    ViaProteccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    SgiAuthModule
  ]
})
export class ViaProteccionModule { }
