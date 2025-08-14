import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import {
  DocumentacionMemoriaListadoMemoriaComponent,
} from './documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';

@NgModule({
  declarations: [
    DocumentacionMemoriaListadoMemoriaComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    SgiAuthModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    DocumentacionMemoriaListadoMemoriaComponent
  ]
})
export class DocumentacionMemoriaModule { }
