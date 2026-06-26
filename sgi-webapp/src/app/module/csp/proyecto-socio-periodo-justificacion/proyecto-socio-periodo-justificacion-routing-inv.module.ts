import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { ProyectoSocioPeriodoJustificacionDataResolver, PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY } from './proyecto-socio-periodo-justificacion-data.resolver';
import { ProyectoSocioPeriodoJustificacionEditarComponent } from './proyecto-socio-periodo-justificacion-editar/proyecto-socio-periodo-justificacion-editar.component';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-datos-generales/proyecto-socio-periodo-justificacion-datos-generales.component';
import { ProyectoSocioPeriodoJustificacionDocumentosComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-documentos/proyecto-socio-periodo-justificacion-documentos.component';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES } from './proyecto-socio-periodo-justificacion-names';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS } from './proyecto-socio-periodo-justificacion-route-params';


const MSG_EDIT_TITLE = marker('title.csp.proyecto-socio-periodo-justificacion');

const routes: SgiRoutes = [
  {
    path: `:${PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS.ID}`,
    component: ProyectoSocioPeriodoJustificacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    },
    resolve: {
      [PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY]: ProyectoSocioPeriodoJustificacionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoSocioPeriodoJustificacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoSocioPeriodoJustificacionDocumentosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoSocioPeriodoJustificacionRoutingInv {
}
