import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhTutorResponse } from './solicitud-rrhh-tutor-response';

class SolicitudRrhhTutorResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhTutorResponse, ISolicitudRrhhTutor> {
  toTarget(value: ISolicitudRrhhTutorResponse): ISolicitudRrhhTutor {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutor;
    }
    return {
      tutorRef: value.tutorRef
    };
  }

  fromTarget(value: ISolicitudRrhhTutor): ISolicitudRrhhTutorResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhTutorResponse;
    }
    return {
      tutorRef: value.tutorRef
    };
  }
}

export const SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER = new SolicitudRrhhTutorResponseConverter();
