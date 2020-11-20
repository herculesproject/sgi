import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { ConvocatoriaResolver } from './convocatoria.resolver';
import { ConvocatoriaListadoInvComponent } from './convocatoria-listado-inv/convocatoria-listado-inv.component';
import { ConvocatoriaRoutingInvModule } from './convocatoria-routing-inv.module';

@NgModule({
  declarations: [
    ConvocatoriaListadoInvComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule
  ],
  providers: [
    ConvocatoriaResolver
  ]
})
export class ConvocatoriaInvModule { }
