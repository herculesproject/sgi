import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AgrupacionServicioListadoComponent } from './agrupacion-servicio-listado/agrupacion-servicio-listado.component';
import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar/agrupacion-servicio-actualizar.component';
import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear/agrupacion-servicio-crear.component';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('cat.servicio.listado.titulo');
const MSG_NEW_TITLE = marker('cat.servicio.crear.titulo');
const MSG_EDIT_TITLE = marker('cat.servicio.actualizar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: AgrupacionServicioListadoComponent,
    data: {
      title: MSG_LISTADO_TITLE
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: AgrupacionServicioCrearComponent,
    data: {
      title: MSG_NEW_TITLE
    }
  },
  {
    path: ':id',
    component: AgrupacionServicioActualizarComponent,
    data: {
      title: MSG_EDIT_TITLE
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AgrupacionServicioRoutingModule { }
