import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGrupo } from '@core/models/csp/grupo';
import { Module } from '@core/module';
import { ActionService } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { GrupoDescriptorService } from '@core/services/csp/grupo-descriptor/grupo-descriptor.service';
import { GrupoEnlaceService } from '@core/services/csp/grupo-enlace/grupo-enlace.service';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoPersonaAutorizadaService } from '@core/services/csp/grupo-persona-autorizada/grupo-persona-autorizada.service';
import { GrupoRelacionInstitucionalService } from '@core/services/csp/grupo-relacion-institucional/grupo-relacion-institucional.service';
import { GrupoResponsableEconomicoService } from '@core/services/csp/grupo-responsable-economico/grupo-responsable-economico.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { NEVER, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { GRUPO_DATA_KEY } from './grupo-data.resolver';
import { GrupoDatosGeneralesFragment } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.fragment';
import { GrupoDescriptorFragment } from './grupo-formulario/grupo-descriptor/grupo-descriptor.fragment';
import { GrupoEnlaceFragment } from './grupo-formulario/grupo-enlace/grupo-enlace.fragment';
import { GrupoEquipoInstrumentalFragment } from './grupo-formulario/grupo-equipo-instrumental/grupo-equipo-instrumental.fragment';
import { GrupoEquipoInvestigacionFragment } from './grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.fragment';
import { GrupoLineaInvestigacionFragment } from './grupo-formulario/grupo-linea-investigacion-listado/grupo-linea-investigacion.fragment';
import { GrupoPersonaAutorizadaFragment } from './grupo-formulario/grupo-persona-autorizada/grupo-persona-autorizada.fragment';
import { GrupoRelacionInstitucionalFragment } from './grupo-formulario/grupo-relacion-institucional/grupo-relacion-institucional.fragment';
import { GrupoResponsableEconomicoFragment } from './grupo-formulario/grupo-responsable-economico/grupo-responsable-economico.fragment';
import { GRUPO_ROUTE_PARAMS } from './grupo-route-params';

export interface IGrupoData {
  grupo: IGrupo;
  isInvestigador: boolean;
  isEjecucionEconomicaGruposEnabled: boolean;
  isGrupoEspecialInvestigacion: boolean;
  readonly: boolean;
}

const MSG_CREATE_GRUPO_ESPECIAL = marker('msg.csp.grupo.create.grupo-especial');
const MSG_CREATE_GRUPO_NO_ESPECIAL = marker('msg.csp.grupo.create.grupo-no-especial');

