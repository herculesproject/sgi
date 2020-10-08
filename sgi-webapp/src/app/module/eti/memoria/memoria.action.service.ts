import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { MemoriaDatosGeneralesFragment } from './memoria-formulario/memoria-datos-generales/memoria-datos-generales.fragment';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { IMemoria } from '@core/models/eti/memoria';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { map } from 'rxjs/operators';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { PETICION_EVALUACION_ROUTE } from '../peticion-evaluacion/peticion-evaluacion-route-names';
import { MemoriaDocumentacionFragment } from './memoria-formulario/memoria-documentacion/memoria-documentacion.fragment';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { TipoDocumentoService } from '@core/services/eti/tipo-documento.service';
import { DocumentacionMemoriaService } from '@core/services/eti/documentacion-memoria.service';
import { NGXLogger } from 'ngx-logger';
import { MemoriaEvaluacionesFragment } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.fragment';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';


const MSG_PETICIONES_EVALUACION = marker('eti.memoria.link.peticionEvaluacion');


@Injectable()
export class MemoriaActionService extends ActionService {

  public estadoActualMemoria: TipoEstadoMemoria;

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    DOCUMENTACION: 'documentacion',
    EVALUACIONES: 'evaluaciones'
  };

  private memoria: IMemoria;
  private datosGenerales: MemoriaDatosGeneralesFragment;
  public readonly: boolean;
  private documentacion: MemoriaDocumentacionFragment;
  private evaluaciones: MemoriaEvaluacionesFragment;

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: MemoriaService,
    private peticionEvaluacionService: PeticionEvaluacionService,
    personaFisicaService: PersonaFisicaService,
    private tipoDocumentoService: TipoDocumentoService,
    protected readonly logger: NGXLogger) {
    super();
    this.memoria = {} as IMemoria;
    if (route.snapshot.data.memoria) {
      this.memoria = route.snapshot.data.memoria;
      this.enableEdit();
      this.addPeticionEvaluacionLink(this.memoria.peticionEvaluacion.id);
      this.readonly = route.snapshot.data.readonly;
      this.estadoActualMemoria = this.memoria.estadoActual;
    }
    else {
      this.loadPeticionEvaluacion(history.state.idPeticionEvaluacion);
    }
    this.datosGenerales = new MemoriaDatosGeneralesFragment(fb, this.readonly, this.memoria?.id, service, personaFisicaService);
    this.documentacion = new MemoriaDocumentacionFragment(logger, this.memoria?.id, service, tipoDocumentoService);
    this.evaluaciones = new MemoriaEvaluacionesFragment(logger, this.memoria?.id, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);
    this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
  }

  private addPeticionEvaluacionLink(idPeticionEvaluacion: number): void {
    this.addActionLink({
      title: MSG_PETICIONES_EVALUACION,
      routerLink: ['../..', PETICION_EVALUACION_ROUTE, idPeticionEvaluacion.toString()]
    });
  }

  private loadPeticionEvaluacion(id: number): void {
    if (id) {
      this.peticionEvaluacionService.findById(id).pipe(
        map((peticionEvaluacion) => {
          this.memoria.peticionEvaluacion = peticionEvaluacion;
          this.addPeticionEvaluacionLink(id);
        })
      ).subscribe();
    }
  }
}
