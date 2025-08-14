import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { EtiInicioComponent } from './eti-inicio/eti-inicio.component';
import { EtiRootComponent } from './eti-root/eti-root.component';
import { EtiRoutingModule } from './eti-routing.module';

@NgModule({
  declarations: [
    EtiRootComponent,
    EtiInicioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    EtiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ]
})
export class EtiModule { }