@Injectable()
export class GrupoActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    EQUIPO_INVESTIGACION: 'equipo-investigacion',
    RESPONSABLE_ECONOMICO: 'responsable-economico',
    EQUIPO_INSTRUMENTAL: 'equipo-instrumental',
    ENLACE: 'enlace',
    PERSONA_AUTORIZADA: 'persona-autorizada',
    LINEA_INVESTIGACION: 'linea-investigacion',
    RELACION_INSTITUCIONAL: 'relacion-institucional',
    DESCRIPTOR: 'descriptor'
  };

  private datosGenerales: GrupoDatosGeneralesFragment;
  private equiposInvestigacion: GrupoEquipoInvestigacionFragment;
  private responsablesEconomicos: GrupoResponsableEconomicoFragment;
  private equiposInstrumentales: GrupoEquipoInstrumentalFragment;
  private enlaces: GrupoEnlaceFragment;
  private personasAutorizadas: GrupoPersonaAutorizadaFragment;
  private lineasInvestigacion: GrupoLineaInvestigacionFragment;
  private relacionesInstitucionales: GrupoRelacionInstitucionalFragment;
  private descriptores: GrupoDescriptorFragment;

  private readonly data: IGrupoData;
  public readonly id: number;

  get isInvestigador(): boolean {
    return this.data.isInvestigador ?? (this.isModuleINV() && this.hasAnyAuthorityInv());
  }

  get grupo(): IGrupo {
    return this.datosGenerales.getValue();
  }

  get readonly(): boolean {
    return this.data?.readonly;
  }

  constructor(
    logger: NGXLogger,
    private route: ActivatedRoute,
    private dialogService: DialogService,
    private translate: TranslateService,
    private readonly configuracionService: ConfigService,
    private readonly documentoService: DocumentoService,
    private readonly empresaService: EmpresaService,
    private readonly grupoDescriptorService: GrupoDescriptorService,
    private readonly grupoEnlaceService: GrupoEnlaceService,
    private readonly grupoEquipoInstrumentalService: GrupoEquipoInstrumentalService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private readonly grupoPersonaAutorizadaService: GrupoPersonaAutorizadaService,
    private readonly grupoRelacionInstitucionalService: GrupoRelacionInstitucionalService,
    private readonly grupoResponsableEconomicoService: GrupoResponsableEconomicoService,
    private readonly grupoService: GrupoService,
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    private readonly palabraClaveService: PalabraClaveService,
    private readonly personaService: PersonaService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly rolProyectoService: RolProyectoService,
    private readonly sgiAuthService: SgiAuthService,
    private readonly tipoEnlaceService: TipoEnlaceService,
    private readonly tipoGrupoDescriptorService: TipoDescriptorGrupoService,
    private readonly vinculacionService: VinculacionService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[GRUPO_DATA_KEY];
    }

    this.datosGenerales = new GrupoDatosGeneralesFragment(
      logger,
      this.id,
      configuracionService,
      documentoService,
      grupoService,
      grupoEquipoService,
      palabraClaveService,
      proyectoSgeService,
      rolProyectoService,
      vinculacionService,
      this.data?.isEjecucionEconomicaGruposEnabled,
      this.data?.readonly
    );
    this.equiposInvestigacion = new GrupoEquipoInvestigacionFragment(
      logger,
      this.id,
      grupoService,
      grupoEquipoService,
      personaService,
      vinculacionService,
      this.data?.readonly,
      configuracionService,
      this.data?.isGrupoEspecialInvestigacion
    );

    this.responsablesEconomicos = new GrupoResponsableEconomicoFragment(
      logger,
      this.id,
      grupoService,
      grupoResponsableEconomicoService,
      personaService,
      this.data?.readonly
    );

    this.equiposInstrumentales = new GrupoEquipoInstrumentalFragment(
      logger,
      this.id,
      grupoService,
      grupoEquipoInstrumentalService,
      this.data?.readonly
    );

    this.enlaces = new GrupoEnlaceFragment(
      this.id,
      grupoService,
      grupoEnlaceService,
      tipoEnlaceService,
      this.data?.readonly
    );

    this.personasAutorizadas = new GrupoPersonaAutorizadaFragment(
      logger,
      this.id,
      grupoService,
      grupoPersonaAutorizadaService,
      personaService,
      this.data?.readonly
    );

    this.lineasInvestigacion = new GrupoLineaInvestigacionFragment(
      logger,
      this.id,
      grupoService,
      grupoLineaInvestigacionService,
      lineaInvestigacionService,
      this.data?.readonly
    );

    this.relacionesInstitucionales = new GrupoRelacionInstitucionalFragment(
      this.id,
      grupoService,
      grupoRelacionInstitucionalService,
      empresaService,
      this.data?.readonly
    );

    this.descriptores = new GrupoDescriptorFragment(
      this.id,
      grupoService,
      grupoDescriptorService,
      tipoGrupoDescriptorService,
      this.data?.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO_INVESTIGACION, this.equiposInvestigacion);
    this.addFragment(this.FRAGMENT.RESPONSABLE_ECONOMICO, this.responsablesEconomicos);
    this.addFragment(this.FRAGMENT.EQUIPO_INSTRUMENTAL, this.equiposInstrumentales);
    this.addFragment(this.FRAGMENT.ENLACE, this.enlaces);
    this.addFragment(this.FRAGMENT.PERSONA_AUTORIZADA, this.personasAutorizadas);
    this.addFragment(this.FRAGMENT.LINEA_INVESTIGACION, this.lineasInvestigacion);
    this.addFragment(this.FRAGMENT.RELACION_INSTITUCIONAL, this.relacionesInstitucionales);
    this.addFragment(this.FRAGMENT.DESCRIPTOR, this.descriptores);

    this.datosGenerales.initialize();

    if (this.isEdit()) {
      this.equiposInvestigacion.initialize();
    }

    this.subscriptions.push(
      this.equiposInvestigacion.equipos$
        .pipe(
          map(equipoInvestigacionWrapped => equipoInvestigacionWrapped.map(({ value }) => value))
        ).subscribe(equipoInvestigacion => this.datosGenerales.equipoInvestigacion$.next(equipoInvestigacion))
    );
  }

  saveOrUpdate(): Observable<void> {
    if (this.isEdit()) {
      return super.saveOrUpdate();
    } else {
      const msgConfirm = this.grupo.especialInvestigacion ? MSG_CREATE_GRUPO_ESPECIAL : MSG_CREATE_GRUPO_NO_ESPECIAL;
      return this.dialogService.showConfirmation(this.translate.instant(msgConfirm)).pipe(
        switchMap((aceptado) => {
          if (aceptado) {
            return super.saveOrUpdate();
          }

          return NEVER;
        })
      );
    }
  }

  private isModuleINV(): boolean {
    return this.route.snapshot.data.module === Module.INV;
  }

  private hasAnyAuthorityInv(): boolean {
    return this.sgiAuthService.hasAuthority('CSP-GIN-INV-VR');
  }

}
