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
import { ProyectoHitosFragment } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.fragment';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito.service';



@Injectable()
export class ProyectoActionService extends ActionService {

  public readonly FRAGMENT = {
    FICHA_GENERAL: 'ficha-general',
    HITOS: 'hitos'
  };

  private fichaGeneral: ProyectoFichaGeneralFragment;
  private hitos: ProyectoHitosFragment;

  proyecto: IProyecto;
  readonly = false;

  get modeloEjecucionId(): number {
    return this.getDatosGeneralesProyecto().modeloEjecucion?.id;
  }

  constructor(
    fb: FormBuilder,
    private logger: NGXLogger,
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    unidadGestionService: UnidadGestionService,
    private convocatoriaService: ConvocatoriaService,
    proyectoHitoService: ProyectoHitoService,
  ) {
    super();

    this.logger = logger;
    if (route.snapshot.data.proyecto) {
      this.proyecto = route.snapshot.data.proyecto;
      this.enableEdit();
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(fb, logger, this.proyecto?.id,
      proyectoService, unidadGestionService, convocatoriaService);
    this.hitos = new ProyectoHitosFragment(logger, this.proyecto?.id, proyectoService,
      proyectoHitoService, this.readonly);

    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);

    if (this.isEdit()) {
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
}
