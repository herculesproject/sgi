import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhTutorRequest } from './solicitud-rrhh-tutor-request';

class SolicitudRrhhTutorRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhTutorRequest, ISolicitudRrhhTutor> {
  toTarget(value: ISolicitudRrhhTutorRequest): ISolicitudRrhhTutor {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutor;
    }
    return {
      tutorRef: value.tutorRef
    };
  }

  fromTarget(value: ISolicitudRrhhTutor): ISolicitudRrhhTutorRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutorRequest;
    }
    return {
      tutorRef: value.tutorRef
    };
  }
}

export const SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER = new SolicitudRrhhTutorRequestConverter();
