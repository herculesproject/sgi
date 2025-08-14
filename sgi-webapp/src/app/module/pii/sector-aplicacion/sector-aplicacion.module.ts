import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SectorAplicacionListadoComponent } from './sector-aplicacion-listado/sector-aplicacion-listado.component';
import { SectorAplicacionModalComponent } from './sector-aplicacion-modal/sector-aplicacion-modal.component';
import { SectorAplicacionRoutingModule } from './sector-aplicacion-routing.module';

@NgModule({
  declarations: [SectorAplicacionListadoComponent, SectorAplicacionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    SectorAplicacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class SectorAplicacionModule { }
