import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { DialogComponent } from './dialog/dialog.component';
import { HeaderComponent } from './header/header.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SelectorModuloComponent } from './selector-modulo/selector-modulo.component';
import { SnackBarComponent } from './snack-bar/snack-bar.component';
import { SpinnerComponent } from './spinner/spinner.component';

@NgModule({
  declarations: [
    HeaderComponent,
    SelectorModuloComponent,
    DialogComponent,
    SnackBarComponent,
    NavbarComponent,
    SpinnerComponent
  ],
  imports: [
    CommonModule,
    MaterialDesignModule,
    TranslateModule,
    SharedModule,
    RouterModule,
    SgiAuthModule
  ],
  exports: [
    HeaderComponent,
    NavbarComponent
  ]
})
export class BlockModule { }
