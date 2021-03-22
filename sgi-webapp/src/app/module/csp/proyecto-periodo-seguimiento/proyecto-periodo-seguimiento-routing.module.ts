import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoPeriodoSeguimientoCrearComponent } from './proyecto-periodo-seguimiento-crear/proyecto-periodo-seguimiento-crear.component';
import { ProyectoPeriodoSeguimientoEditarComponent } from './proyecto-periodo-seguimiento-editar/proyecto-periodo-seguimiento-editar.component';
import { ProyectoPeriodoSeguimientoDatosGeneralesComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-datos-generales/proyecto-periodo-seguimiento-datos-generales.component';
import { ProyectoPeriodoSeguimientoDocumentosComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-documentos/proyecto-periodo-seguimiento-documentos.component';
import { PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES } from './proyecto-periodo-seguimiento-route-names';
import { ProyectoPeriodoSeguimientoGuard } from './proyecto-periodo-seguimiento.guard';


const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_EDIT_TITLE = marker('title.csp.proyecto-periodo-seguimiento-cientifico.periodo');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoPeriodoSeguimientoCrearComponent,
    canActivate: [SgiAuthGuard, ProyectoPeriodoSeguimientoGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_EDIT_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
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
  },
  {
    path: `:id`,
    component: ProyectoPeriodoSeguimientoEditarComponent,
    canActivate: [SgiAuthGuard, ProyectoPeriodoSeguimientoGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE
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
export class ProyectoPeriodoSeguimientoRouting {
}
