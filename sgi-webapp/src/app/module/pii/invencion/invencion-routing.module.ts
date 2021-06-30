import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvencionCrearComponent } from './invencion-crear/invencion-crear.component';
import { InvencionDatosGeneralesComponent } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.component';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';
import { INVENCION_ROUTE_NAMES } from './invencion-route-names';

const MSG_LISTADO_TITLE = marker('menu.pii.invenciones');
const MSG_NEW_TITLE = marker('title.new.entity');
const INVENCION_KEY = marker('pii.invencion');

const routes: SgiRoutes = [
  {
    path: '',
    component: InvencionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: InvencionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: INVENCION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'PII-INV-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: INVENCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: INVENCION_ROUTE_NAMES.DATOS_GENERALES,
        component: InvencionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InvencionRoutingModule {
}
