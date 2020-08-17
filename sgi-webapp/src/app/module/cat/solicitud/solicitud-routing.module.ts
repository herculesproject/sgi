import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SolicitudActualizarComponent } from './solicitud-actualizar/solicitud-actualizar.component';

const routes: Routes = [
  {
    path: '',
    component: SolicitudActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudRoutingModule { }
