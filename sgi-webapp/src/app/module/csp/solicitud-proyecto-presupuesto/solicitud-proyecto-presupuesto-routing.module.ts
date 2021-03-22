import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudProyectoPresupuestoCrearComponent } from './solicitud-proyecto-presupuesto-crear/solicitud-proyecto-presupuesto-crear.component';
import { SolicitudProyectoPresupuestoEditarComponent } from './solicitud-proyecto-presupuesto-editar/solicitud-proyecto-presupuesto-editar.component';
import { SolicitudProyectoPresupuestoDatosGeneralesComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-datos-generales/solicitud-proyecto-presupuesto-datos-generales.component';
import { SolicitudProyectoPresupuestoPartidasGastoComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-partidas-gasto/solicitud-proyecto-presupuesto-partidas-gasto.component';
import { SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES } from './solicitud-proyecto-presupuesto-route-names';

const MSG_EDIT_TITLE = marker('csp.solicitud-proyecto-presupuesto');
const MSG_NEW_TITLE = marker('title.csp.solicitud-proyecto-presupuesto.new');

const routes: SgiRoutes = [
  {
    path: ROUTE_NAMES.NEW,
    component: SolicitudProyectoPresupuestoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProyectoPresupuestoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.PARTIDAS_GASTO,
        component: SolicitudProyectoPresupuestoPartidasGastoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:id`,
    component: SolicitudProyectoPresupuestoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProyectoPresupuestoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.PARTIDAS_GASTO,
        component: SolicitudProyectoPresupuestoPartidasGastoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SolicitudProyectoPresupuestoRoutingModule {
}
