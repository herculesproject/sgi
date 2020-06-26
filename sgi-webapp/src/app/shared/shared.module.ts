import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';

import { HeaderComponent } from './componentes-layout/header/header.component';
import { MenuPrincipalComponent } from './componentes-layout/menu-principal/menu-principal.component';
import { MenuSecundarioComponent } from './componentes-layout/menu-secundario/menu-secundario.component';
import { DialogComponent } from './componentes-shared/dialog/dialog.component';
import { SnackBarComponent } from './componentes-shared/snack-bar/snack-bar.component';

@NgModule({
  declarations: [
    HeaderComponent,
    MenuSecundarioComponent,
    MenuPrincipalComponent,
    DialogComponent,
    SnackBarComponent,
  ],
  imports: [
    RouterModule,
    FlexLayoutModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
  ],
  exports: [
    MaterialDesignModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    HeaderComponent,
    MenuPrincipalComponent,
    MenuSecundarioComponent,
  ],
})
export class SharedModule {}
