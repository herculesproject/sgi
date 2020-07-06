import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { PiiRootComponent } from './pii-root/pii-root.component';
import { PiiRoutingModule } from './pii-routing.module';

@NgModule({
  declarations: [PiiRootComponent],
  imports: [
    SharedModule,
    CommonModule,
    PiiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
  ],
  providers: [],
  exports: [TranslateModule],
})
export class PiiModule {}
