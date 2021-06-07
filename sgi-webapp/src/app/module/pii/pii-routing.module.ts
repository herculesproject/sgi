import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { PiiInicioComponent } from './pii-inicio/pii-inicio.component';
import { PiiRootComponent } from './pii-root/pii-root.component';

const MSG_ROOT_TITLE = marker('pii.root.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: PiiRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: PiiInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      { path: '**', component: null }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PiiRoutingModule { }
