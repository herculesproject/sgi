import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoProteccionListadoComponent } from './tipo-proteccion-listado/tipo-proteccion-listado.component';
import { TipoProteccionRoutingModule } from './tipo-proteccion-routing.module';
import { TipoProteccionResolver } from './tipo-proteccion.resolver';
import { TipoProteccionCrearComponent } from './tipo-proteccion-crear/tipo-proteccion-crear.component';
import { TipoProteccionEditarComponent } from './tipo-proteccion-editar/tipo-proteccion-editar.component';
import { TipoProteccionDatosGeneralesComponent } from './tipo-proteccion-formulario/tipo-proteccion-datos-generales/tipo-proteccion-datos-generales.component';

@NgModule({
  declarations: [
    TipoProteccionListadoComponent,
    TipoProteccionCrearComponent,
    TipoProteccionEditarComponent,
    TipoProteccionDatosGeneralesComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoProteccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
    TipoProteccionResolver
  ]
})
export class TipoProteccionModule { }
