import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { EntidadFinanciadoraModalComponent } from '../modals/entidad-financiadora-modal/entidad-financiadora-modal.component';
import { MiembroEquipoProyectoModalComponent } from '../modals/miembro-equipo-proyecto-modal/miembro-equipo-proyecto-modal.component';
import { MiembroEquipoSolicitudModalComponent } from './miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { PartidaGastoModalComponent } from './partida-gasto-modal/partida-gasto-modal.component';

@NgModule({
  declarations: [
    EntidadFinanciadoraModalComponent,
    PartidaGastoModalComponent,
    MiembroEquipoProyectoModalComponent,
    MiembroEquipoSolicitudModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    EntidadFinanciadoraModalComponent,
    PartidaGastoModalComponent,
    MiembroEquipoSolicitudModalComponent
  ]
})
export class CspModalsModule { }
