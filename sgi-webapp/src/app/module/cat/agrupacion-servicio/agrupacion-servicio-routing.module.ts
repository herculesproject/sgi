import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AgrupacionServicioListadoComponent } from './agrupacion-servicio-listado/agrupacion-servicio-listado.component';
import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar/agrupacion-servicio-actualizar.component';
import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear/agrupacion-servicio-crear.component';
import { ROUTE_NAMES } from '@core/route.names';

const routes: Routes = [
  {
    path: '',
    component: AgrupacionServicioListadoComponent,
  },
  {
    path: ROUTE_NAMES.NEW,
    component: AgrupacionServicioCrearComponent,
  },
  {
    path: `${ROUTE_NAMES.EDIT}/:id`,
    component: AgrupacionServicioActualizarComponent,
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AgrupacionServicioRoutingModule { }
