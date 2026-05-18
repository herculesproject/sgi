import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { environment } from '@env';
import {
  CreateCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  UpdateCtor
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { IGrupoDescriptorRequest } from './grupo-descriptor-request';
import { GRUPO_DESCRIPTOR_REQUEST_CONVERTER } from './grupo-descriptor-request.converter';
import { IGrupoDescriptorResponse } from './grupo-descriptor-response';
import { GRUPO_DESCRIPTOR_RESPONSE_CONVERTER } from './grupo-descriptor-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoDescriptorMixinBase:
  CreateCtor<IGrupoDescriptor, IGrupoDescriptor, IGrupoDescriptorRequest, IGrupoDescriptorResponse> &
  UpdateCtor<number, IGrupoDescriptor, IGrupoDescriptor, IGrupoDescriptorRequest, IGrupoDescriptorResponse> &
  FindByIdCtor<number, IGrupoDescriptor, IGrupoDescriptorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_DESCRIPTOR_REQUEST_CONVERTER,
        GRUPO_DESCRIPTOR_RESPONSE_CONVERTER
      ),
      GRUPO_DESCRIPTOR_REQUEST_CONVERTER,
      GRUPO_DESCRIPTOR_RESPONSE_CONVERTER
    ),
    GRUPO_DESCRIPTOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoDescriptorService extends _GrupoDescriptorMixinBase {
  private static readonly MAPPING = '/grupodescriptores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoDescriptorService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
