import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { SolicitudProteccionCrearComponent } from './solicitud-proteccion-crear/solicitud-proteccion-crear.component';
import { SOLICITUD_PROTECCION_DATA_KEY, SolicitudProteccionDataResolver } from './solicitud-proteccion-data.resolver';
import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar/solicitud-proteccion-editar.component';
import { SolicitudProteccionDatosGeneralesComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.component';
import { SolicitudProteccionProcedimientosComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-procedimientos/solicitud-proteccion-procedimientos.component';
import { SOLICITUD_PROTECCION_ROUTE_NAMES } from './solicitud-proteccion-route-names';
import { SOLICITUD_PROTECCION_ROUTE_PARAMS } from './solicitud-proteccion-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: SolicitudProteccionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: SOLICITUD_PROTECCION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PII-INV-C'
    },
    resolve: {
      [SOLICITUD_PROTECCION_DATA_KEY]: SolicitudProteccionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.PROCEDIMIENTOS,
        component: SolicitudProteccionProcedimientosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${SOLICITUD_PROTECCION_ROUTE_PARAMS.ID}`,
    component: SolicitudProteccionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: SOLICITUD_PROTECCION_KEY,
      hasAuthority: 'PII-INV-E'
    },
    resolve: {
      [SOLICITUD_PROTECCION_DATA_KEY]: SolicitudProteccionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.PROCEDIMIENTOS,
        component: SolicitudProteccionProcedimientosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudProteccionRoutingModule { }
