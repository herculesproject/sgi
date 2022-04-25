import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-evaluacion-listado',
  templateUrl: './evaluacion-listado.component.html',
  styleUrls: ['./evaluacion-listado.component.scss']
})
export class EvaluacionListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluaciones$: Observable<IEvaluacion[]> = of();

  buscadorFormGrou: FormGroup;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  constructor(
    private readonly evaluacionesService: EvaluacionService,
    protected readonly snackBarService: SnackBarService,
    protected readonly personaService: PersonaService

  ) {

    super(snackBarService, MSG_ERROR);

    this.totalElementos = 0;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(13%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.suscripciones = [];
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      referenciaMemoria: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl(null, []),
      solicitante: new FormControl('', []),
      tipoEvaluacion: new FormControl(null, [])
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    const observable$ = this.evaluacionesService.findAllByMemoriaAndRetrospectivaEnEvaluacion(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.displayedColumns = ['memoria.comite.comite', 'tipoEvaluacion', 'fechaDictamen', 'memoria.numReferencia', 'solicitante',
      'dictamen.nombre', 'version', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString())
      .and('fechaDictamen', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value))
      .and('fechaDictamen', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionFin.value))
      .and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.referenciaMemoria.value)
      .and(
        'convocatoriaReunion.tipoConvocatoriaReunion.id',
        SgiRestFilterOperator.EQUALS,
        controls.tipoConvocatoriaReunion.value?.id?.toString()
      ).and('memoria.peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);

    return filter;
  }

  protected loadTable(reset?: boolean) {
    this.evaluaciones$ = this.getObservableLoadTable(reset).pipe(
      switchMap((evaluaciones) => {
        return from(evaluaciones).pipe(
          mergeMap(evaluacion => {
            const personaId = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
            if (personaId) {
              return this.personaService.findById(personaId).pipe(
                map(persona => {
                  evaluacion.memoria.peticionEvaluacion.solicitante = persona;
                  return evaluacion;
                })
              );
            }
            return of(evaluacion);
          }),
          map(() => evaluaciones)
        );
      })
    );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

}
