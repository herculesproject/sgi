import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ProyectoRoutingModule } from './proyecto-routing.module';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';
import { ProyectoCrearComponent } from './proyecto-crear/proyecto-crear.component';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { ProyectoResolver } from './proyecto.resolver';
import { ProyectoHitosComponent } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.component';
import { ProyectoHitosModalComponent } from './modals/proyecto-hitos-modal/proyecto-hitos-modal.component';
import { ProyectoSociosComponent } from './proyecto-formulario/proyecto-socios/proyecto-socios.component';

@NgModule({
  declarations: [
    ProyectoListadoComponent,
    ProyectoCrearComponent,
    ProyectoEditarComponent,
    ProyectoFichaGeneralComponent,
    ProyectoHitosComponent,
    ProyectoHitosModalComponent,
    ProyectoSociosComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [
    ProyectoResolver
  ]
})
export class ProyectoModule { }
