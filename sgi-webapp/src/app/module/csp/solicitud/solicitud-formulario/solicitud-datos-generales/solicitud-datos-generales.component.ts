import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { SolicitudDatosGeneralesFragment, SolicitudModalidadEntidadConvocanteListado } from './solicitud-datos-generales.fragment';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SolicitudActionService } from '../../solicitud.action.service';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { Subscription, Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { TipoFormularioSolicitud } from '@core/enums/tipo-formulario-solicitud';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { SolicitudModalidadEntidadConvocanteModalData, SolicitudModalidadEntidadConvocanteModalComponent } from '../../modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';
import { MatDialog } from '@angular/material/dialog';

const MSG_ERROR_INIT = marker('csp.solicitud.datosGenerales.error.cargar');
const LABEL_BUSCADOR_SOLICITANTES = marker('csp.solicitud.datosGenerales.solicitante');

@Component({
  selector: 'sgi-solicitud-datos-generales',
  templateUrl: './solicitud-datos-generales.component.html',
  styleUrls: ['./solicitud-datos-generales.component.scss']
})
export class SolicitudDatosGeneralesComponent extends FormFragmentComponent<ISolicitud> implements OnInit, OnDestroy {

  labelBuscadorSolicitantes = LABEL_BUSCADOR_SOLICITANTES;

  formPart: SolicitudDatosGeneralesFragment;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  tiposFormulario: TipoFormularioSolicitud[];

  private unidadesGestion = [] as IUnidadGestion[];
  unidadesGestionFiltered$: Observable<IUnidadGestion[]>;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSourceEntidadesConvocantes: MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions = [] as Subscription[];

  constructor(
    protected readonly logger: NGXLogger,
    protected actionService: SolicitudActionService,
    private readonly snackBarService: SnackBarService,
    private unidadGestionService: UnidadGestionService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as SolicitudDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesEntidad = new FxFlexProperties();
    this.fxFlexPropertiesEntidad.sm = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.md = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.gtMd = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.order = '3';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['entidadConvocante', 'plan', 'programaConvocatoria', 'modalidadSolicitud', 'acciones'];

    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadUnidadesGestion();
    this.loadTiposFormulario();

    this.dataSourceEntidadesConvocantes = new MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>();
    this.dataSourceEntidadesConvocantes.paginator = this.paginator;
    this.dataSourceEntidadesConvocantes.sortingDataAccessor =
      (entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado, property: string) => {
        switch (property) {
          case 'entidadConvocante':
            return entidadConvocanteModalidad.entidadConvocante.entidad.razonSocial;
          case 'plan':
            return entidadConvocanteModalidad.plan.nombre;
          case 'programaConvocatoria':
            return entidadConvocanteModalidad.entidadConvocante.programa.nombre;
          case 'modalidadSolicitud':
            return entidadConvocanteModalidad.modalidad?.value.programa?.nombre;
          default:
            return entidadConvocanteModalidad[property];
        }
      };
    this.sort.sort(({ id: 'entidadConvocante', start: 'asc' }) as MatSortable);
    this.dataSourceEntidadesConvocantes.sort = this.sort;

    this.subscriptions.push(this.formPart.entidadesConvocantesModalidad$.subscribe(elements => {
      this.dataSourceEntidadesConvocantes.data = elements;
    }));

    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Devuelve el nombre de una gestión unidad.
   *
   * @param unidadGestion gestión unidad.
   * @returns nombre de una gestión unidad.
   */
  getUnidadGestion(unidadGestion?: IUnidadGestion): string | undefined {
    return typeof unidadGestion === 'string' ? unidadGestion : unidadGestion?.nombre;
  }

  /**
   * Apertura de modal de modadidad solicitud
   *
   * @param entidadConvocanteModalidad EntidadConvocanteModalidad que se carga en el modal para modificarlo.
   */
  openModalSelectModalidad(entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado): void {
    this.logger.debug(SolicitudDatosGeneralesComponent.name, `openModalSelectModalidad(${entidadConvocanteModalidad})`, 'start');

    const data: SolicitudModalidadEntidadConvocanteModalData = {
      entidad: entidadConvocanteModalidad.entidadConvocante.entidad,
      plan: entidadConvocanteModalidad.plan,
      programa: entidadConvocanteModalidad.entidadConvocante.programa,
      modalidad: entidadConvocanteModalidad.modalidad?.value
    };

    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(SolicitudModalidadEntidadConvocanteModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadConvocanteModalidadModal: SolicitudModalidadEntidadConvocanteModalData) => {

        if (!entidadConvocanteModalidadModal) {
          this.logger.debug(SolicitudDatosGeneralesComponent.name,
            `openModalSelectModalidad(${entidadConvocanteModalidadModal})`, 'end');
          return;
        }

        if (!entidadConvocanteModalidad.modalidad) {
          this.formPart.addSolicitudModalidad(entidadConvocanteModalidadModal.modalidad);
        } else if (!entidadConvocanteModalidadModal.modalidad) {
          this.formPart.deleteSolicitudModalidad(entidadConvocanteModalidad.modalidad);
        } else if (!entidadConvocanteModalidad.modalidad.created) {
          this.formPart.updateSolicitudModalidad(entidadConvocanteModalidadModal.modalidad);
        }

        this.logger.debug(SolicitudDatosGeneralesComponent.name, `openModalSelectModalidad(${entidadConvocanteModalidadModal})`, 'end');
      }
    );

  }

  /**
   * Carga los tipos de justificacion del enum TipoFormularioSolicitud
   */
  private loadTiposFormulario() {
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'loadTiposFormulario()', 'start');
    this.tiposFormulario = Object.keys(TipoFormularioSolicitud).map(key => TipoFormularioSolicitud[key]);
    this.logger.debug(SolicitudDatosGeneralesComponent.name, 'loadTiposFormulario()', 'end');
  }

  /**
   * Carga la lista de unidades de gestion seleccionables por el usuario
   */
  private loadUnidadesGestion() {
    this.logger.debug(SolicitudDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'start');
    this.subscriptions.push(
      this.unidadGestionService.findAllRestringidos().subscribe(
        res => {
          this.unidadesGestion = res.items;
          this.unidadesGestionFiltered$ = this.formGroup.controls.unidadGestion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroUnidadGestion(value))
            );
          this.logger.debug(SolicitudDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(SolicitudDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'error');
        }
      )
    );
  }

  /**
   * Filtra la lista por el value
   *
   * @param value del input para autocompletar
   * @returns Lista filtrada
   */
  private filtroUnidadGestion(value: string | IUnidadGestion): IUnidadGestion[] {
    // Si no es un string no se filtra
    if (typeof value !== 'string') {
      return this.unidadesGestion;
    }

    const filterValue = value.toString().toLowerCase();
    return this.unidadesGestion.filter(unidadGestion => unidadGestion.nombre.toLowerCase().includes(filterValue));
  }




}
