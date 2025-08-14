import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ConfiguracionFormularioComponent } from './configuracion-formulario/configuracion-formulario.component';
import { ConfiguracionRoutingModule } from './configuracion-routing.module';

@NgModule({
  declarations: [
    ConfiguracionFormularioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ConfiguracionRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ]
})
export class ConfiguracionModule { }
