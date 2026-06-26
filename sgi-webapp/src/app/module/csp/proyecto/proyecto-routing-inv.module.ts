import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { InvestigadorAccessConstraint, ProyectoAccessGuard } from './proyecto-acccess.guard';
import { ProyectoDataResolver, PROYECTO_DATA_KEY } from './proyecto-data.resolver';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoAmortizacionFondosComponent } from './proyecto-formulario/proyecto-amortizacion-fondos/proyecto-amortizacion-fondos.component';
import { ProyectoAreaConocimientoComponent } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.component';
import { ProyectoCalendarioFacturacionComponent } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.component';
import { ProyectoCalendarioJustificacionComponent } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.component';
import { ProyectoClasificacionesComponent } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.component';
import { ProyectoConceptosGastoComponent } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.component';
import { ProyectoConsultaPresupuestoComponent } from './proyecto-formulario/proyecto-consulta-presupuesto/proyecto-consulta-presupuesto.component';
import { ProyectoContextoComponent } from './proyecto-formulario/proyecto-contexto/proyecto-contexto.component';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { ProyectoDocumentosComponent } from './proyecto-formulario/proyecto-documentos/proyecto-documentos.component';
import { ProyectoEntidadGestoraComponent } from './proyecto-formulario/proyecto-entidad-gestora/proyecto-entidad-gestora.component';
import { ProyectoEntidadesConvocantesComponent } from './proyecto-formulario/proyecto-entidades-convocantes/proyecto-entidades-convocantes.component';
import { ProyectoEntidadesFinanciadorasComponent } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.component';
import { ProyectoEquipoComponent } from './proyecto-formulario/proyecto-equipo/proyecto-equipo.component';
import { ProyectoHistoricoEstadosComponent } from './proyecto-formulario/proyecto-historico-estados/proyecto-historico-estados.component';
import { ProyectoHitosComponent } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.component';
import { ProyectoPaqueteTrabajoComponent } from './proyecto-formulario/proyecto-paquete-trabajo/proyecto-paquete-trabajo.component';
import { ProyectoPartidasPresupuestariasComponent } from './proyecto-formulario/proyecto-partidas-presupuestarias/proyecto-partidas-presupuestarias.component';
import { ProyectoPeriodoSeguimientosComponent } from './proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.component';
import { ProyectoPlazosComponent } from './proyecto-formulario/proyecto-plazos/proyecto-plazos.component';
import { ProyectoPresupuestoComponent } from './proyecto-formulario/proyecto-presupuesto/proyecto-presupuesto.component';
import { ProyectoProrrogasComponent } from './proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.component';
import { ProyectoProyectosSgeComponent } from './proyecto-formulario/proyecto-proyectos-sge/proyecto-proyectos-sge.component';
import { ProyectoRelacionesComponent } from './proyecto-formulario/proyecto-relaciones/proyecto-relaciones.component';
import { ProyectoResponsableEconomicoComponent } from './proyecto-formulario/proyecto-responsable-economico/proyecto-responsable-economico.component';
import { ProyectoSociosComponent } from './proyecto-formulario/proyecto-socios/proyecto-socios.component';
import { ProyectoUnidadesVinculacionComponent } from './proyecto-formulario/proyecto-unidades-vinculacion/proyecto-unidades-vinculacion.component';
import { ProyectoListadoInvComponent } from './proyecto-listado-inv/proyecto-listado-inv.component';
import { PROYECTO_ROUTE_NAMES } from './proyecto-route-names';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

