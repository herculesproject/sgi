import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { ConvocatoriaPublicListadoComponent } from './convocatoria-public-listado/convocatoria-public-listado.component';
import { ConvocatoriaRoutingModule } from './convocatoria-public-routing.module';

@NgModule({
  declarations: [
    ConvocatoriaPublicListadoComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgempSharedModule
  ],
  providers: [
  ]
})
export class ConvocatoriaPublicModule { }
