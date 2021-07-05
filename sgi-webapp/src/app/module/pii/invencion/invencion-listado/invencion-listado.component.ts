import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SectorAplicacionService } from '@core/services/pii/sector-aplicacion/sector-aplicacion.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');
const MSG_BUTTON_NEW = marker('btn.add.entity');
const INVENCION_KEY = marker('pii.invencion');

@Component({
  selector: 'sgi-invencion-listado',
  templateUrl: './invencion-listado.component.html',
  styleUrls: ['./invencion-listado.component.scss']
})
export class InvencionListadoComponent extends AbstractTablePaginationComponent<IInvencion> implements OnInit {

  MSG_PARAMS = MSG_PARAMS;
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear: string;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  invencion$: Observable<IInvencion[]>;

  readonly sectoresAplicacion$: Observable<ISectorAplicacion[]>;
  readonly tiposProteccion$: Observable<ITipoProteccion[]>;

  get TipoColectivo() {
    return TipoColectivo;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly invencionService: InvencionService,
    private readonly translate: TranslateService,
    sectorAplicacionService: SectorAplicacionService,
    tipoProteccionService: TipoProteccionService,
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.sectoresAplicacion$ = sectorAplicacionService.findAll().pipe(map(({ items }) => items));
    this.tiposProteccion$ = tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      id: new FormControl(''),
      fechaComunicacionDesde: new FormControl(),
      fechaComunicacionHasta: new FormControl(),
      sectorAplicacion: new FormControl(null),
      tipoProteccion: new FormControl(null),
      inventor: new FormControl(''),
      titulo: new FormControl(''),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  protected createObservable(): Observable<SgiRestListResult<IInvencion>> {
    const observable$ = this.invencionService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['id', 'fechaComunicacion', 'titulo', 'tipoProteccion.nombre', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.invencion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, controls.id.value)
      .and('fechaComunicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaComunicacionDesde.value))
      .and('fechaComunicacion', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaComunicacionHasta.value?.plus({ hour: 23, minutes: 59, seconds: 59 })))
      .and('tipoProteccion.id', SgiRestFilterOperator.EQUALS, controls.tipoProteccion.value?.id?.toString())
      .and('sectoresAplicacion.sectorAplicacion.id', SgiRestFilterOperator.EQUALS, controls.sectorAplicacion.value?.id?.toString())
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('inventores.inventorRef', SgiRestFilterOperator.LIKE_ICASE, controls.inventor.value?.id?.toString());

    return filter;
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaComunicacionDesde.setValue(null);
    this.formGroup.controls.fechaComunicacionHasta.setValue(null);
    this.onSearch();
  }
}
