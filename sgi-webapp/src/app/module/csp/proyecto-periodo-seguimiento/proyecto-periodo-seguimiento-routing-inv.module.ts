import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import {
  PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY,
  ProyectoPeriodoSeguimientoDataResolver
} from './proyecto-periodo-seguimiento-data.resolver';
import { ProyectoPeriodoSeguimientoEditarComponent } from './proyecto-periodo-seguimiento-editar/proyecto-periodo-seguimiento-editar.component';
import { ProyectoPeriodoSeguimientoDatosGeneralesComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-datos-generales/proyecto-periodo-seguimiento-datos-generales.component';
import { ProyectoPeriodoSeguimientoDocumentosComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-documentos/proyecto-periodo-seguimiento-documentos.component';
import { PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES } from './proyecto-periodo-seguimiento-route-names';
import { PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_PARAMS } from './proyecto-periodo-seguimiento-route-params';

const MSG_EDIT_TITLE = marker('title.csp.proyecto-periodo-seguimiento-cientifico.periodo');

const routes: SgiRoutes = [
  {
    path: `:${PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_PARAMS.ID}`,
    component: ProyectoPeriodoSeguimientoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    },
    resolve: {
      [PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY]: ProyectoPeriodoSeguimientoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoPeriodoSeguimientoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoPeriodoSeguimientoDocumentosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoPeriodoSeguimientoRoutingInv {
}
