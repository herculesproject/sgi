import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { EjecucionEconomicaDataResolver, EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { DetalleOperacionesGastosComponent } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.component';
import { DetalleOperacionesIngresosComponent } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.component';
import { DetalleOperacionesModificacionesComponent } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.component';
import { EjecucionPresupuestariaEstadoActualComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.component';
import { EjecucionPresupuestariaGastosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.component';
import { EjecucionPresupuestariaIngresosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.component';
import { FacturasGastosComponent } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.component';
import { PersonalContratadoComponent } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { ViajesDietasComponent } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.component';
import { EjecucionEconomicaListadoInvComponent } from './ejecucion-economica-listado-inv/ejecucion-economica-listado-inv.component';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from './ejecucion-economica-route-names';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

const EJECUCION_ECONOMICA_KEY = marker('menu.principal.inv.ejecucion-economica');

const routeDataInv = {
  title: EJECUCION_ECONOMICA_KEY,
  hasAuthorityForAnyUO: 'CSP-EJEC-INV-VR',
  module: Module.INV
};

const routes: SgiRoutes = [
  {
    path: '',
    component: EjecucionEconomicaListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: routeDataInv
  },
  {
    path: `:${EJECUCION_ECONOMICA_ROUTE_PARAMS.ID}`,
    component: EjecucionEconomicaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [EJECUCION_ECONOMICA_DATA_KEY]: EjecucionEconomicaDataResolver
    },
    data: routeDataInv,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS,
        component: ProyectosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL,
        component: EjecucionPresupuestariaEstadoActualComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_GASTOS,
        component: EjecucionPresupuestariaGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_INGRESOS,
        component: EjecucionPresupuestariaIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_GASTOS,
        component: DetalleOperacionesGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_INGRESOS,
        component: DetalleOperacionesIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_MODIFICACIONES,
        component: DetalleOperacionesModificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.FACTURAS_GASTOS,
        component: FacturasGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.VIAJES_DIETAS,
        component: ViajesDietasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.PERSONAL_CONTRATADO,
        component: PersonalContratadoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EjecucionEconomicaRoutingInvModule {
}
