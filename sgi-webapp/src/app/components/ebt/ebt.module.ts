import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { ComponentsModule } from '../components.module';
import { EbtRootComponent } from './ebt-root/ebt-root.component';
import { EbtRoutingModule } from './ebt-routing.module';

@NgModule({
  declarations: [EbtRootComponent],
  imports: [
    SharedModule,
    CommonModule,
    EbtRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
  ],
  providers: [],
  exports: [TranslateModule],
})
export class EbtModule { }
