import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ActionService } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.fragment';
import { EjecucionPresupuestariaGastosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.fragment';
import { EjecucionPresupuestariaIngresosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.fragment';
import { ProyectosFragment } from './ejecucion-economica-formulario/proyectos/proyectos.fragment';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

export interface IEjecucionEconomicaData {
  readonly: boolean;
  proyectoSge: IProyectoSge;
  proyectosRelacionados: IProyecto[];
}

@Injectable()
export class EjecucionEconomicaActionService extends ActionService {

  public readonly FRAGMENT = {
    PROYECTOS: 'proyectos',
    EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL: 'ejecucion-presupuestaria-estado-actual',
    EJECUCION_PRESUPUESTARIA_GASTOS: 'ejecucion-presupuestaria-gastos',
    EJECUCION_PRESUPUESTARIA_INGRESOS: 'ejecucion-presupuestaria-ingresos'
  };

  private proyectos: ProyectosFragment;
  private ejecucionPresupuestariaEstadoActual: EjecucionPresupuestariaEstadoActualFragment;
  private ejecucionPresupuestariaGastos: EjecucionPresupuestariaGastosFragment;
  private ejecucionPresupuestariaIngresos: EjecucionPresupuestariaIngresosFragment;

  private readonly data: IEjecucionEconomicaData;

  constructor(
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    personaService: PersonaService,
    proyectoAnualidadService: ProyectoAnualidadService,
    ejecucionEconomicaService: EjecucionEconomicaService
  ) {
    super();

    this.data = route.snapshot.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID));

    this.proyectos = new ProyectosFragment(id, this.data.proyectoSge, this.data.proyectosRelacionados);

    this.ejecucionPresupuestariaEstadoActual = new EjecucionPresupuestariaEstadoActualFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaGastos = new EjecucionPresupuestariaGastosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaIngresos = new EjecucionPresupuestariaIngresosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.addFragment(this.FRAGMENT.PROYECTOS, this.proyectos);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, this.ejecucionPresupuestariaEstadoActual);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_GASTOS, this.ejecucionPresupuestariaGastos);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_INGRESOS, this.ejecucionPresupuestariaIngresos);
  }

}
