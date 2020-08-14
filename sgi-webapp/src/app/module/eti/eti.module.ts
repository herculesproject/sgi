import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { EtiRoutingModule } from './eti-routing.module';
import { EtiRootComponent } from './eti-root/eti-root.component';
import { EtiMenuPrincipalComponent } from './eti-menu-principal/eti-menu-principal.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { InicioComponent } from './inicio/inicio.component';

@NgModule({
  declarations: [EtiRootComponent, EtiMenuPrincipalComponent, InicioComponent],
  imports: [
    SharedModule,
    CommonModule,
    EtiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  providers: []
})
export class EtiModule {
}
