import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';
import { ActionGuard } from '@core/guards/master-form.guard';
import { CONVOCATORIA_REUNION_ROUTE_NAMES } from './convocatoria-reunion-route-names';
import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ConvocatoriaReunionListadoMemoriasComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-listado-memorias/convocatoria-reunion-listado-memorias.component';

const MSG_LISTADO_TITLE = marker('eti.convocatoriaReunion.listado.titulo');
const MSG_NEW_TITLE = marker('eti.convocatoriaReunion.crear.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaReunionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-CNV-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaReunionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      hasAuthorityForAnyUO: 'ETI-CNV-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaReunionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.ASIGNACION_MEMORIAS,
        component: ConvocatoriaReunionListadoMemoriasComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaReunionRoutingModule {
}
