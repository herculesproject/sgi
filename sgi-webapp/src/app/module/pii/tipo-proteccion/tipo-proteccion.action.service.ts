import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { ActionService } from '@core/services/action-service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { NGXLogger } from 'ngx-logger';
import { TipoProteccionDatosGeneralesFragment } from './tipo-proteccion-formulario/tipo-proteccion-datos-generales/tipo-proteccion-datos-generales.fragment';

@Injectable()
export class TipoProteccionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales'
  };
  private tipoProteccion: ITipoProteccion;
  private tipoProteccionDatosGenerales: TipoProteccionDatosGeneralesFragment;

  get getTipoProteccion() {
    return this.tipoProteccion;
  }

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    tipoProteccion: TipoProteccionService
  ) {
    super();
    this.tipoProteccion = {} as ITipoProteccion;
    if (route.snapshot.data.tipoProteccion) {
      this.tipoProteccion = route.snapshot.data.tipoProteccion;
      this.enableEdit();
    }

    this.tipoProteccionDatosGenerales = new TipoProteccionDatosGeneralesFragment(logger, this.tipoProteccion?.id,
      tipoProteccion);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.tipoProteccionDatosGenerales);
  }

}
