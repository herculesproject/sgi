import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { PROYECTO_ANUALIDAD_DATA_KEY, ProyectoAnualidadDataResolver } from './proyecto-anualidad-data.resolver';
import { ProyectoAnualidadEditarComponent } from './proyecto-anualidad-editar/proyecto-anualidad-editar.component';
import { ProyectoAnualidadDatosGeneralesComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-datos-generales/proyecto-anualidad-datos-generales.component';
import { ProyectoAnualidadGastosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-gastos/proyecto-anualidad-gastos.component';
import { ProyectoAnualidadIngresosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-ingresos/proyecto-anualidad-ingresos.component';
import { ProyectoAnualidadResumenComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-resumen/proyecto-anualidad-resumen.component';
import { PROYECTO_ANUALIDAD_ROUTE_NAMES } from './proyecto-anualidad-route-names';
import { PROYECTO_ANUALIDAD_ROUTE_PARAMS } from './proyecto-anualidad-route-params';

const MSG_EDIT_TITLE = marker('csp.proyecto-presupuesto.anualidad');

const routes: SgiRoutes = [
  {
    path: `:${PROYECTO_ANUALIDAD_ROUTE_PARAMS.ID}`,
    component: ProyectoAnualidadEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    },
    resolve: {
      [PROYECTO_ANUALIDAD_DATA_KEY]: ProyectoAnualidadDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoAnualidadDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.GASTOS,
        component: ProyectoAnualidadGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.INGRESOS,
        component: ProyectoAnualidadIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.RESUMEN,
        component: ProyectoAnualidadResumenComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoAnualidadRoutingInv {
}
