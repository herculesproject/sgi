import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: null,
  },
  {
    path: UrlUtils.crear.valueOf(),
    component: ActaCrearComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActaRoutingModule {
}
