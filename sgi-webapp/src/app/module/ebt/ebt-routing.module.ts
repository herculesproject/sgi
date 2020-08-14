import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EbtRootComponent } from './ebt-root/ebt-root.component';

const routes: Routes = [
  { path: '**', component: EbtRootComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EbtRoutingModule {
}
