import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { EmpresaExplotacionResultadosCrearComponent } from './empresa-explotacion-resultados-crear/empresa-explotacion-resultados-crear.component';
import { EmpresaExplotacionResultadosDataResolver } from './empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosEditarComponent } from './empresa-explotacion-resultados-editar/empresa-explotacion-resultados-editar.component';
import { EmpresaExplotacionResultadosDatosGeneralesComponent } from './empresa-explotacion-resultados-formulario/empresa-explotacion-resultados-datos-generales/empresa-explotacion-resultados-datos-generales.component';
import { EmpresaExplotacionResultadosListadoComponent } from './empresa-explotacion-resultados-listado/empresa-explotacion-resultados-listado.component';
import { EmpresaExplotacionResultadosRoutingModule } from './empresa-explotacion-resultados-routing.module';

@NgModule({
  declarations: [
    EmpresaExplotacionResultadosListadoComponent,
    EmpresaExplotacionResultadosCrearComponent,
    EmpresaExplotacionResultadosEditarComponent,
    EmpresaExplotacionResultadosDatosGeneralesComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    EmpresaExplotacionResultadosRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    SgempSharedModule,
    CspSharedModule,
  ],
  providers: [
    EmpresaExplotacionResultadosDataResolver,
  ]
})
export class EmpresaExplotacionResultadosModule { }
