import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { ActionService } from '@core/services/action-service';
import { ProyectoResumenService } from '@core/services/csp/proyecto-resumen/proyecto-resumen.service';
import { AutorService } from '@core/services/prc/autor/autor.service';
import { CampoProduccionCientificaService } from '@core/services/prc/campo-produccion-cientifica/campo-produccion-cientifica.service';
import { ConfiguracionCampoService } from '@core/services/prc/configuracion-campo/configuracion-campo.service';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { ValorCampoService } from '@core/services/prc/valor-campo/valor-campo.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { IProduccionCientificaData, PRODUCCION_CIENTIFICA_DATA_KEY } from '../shared/produccion-cientifica.resolver';
import { PublicacionDatosGeneralesFragment } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.fragment';

@Injectable()
export class PublicacionActionService extends ActionService {

  public readonly id: number;
  private data: IProduccionCientificaData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };
  private produccionCientifica$: BehaviorSubject<IProduccionCientifica>;
  private datosGenerales: PublicacionDatosGeneralesFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    route: ActivatedRoute,
    campoProduccionCientificaService: CampoProduccionCientificaService,
    configuracionCampo: ConfiguracionCampoService,
    valorCampoService: ValorCampoService,
    produccionCientificaService: ProduccionCientificaService,
    personaService: PersonaService,
    proyectoResumenService: ProyectoResumenService,
    documentoService: DocumentoService,
    autorService: AutorService,
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[PRODUCCION_CIENTIFICA_DATA_KEY];
      this.produccionCientifica$ = new BehaviorSubject(this.data.produccionCientifica);
      this.enableEdit();

      this.datosGenerales = new PublicacionDatosGeneralesFragment(
        this.data?.produccionCientifica?.id,
        this.getProduccionCientifica$(),
        campoProduccionCientificaService,
        configuracionCampo,
        valorCampoService,
        produccionCientificaService,
        personaService,
        proyectoResumenService,
        documentoService,
        autorService);
      this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    } else {
      this.produccionCientifica$ = new BehaviorSubject(undefined);
    }
  }

  getProduccionCientifica$(): Observable<IProduccionCientifica> {
    return this.produccionCientifica$.asObservable();
  }

  isProduccionCientificaEditable$(): Observable<boolean> {
    return this.getProduccionCientifica$().pipe(
      map(({ estado }) => estado?.estado === TipoEstadoProduccion.VALIDADO || estado?.estado === TipoEstadoProduccion.RECHAZADO)
    );
  }

  emitProduccionCientifica(produccionCientifica: IProduccionCientifica): void {
    this.produccionCientifica$.next(produccionCientifica);
  }
}
