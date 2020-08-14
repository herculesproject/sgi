import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PiiRootComponent } from './pii-root/pii-root.component';

const routes: Routes = [
  { path: '', component: PiiRootComponent },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PiiRoutingModule {
}
