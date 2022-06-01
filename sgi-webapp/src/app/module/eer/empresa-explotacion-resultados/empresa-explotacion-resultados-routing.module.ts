import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EmpresaExplotacionResultadosCrearComponent } from './empresa-explotacion-resultados-crear/empresa-explotacion-resultados-crear.component';
import { EmpresaExplotacionResultadosDataResolver, EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from './empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosEditarComponent } from './empresa-explotacion-resultados-editar/empresa-explotacion-resultados-editar.component';
import { EmpresaExplotacionResultadosDatosGeneralesComponent } from './empresa-explotacion-resultados-formulario/empresa-explotacion-resultados-datos-generales/empresa-explotacion-resultados-datos-generales.component';
import { EmpresaExplotacionResultadosListadoComponent } from './empresa-explotacion-resultados-listado/empresa-explotacion-resultados-listado.component';
import { EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_NAMES } from './empresa-explotacion-resultados-route-names';
import { EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS } from './empresa-explotacion-resultados-route-params';

const EMPRESA_TITLE_KEY = marker('eer.empresa-explotacion-resultados');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: EmpresaExplotacionResultadosListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: EMPRESA_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['EER-EER-E', 'EER-EER-B', 'EER-EER-R', 'EER-EER-V']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: EmpresaExplotacionResultadosCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: EMPRESA_TITLE_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
      },
      hasAuthorityForAnyUO: 'EER-EER-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_NAMES.DATOS_GENERALES,
        component: EmpresaExplotacionResultadosDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  },
  {
    path: `:${EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS.ID}`,
    component: EmpresaExplotacionResultadosEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY]: EmpresaExplotacionResultadosDataResolver
    },
    data: {
      title: EMPRESA_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['EER-EER-E', 'EER-EER-V']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_NAMES.DATOS_GENERALES,
        component: EmpresaExplotacionResultadosDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmpresaExplotacionResultadosRoutingModule { }
