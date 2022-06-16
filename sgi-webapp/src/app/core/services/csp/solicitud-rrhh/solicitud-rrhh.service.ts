import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate,
  SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { ISolicitudRrhhRequest } from './solicitud-rrhh-request';
import { SOLICITUD_RRHH_REQUEST_CONVERTER } from './solicitud-rrhh-request.converter';
import { ISolicitudRrhhResponse } from './solicitud-rrhh-response';
import { SOLICITUD_RRHH_RESPONSE_CONVERTER } from './solicitud-rrhh-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudRrhhMixinBase:
  CreateCtor<ISolicitudRrhh, ISolicitudRrhh, ISolicitudRrhhRequest, ISolicitudRrhhResponse> &
  UpdateCtor<number, ISolicitudRrhh, ISolicitudRrhh, ISolicitudRrhhRequest, ISolicitudRrhhResponse> &
  FindByIdCtor<number, ISolicitudRrhh, ISolicitudRrhhResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SOLICITUD_RRHH_REQUEST_CONVERTER,
        SOLICITUD_RRHH_RESPONSE_CONVERTER
      ),
      SOLICITUD_RRHH_REQUEST_CONVERTER,
      SOLICITUD_RRHH_RESPONSE_CONVERTER
    ),
    SOLICITUD_RRHH_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhService extends _SolicitudRrhhMixinBase {
  private static readonly MAPPING = '/solicitudes-rrhh';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhService.MAPPING}`,
      http,
    );
  }

}
