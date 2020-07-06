import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RootComponent } from '../root/root.component';
import { PiiRootComponent } from './pii-root/pii-root.component';

const routes: Routes = [
  { path: '', component: PiiRootComponent },
  { path: '**', component: RootComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PiiRoutingModule {
}
