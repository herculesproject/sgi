import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MaterialDesignModule} from '@material/material-design.module';
import {TranslateModule} from '@ngx-translate/core';
import {SharedModule} from '@shared/shared.module';

import {ComponentsModule} from '../components.module';
import {CatMenuPrincipalComponent} from './cat-menu-principal/cat-menu-principal.component';
import {CatRootComponent} from './cat-root/cat-root.component';
import {CatRoutingModule} from './cat-routing.module';

@NgModule({
  declarations: [
    CatRootComponent,
    CatMenuPrincipalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    CatRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
  ],
  providers: [],
})
export class CatModule { }
