import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { PROYECTO_PRORROGA_DATA_KEY, ProyectoProrrogaDataResolver } from './proyecto-prorroga-data.resolver';
import { ProyectoProrrogaEditarComponent } from './proyecto-prorroga-editar/proyecto-prorroga-editar.component';
import { ProyectoProrrogaDatosGeneralesComponent } from './proyecto-prorroga-formulario/proyecto-prorroga-datos-generales/proyecto-prorroga-datos-generales.component';
import { ProyectoProrrogaDocumentosComponent } from './proyecto-prorroga-formulario/proyecto-prorroga-documentos/proyecto-prorroga-documentos.component';
import { PROYECTO_PRORROGA_ROUTE_NAMES } from './proyecto-prorroga-route-names';
import { PROYECTO_PRORROGA_ROUTE_PARAMS } from './proyecto-prorroga-route-params';

const MSG_EDIT_TITLE = marker('csp.proyecto-prorroga');

const routes: SgiRoutes = [
  {
    path: `:${PROYECTO_PRORROGA_ROUTE_PARAMS.ID}`,
    component: ProyectoProrrogaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    },
    resolve: {
      [PROYECTO_PRORROGA_DATA_KEY]: ProyectoProrrogaDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_PRORROGA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_PRORROGA_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoProrrogaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_PRORROGA_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoProrrogaDocumentosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoProrrogaRoutingInv {
}
