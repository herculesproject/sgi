import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionBaremoService } from '@core/services/prc/configuracion-baremo/configuracion-baremo.service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaBaremacionBaremosPuntuacionesFragment } from './convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';
import { ConvocatoriaBaremacionDatosGeneralesFragment } from './convocatoria-baremacion-formulario/convocatoria-baremacion-datos-generales/convocatoria-baremacion-datos-generales.fragment';
import { CONVOCATORIA_BAREMACION_ROUTE_PARAMS } from './convocatoria-baremacion-params';
import { CONVOCATORIA_BAREMACION_DATA_KEY } from './convocatoria-baremacion.resolver';

export interface IConvocatoriaBaremacionData {
  canEdit: boolean;
  convocatoriaBaremacion: IConvocatoriaBaremacion;
}

@Injectable()
export class ConvocatoriaBaremacionActionService extends ActionService {

  public readonly id: number;
  private data: IConvocatoriaBaremacionData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    BAREMOS_PUNTUACIONES: 'baremos-puntuaciones'
  };

  private datosGenerales: ConvocatoriaBaremacionDatosGeneralesFragment;
  private baremosPuntuaciones: ConvocatoriaBaremacionBaremosPuntuacionesFragment

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaBaremacionService: ConvocatoriaBaremacionService,
    configuracionBaremoService: ConfiguracionBaremoService
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(CONVOCATORIA_BAREMACION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[CONVOCATORIA_BAREMACION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new ConvocatoriaBaremacionDatosGeneralesFragment(
      this.id, this.data?.convocatoriaBaremacion, this.data?.canEdit, convocatoriaBaremacionService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.baremosPuntuaciones = new ConvocatoriaBaremacionBaremosPuntuacionesFragment(
        logger,
        this.id,
        this.data?.convocatoriaBaremacion,
        this.data?.canEdit,
        configuracionBaremoService,
        convocatoriaBaremacionService
      );

      this.addFragment(this.FRAGMENT.BAREMOS_PUNTUACIONES, this.baremosPuntuaciones);
    }
  }
}
