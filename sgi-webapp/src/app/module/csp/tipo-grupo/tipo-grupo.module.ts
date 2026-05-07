import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoGrupoListadoComponent } from './tipo-grupo-listado/tipo-grupo-listado.component';
import { TipoGrupoModalComponent } from './tipo-grupo-modal/tipo-grupo-modal.component';
import { TipoGrupoRoutingModule } from './tipo-grupo-routing.module';

@NgModule({
  declarations: [
    TipoGrupoListadoComponent,
    TipoGrupoModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    TipoGrupoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class TipoGrupoModule { }
