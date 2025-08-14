import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { AreaTematicaCrearComponent } from './area-tematica-crear/area-tematica-crear.component';
import { AreaTematicaEditarComponent } from './area-tematica-editar/area-tematica-editar.component';
import { AreaTematicaArbolComponent } from './area-tematica-formulario/area-tematica-arbol/area-tematica-arbol.component';
import { AreaTematicaDatosGeneralesComponent } from './area-tematica-formulario/area-tematica-datos-generales/area-tematica-datos-generales.component';
import { AreaTematicaListadoComponent } from './area-tematica-listado/area-tematica-listado.component';
import { AreaTematicaRoutingModule } from './area-tematica-routing.module';
import { AreaTematicaResolver } from './area-tematica.resolver';

@NgModule({
  declarations: [AreaTematicaCrearComponent, AreaTematicaEditarComponent,
    AreaTematicaListadoComponent, AreaTematicaDatosGeneralesComponent, AreaTematicaArbolComponent],
  imports: [
    CommonModule,
    SharedModule,
    AreaTematicaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
    AreaTematicaResolver
  ]
})
export class AreaTematicaModule { }
