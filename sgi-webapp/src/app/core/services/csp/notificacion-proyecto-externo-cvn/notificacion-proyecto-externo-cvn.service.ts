import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_AUTORIZACION_REQUEST_CONVERTER } from './notificacion-proyecto-externo-cvn-asociar-autorizacion-request.converter';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_PROYECTO_REQUEST_CONVERTER } from './notificacion-proyecto-externo-cvn-asociar-proyecto-request.converter';
import { INotificacionProyectoExternoCVNRequest } from './notificacion-proyecto-externo-cvn-request';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER } from './notificacion-proyecto-externo-cvn-request.converter';
import { INotificacionProyectoExternoCVNResponse } from './notificacion-proyecto-externo-cvn-response';
import { NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER } from './notificacion-proyecto-externo-cvn-response.converter';

// tslint:disable-next-line: variable-name
const _INotificacionProyectoExternoCVNMixinBase:
  CreateCtor<INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVN,
    INotificacionProyectoExternoCVNRequest, INotificacionProyectoExternoCVNResponse> &
  FindByIdCtor<number, INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVNResponse> &
  FindAllCtor<INotificacionProyectoExternoCVN, INotificacionProyectoExternoCVNResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        SgiRestBaseService,
        NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER,
        NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
      ),
      NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
    ),
    NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class NotificacionProyectoExternoCvnService extends _INotificacionProyectoExternoCVNMixinBase {
  private static readonly MAPPING = '/notificacionesproyectosexternoscvn';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${NotificacionProyectoExternoCvnService.MAPPING}`,
      http,
    );
  }

  asociarAutorizacion(id: number, notificacionAutorizacionAsociada: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    return this.http.patch<INotificacionProyectoExternoCVNResponse>(`${this.endpointUrl}/${id}/asociarautorizacion`,
      NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_AUTORIZACION_REQUEST_CONVERTER.fromTarget(notificacionAutorizacionAsociada)
    ).pipe(
      map((response => NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

  asociarProyecto(id: number, notificacionProyectoAsociado: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    return this.http.patch<INotificacionProyectoExternoCVNResponse>(`${this.endpointUrl}/${id}/asociarproyecto`,
      NOTIFICACION_PROYECTO_EXTERNO_CVN_ASOCIAR_PROYECTO_REQUEST_CONVERTER.fromTarget(notificacionProyectoAsociado)
    ).pipe(
      map((response => NOTIFICACION_PROYECTO_EXTERNO_CVN_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

}