const MSG_PROYECTOS_TITLE = marker('inv.proyecto.listado.titulo');
const MSG_PROYECTOS_VER_TITLE = marker('inv.proyecto.ver.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ProyectoListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_PROYECTOS_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    }
  },
  {
    path: `:${PROYECTO_ROUTE_PARAMS.ID}`,
    component: ProyectoEditarComponent,
    canActivate: [SgiAuthGuard],
    canActivateChild: [ProyectoAccessGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [PROYECTO_DATA_KEY]: ProyectoDataResolver
    },
    data: {
      title: MSG_PROYECTOS_VER_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV,
      // Default access constraint
      accessConstraint: InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.AMORTIZACION_FONDOS,
        component: ProyectoAmortizacionFondosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.AREA_CONOCIMIENTO,
        component: ProyectoAreaConocimientoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CALENDARIO_FACTURACION,
        component: ProyectoCalendarioFacturacionComponent,
        canDeactivate: [FragmentGuard],
        data: {
          accessConstraint: InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.CALENDARIO_JUSTIFICACION,
        component: ProyectoCalendarioJustificacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CLASIFICACIONES,
        component: ProyectoClasificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONSULTA_PRESUPUESTO,
        component: ProyectoConsultaPresupuestoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONTEXTO_PROYECTO,
        component: ProyectoContextoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD,
        component: ProyectoConceptosGastoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GASTO_PERMITIDO,
        redirectTo: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GASTO_NO_PERMITIDO,
        redirectTo: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDAD_GESTORA,
        component: ProyectoEntidadGestoraComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDADES_CONVOCANTES,
        component: ProyectoEntidadesConvocantesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDADES_FINANCIADORAS,
        component: ProyectoEntidadesFinanciadorasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.EQUIPO_PROYECTO,
        component: ProyectoEquipoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoDocumentosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.FASES,
        component: ProyectoPlazosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard],
        data: {
          accessConstraint: InvestigadorAccessConstraint.NONE
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: ProyectoHistoricoEstadosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.HITOS,
        component: ProyectoHitosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.IDENTIFICACION,
        component: ProyectoProyectosSgeComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PARTIDAS_PRESUPUESTARIAS,
        component: ProyectoPartidasPresupuestariasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRESUPUESTO,
        component: ProyectoPresupuestoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRORROGAS,
        component: ProyectoProrrogasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.RELACIONES,
        component: ProyectoRelacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.RESPONSABLE_ECONOMICO,
        component: ProyectoResponsableEconomicoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        component: ProyectoPeriodoSeguimientosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.SOCIOS,
        component: ProyectoSociosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PAQUETE_TRABAJO,
        component: ProyectoPaqueteTrabajoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.UNIDADES_VINCULACION,
        component: ProyectoUnidadesVinculacionComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    canActivateChild: [ProyectoAccessGuard],
    resolve: {
      [PROYECTO_DATA_KEY]: ProyectoDataResolver
    },
    data: {
      title: MSG_PROYECTOS_VER_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV,
      accessConstraint: InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA
    },
    children: [
      {
        path: PROYECTO_ROUTE_NAMES.SOCIOS,
        loadChildren: () =>
          import('../proyecto-socio/proyecto-socio-inv.module').then(
            (m) => m.ProyectoSocioInvModule
          )
      },
      {
        path: PROYECTO_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        loadChildren: () =>
          import('../proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-inv.module').then(
            (m) => m.ProyectoPeriodoSeguimientoInvModule
          )
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRORROGAS,
        loadChildren: () =>
          import('../proyecto-prorroga/proyecto-prorroga-inv.module').then(
            (m) => m.ProyectoProrrogaInvModule
          )
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GASTO_PERMITIDO,
        loadChildren: () =>
          import('../proyecto-concepto-gasto/proyecto-concepto-gasto-inv.module').then(
            (m) => m.ProyectoConceptoGastoInvModule
          ),
        data: {
          permitido: true
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GASTO_NO_PERMITIDO,
        loadChildren: () =>
          import('../proyecto-concepto-gasto/proyecto-concepto-gasto-inv.module').then(
            (m) => m.ProyectoConceptoGastoInvModule
          ),
        data: {
          permitido: false
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRESUPUESTO,
        loadChildren: () =>
          import('../proyecto-anualidad/proyecto-anualidad-inv.module').then(
            (m) => m.ProyectoAnualidadInvModule
          )
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoRoutingInvModule {
}
