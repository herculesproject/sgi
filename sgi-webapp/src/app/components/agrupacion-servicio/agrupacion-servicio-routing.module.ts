import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AgrupacionServicioListadoComponent } from './agrupacion-servicio-listado/agrupacion-servicio-listado.component';
import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar/agrupacion-servicio-actualizar.component';
import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear/agrupacion-servicio-crear.component';
import { UrlUtils } from '@core/utils/url-utils';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: AgrupacionServicioListadoComponent,
  },
  {
    path: UrlUtils.agregar.valueOf(),
    component: AgrupacionServicioCrearComponent,
  },
  {
    path: UrlUtils.actualizar.valueOf() + ':id',
    component: AgrupacionServicioActualizarComponent,
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AgrupacionServicioRoutingModule { }
