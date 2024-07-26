import { Component, Inject, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { IGrupoTipo, TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { CKEDITOR_CONFIG, CkEditorConfig } from '@shared/sgi-ckeditor-config';
import Editor from 'ckeditor5-custom-build/build/ckeditor';
import { Subscription } from 'rxjs';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoDatosGeneralesFragment } from './grupo-datos-generales.fragment';

const GRUPO_NOMBRE_KEY = marker('csp.grupo.nombre');
const GRUPO_INVESTIGADOR_PRINCIPAL_KEY = marker('csp.grupo.investigador-principal');
const GRUPO_CODIGO_KEY = marker('csp.grupo.codigo');
const GRUPO_FECHA_INICIO_KEY = marker('label.fecha-inicio');
const GRUPO_ESPECIAL_INVESTIGACION_KEY = marker('csp.grupo.especial-investigacion');
const GRUPO_RESUMEN_KEY = marker('csp.grupo.resumen');

@Component({
  selector: 'sgi-grupo-datos-generales',
  templateUrl: './grupo-datos-generales.component.html',
  styleUrls: ['./grupo-datos-generales.component.scss']
})
export class GrupoDatosGeneralesComponent extends FormFragmentComponent<IGrupo> implements OnInit {
  public readonly CkEditor = Editor;

  formPart: GrupoDatosGeneralesFragment;

  colectivosBusqueda: string[];

  private subscriptions = [] as Subscription[];

  msgParamTituloEntity = {};
  msgParamNombreEntity = {};
  msgParamInvestigadorPrincipalEntity = {};
  msgParamCodigoEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamEspecialInvestigacionEntity = {};
  msgParamResumenEntity = {};

  tiposGrupo = new MatTableDataSource<IGrupoTipo>();
  columnsTipo = ['tipo', 'fechaInicioTipo', 'fechaFinTipo'];

  especialesInvestigacionGrupo = new MatTableDataSource<IGrupoEspecialInvestigacion>();
  columnsEspecialInvestigacion = ['especialInvestigacion', 'fechaInicio', 'fechaFin'];

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  get grupo(): IGrupo {
    return this.formPart.getValue();
  }

  constructor(
    protected actionService: GrupoActionService,
    public authService: SgiAuthService,
    private rolProyectoColectivoService: RolProyectoColectivoService,
    private readonly translate: TranslateService,
    @Inject(CKEDITOR_CONFIG) public readonly configCkEditor: CkEditorConfig
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService, translate);
    this.formPart = this.fragment as GrupoDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.loadColectivosBusqueda();
    this.loadHistoricoTipos();
    this.loadHistoricoEspecialesInvestigacion();
  }

  private loadColectivosBusqueda(): void {
    this.subscriptions.push(
      this.rolProyectoColectivoService.findColectivosActivos().subscribe(colectivos => {
        this.colectivosBusqueda = colectivos
      })
    );
  }

  protected setupI18N(): void {

    this.translate.get(
      GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_INVESTIGADOR_PRINCIPAL_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamInvestigadorPrincipalEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_CODIGO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamCodigoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_ESPECIAL_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEspecialInvestigacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }

  private loadHistoricoTipos() {
    this.subscriptions.push(this.formPart.tipos$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.tiposGrupo.data = [];
      } else {
        this.tiposGrupo.data = data;
      }
    }
    ));
  }

  private loadHistoricoEspecialesInvestigacion() {
    this.subscriptions.push(this.formPart.especialesInvestigacion$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.especialesInvestigacionGrupo.data = [];
      } else {
        this.especialesInvestigacionGrupo.data = data;
      }
    }
    ));
  }
}
