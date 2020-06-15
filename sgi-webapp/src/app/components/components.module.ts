import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RootComponent } from './root/root.component';
import { SharedModule } from '@shared/shared.module';



@NgModule({
  declarations: [RootComponent],
  imports: [
    CommonModule,
    SharedModule
  ]
})
export class ComponentsModule { }
