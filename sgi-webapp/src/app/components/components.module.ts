import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@shared/shared.module';

import { RootComponent } from './root/root.component';

@NgModule({
  declarations: [RootComponent],
  exports: [
    RootComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule
  ]
})
export class ComponentsModule { }
