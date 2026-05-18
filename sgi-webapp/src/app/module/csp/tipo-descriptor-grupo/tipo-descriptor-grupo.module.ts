import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoDescriptorGrupoListadoComponent } from './tipo-descriptor-grupo-listado/tipo-descriptor-grupo-listado.component';
import { TipoDescriptorGrupoModalComponent } from './tipo-descriptor-grupo-modal/tipo-descriptor-grupo-modal.component';
import { TipoDescriptorGrupoRoutingModule } from './tipo-descriptor-grupo-routing.module';

@NgModule({
  declarations: [
    TipoDescriptorGrupoListadoComponent,
    TipoDescriptorGrupoModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    TipoDescriptorGrupoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class TipoDescriptorGrupoModule { }
