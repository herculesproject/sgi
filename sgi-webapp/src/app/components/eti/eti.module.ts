import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { EtiRoutingModule } from './eti-routing.module';
import { EtiRootComponent } from './eti-root/eti-root.component';
import { EtiMenuPrincipalComponent } from './eti-menu-principal/eti-menu-principal.component';
import { ComponentsModule } from '../components.module';
import { SgiAuthModule } from '@sgi/framework/auth';

@NgModule({
  declarations: [EtiRootComponent, EtiMenuPrincipalComponent],
  imports: [
    SharedModule,
    CommonModule,
    EtiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
    SgiAuthModule
  ],
  providers: [],
  exports: [TranslateModule],
})
export class EtiModule {
}
