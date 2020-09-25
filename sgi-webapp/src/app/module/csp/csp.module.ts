import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspRoutingModule } from './csp-routing.module';
import { CspRootComponent } from './csp-root/csp-root.component';
import { CspMenuPrincipalComponent } from './csp-menu-principal/csp-menu-principal.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { CspInicioComponent } from './csp-inicio/csp-inicio.component';

@NgModule({
  declarations: [CspRootComponent, CspMenuPrincipalComponent, CspInicioComponent],
  imports: [
    SharedModule,
    CommonModule,
    CspRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  providers: []
})
export class CspModule {
}
