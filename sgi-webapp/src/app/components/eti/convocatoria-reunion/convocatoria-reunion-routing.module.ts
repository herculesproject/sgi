import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: ConvocatoriaReunionListadoComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaReunionRoutingModule {
}
