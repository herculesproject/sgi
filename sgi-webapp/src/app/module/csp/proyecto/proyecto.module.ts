import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ProyectoRoutingModule } from './proyecto-routing.module';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';

@NgModule({
  declarations: [
  ProyectoListadoComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class ProyectoModule { }
