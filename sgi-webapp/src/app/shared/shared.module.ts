import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';

import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { FooterCrearComponent } from './footers/footer-crear/footer-crear.component';
import { ActionFooterComponent } from './action-footer/action-footer.component';
import { RootComponent } from './root/root.component';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BuscarPersonaComponent } from './buscar-persona/buscar-persona.component';
import { BuscarPersonaDialogoComponent } from './buscar-persona/dialogo/buscar-persona-dialogo.component';
import { ActionFragmentMenuItemComponent } from './action-fragment-menu-item/action-fragment-menu-item.component';
import { ActionFragmentLinkItemComponent } from './action-fragment-link-item/action-fragment-link-item.component';

@NgModule({
  declarations: [
    BreadcrumbComponent,
    FooterCrearComponent,
    ActionFooterComponent,
    RootComponent,
    BuscarPersonaComponent,
    BuscarPersonaDialogoComponent,
    ActionFragmentMenuItemComponent,
    ActionFragmentLinkItemComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    BreadcrumbComponent,
    FooterCrearComponent,
    ActionFooterComponent,
    RootComponent,
    BuscarPersonaComponent,
    BuscarPersonaDialogoComponent,
    ActionFragmentMenuItemComponent,
    ActionFragmentLinkItemComponent
  ]
})
export class SharedModule {
}
