import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

import { MaterialDesignModule } from '@material/material-design.module';
import { HeaderComponent } from './componentes-layout/header/header.component';
import { MenuSecundarioComponent } from './componentes-layout/menu-secundario/menu-secundario.component';
import { MenuPrincipalComponent } from './componentes-layout/menu-principal/menu-principal.component';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [HeaderComponent, MenuSecundarioComponent, MenuPrincipalComponent],
  imports: [
    RouterModule,
    FlexLayoutModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  exports: [
    MaterialDesignModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    HeaderComponent,
    MenuPrincipalComponent,
    MenuSecundarioComponent
  ]
})
export class SharedModule { }
