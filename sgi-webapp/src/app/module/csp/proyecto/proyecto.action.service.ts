import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { NGXLogger } from 'ngx-logger';
import { FormBuilder } from '@angular/forms';
import { ProyectoFichaGeneralFragment } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.fragment';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { IProyecto } from '@core/models/csp/proyecto';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoEntidadesFinanciadorasFragment } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';
import { ProyectoEntidadFinanciadoraService } from '@core/services/csp/proyecto-entidad-financiadora.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { ProyectoHitosFragment } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.fragment';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoSociosFragment } from './proyecto-formulario/proyecto-socios/proyecto-socios.fragment';
import { BehaviorSubject } from 'rxjs';


@Injectable()
export class ProyectoActionService extends ActionService {

  public readonly FRAGMENT = {
    FICHA_GENERAL: 'ficha-general',
    ENTIDADES_FINANCIADORAS: 'entidades-financiadoras',
    SOCIOS: 'socios',
    HITOS: 'hitos'
  };

  private fichaGeneral: ProyectoFichaGeneralFragment;
  private entidadesFinanciadoras: ProyectoEntidadesFinanciadorasFragment;
  private hitos: ProyectoHitosFragment;
  private socios: ProyectoSociosFragment;

  proyecto: IProyecto;
  readonly = false;

  get modeloEjecucionId(): number {
    return this.getDatosGeneralesProyecto().modeloEjecucion?.id;
  }

  public disabledAddSocios$ = new BehaviorSubject<boolean>(false);

  constructor(
    fb: FormBuilder,
    private logger: NGXLogger,
    route: ActivatedRoute,
    protected proyectoService: ProyectoService,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService,
    unidadGestionService: UnidadGestionService,
    convocatoriaService: ConvocatoriaService,
    proyectoEntidadFinanciadoraService: ProyectoEntidadFinanciadoraService,
    proyectoHitoService: ProyectoHitoService
  ) {
    super();

    if (route.snapshot.data.proyecto) {
      this.proyecto = route.snapshot.data.proyecto;
      this.enableEdit();
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(fb, logger, this.proyecto?.id, proyectoService, unidadGestionService, convocatoriaService, this);
    if (this.isEdit()) {
      this.entidadesFinanciadoras = new ProyectoEntidadesFinanciadorasFragment(logger, this.proyecto?.id, proyectoService, proyectoEntidadFinanciadoraService, empresaEconomicaService, false);
      this.socios = new ProyectoSociosFragment(logger, this.proyecto?.id, empresaEconomicaService, proyectoService, proyectoSocioService, this);
      this.hitos = new ProyectoHitosFragment(logger, this.proyecto?.id, proyectoService, proyectoHitoService, this.readonly);
    }

    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);
    if (this.isEdit()) {
      this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);
      this.addFragment(this.FRAGMENT.SOCIOS, this.socios);
      this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    }

  }

  /**
   * Recupera los datos del proyecto del formulario de datos generales,
   * si no se ha cargado el formulario de datos generales se recuperan los datos de la proyecto que se esta editando.
   *
   * @returns los datos generales del proyecto.
   */
  private getDatosGeneralesProyecto(): IProyecto {
    return this.fichaGeneral.isInitialized() ? this.fichaGeneral.getValue() : {} as IProyecto;
  }


  /**
   * Indica si es un proyecto colaborativo
   */
  set isProyectoColaborativo(isColaborativo: boolean) {
    this.disabledAddSocios$.next(isColaborativo);
  }


  /**
   * Comprueba si es un proyecto colaborativo, si la pestaña fichaGeneral no esta inicializada 
   * y es una edicion se hace la consulta y si no se recupera el valor previo de isProyectoColavorativo.
   */
  checkProyectoColavorativo(): void {
    this.logger.debug(ProyectoActionService.name, 'checkProyectoColavorativo()', 'start');

    if (!this.fichaGeneral.isInitialized() && this.proyecto?.id) {
      const subscription = this.proyectoService.findById(this.proyecto.id)
        .subscribe((proyecto) => {
          this.isProyectoColaborativo = proyecto ? proyecto.colaborativo : false;
          this.logger.debug(ProyectoActionService.name, 'checkProyectoColavorativo()', 'end');
        },
          (error) => {
            this.isProyectoColaborativo = false;
            this.logger.error(ProyectoActionService.name, 'checkProyectoColavorativo()', error);
          }
        );
      this.subscriptions.push(subscription);
    }

    this.logger.debug(ProyectoActionService.name, 'checkProyectoColavorativo()', 'end');
  }

}
