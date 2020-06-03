import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MaterialDesignModule } from '@material/material-design.module';

@NgModule({
  declarations: [],
  imports: [
    RouterModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class SharedModule { }
