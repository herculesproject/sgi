import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoEnlaceListadoComponent } from './tipo-enlace-listado/tipo-enlace-listado.component';
import { TipoEnlaceModalComponent } from './tipo-enlace-modal/tipo-enlace-modal.component';
import { TipoEnlaceRoutingModule } from './tipo-enlace-routing.module';

@NgModule({
  declarations: [
    TipoEnlaceListadoComponent,
    TipoEnlaceModalComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    TipoEnlaceRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class TipoEnlaceModule { }
