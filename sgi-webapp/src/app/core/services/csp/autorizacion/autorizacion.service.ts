import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IAutorizacionRequest } from './autorizacion-request';
import { AUTORIZACION_REQUEST_CONVERTER } from './autorizacion-request.converter';
import { IAutorizacionResponse } from './autorizacion-response';
import { AUTORIZACION_RESPONSE_CONVERTER } from './autorizacion-response.converter';

// tslint:disable-next-line: variable-name
const _AutorizacionMixinBase:
  CreateCtor<IAutorizacion, IAutorizacion, IAutorizacionRequest, IAutorizacionResponse> &
  UpdateCtor<number, IAutorizacion, IAutorizacion, IAutorizacionRequest, IAutorizacionResponse> &
  FindByIdCtor<number, IAutorizacion, IAutorizacionResponse> &
  FindAllCtor<IAutorizacion, IAutorizacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          AUTORIZACION_REQUEST_CONVERTER,
          AUTORIZACION_RESPONSE_CONVERTER
        ),
        AUTORIZACION_REQUEST_CONVERTER,
        AUTORIZACION_RESPONSE_CONVERTER
      ),
      AUTORIZACION_RESPONSE_CONVERTER
    ),
    AUTORIZACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AutorizacionService extends _AutorizacionMixinBase {
  private static readonly MAPPING = '/autorizaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AutorizacionService.MAPPING}`,
      http,
    );
  }
}
