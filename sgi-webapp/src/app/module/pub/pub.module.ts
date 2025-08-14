import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { PubInicioComponent } from './pub-inicio/pub-inicio.component';
import { PubRootComponent } from './pub-root/pub-root.component';
import { PubRoutingModule } from './pub-routing.module';

@NgModule({
  declarations: [
    PubRootComponent,
    PubInicioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PubRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  providers: []
})
export class PubModule { }
