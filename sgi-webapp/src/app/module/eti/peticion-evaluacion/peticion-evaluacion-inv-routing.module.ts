import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv/peticion-evaluacion-listado-inv.component';
import { PeticionEvaluacionListadoGesComponent } from './peticion-evaluacion-listado-ges/peticion-evaluacion-listado-ges.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ROUTE_NAMES } from '@core/route.names';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('eti.peticionEvaluacion.listado.titulo');
const MSG_NEW_TITLE = marker('eti.peticionEvaluacion.crear.titulo');
const MSG_EDIT_TITLE = marker('eti.peticionEvaluacion.actualizar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: PeticionEvaluacionListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-PEV-VR'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PeticionEvaluacionInvRoutingModule {
}
