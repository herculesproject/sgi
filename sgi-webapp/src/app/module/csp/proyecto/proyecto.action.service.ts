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



@Injectable()
export class ProyectoActionService extends ActionService {

  public readonly FRAGMENT = {
    FICHA_GENERAL: 'ficha-general'
  };

  private fichaGeneral: ProyectoFichaGeneralFragment;

  proyecto: IProyecto;

  constructor(
    fb: FormBuilder,
    private logger: NGXLogger,
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    unidadGestionService: UnidadGestionService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super();

    this.logger = logger;
    if (route.snapshot.data.proyecto) {
      this.proyecto = route.snapshot.data.proyecto;
      this.enableEdit();
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(fb, logger, this.proyecto?.id, proyectoService, unidadGestionService, convocatoriaService);


    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);
  }
}
