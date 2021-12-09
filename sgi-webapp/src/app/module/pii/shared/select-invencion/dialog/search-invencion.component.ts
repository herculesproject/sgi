import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { merge, Observable, Subject, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface SearchInvencionModalData extends SearchModalData {

}

@Component({
  selector: 'sgi-search-invencion',
  templateUrl: './search-invencion.component.html',
  styleUrls: ['./search-invencion.component.scss']
})
export class SearchInvencionModalComponent implements OnInit, AfterViewInit, OnDestroy {
  formGroup: FormGroup;
  displayedColumns = ['id', 'fechaComunicacion', 'titulo', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  private subscriptions: Subscription[] = [];

  readonly invenciones$ = new Subject<IInvencion[]>();
  readonly tiposProteccion$: Observable<ITipoProteccion[]>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public dialogRef: MatDialogRef<SearchInvencionModalComponent, IInvencion>,
    @Inject(MAT_DIALOG_DATA) public data: SearchInvencionModalData,
    private readonly invencionService: InvencionService,
    readonly tipoProteccionService: TipoProteccionService
  ) {
    this.tiposProteccion$ = tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      id: new FormControl(),
      fechaComunicacionDesde: new FormControl(),
      fechaComunicacionHasta: new FormControl(),
      titulo: new FormControl(this.data.searchTerm),
      tipoProteccion: new FormControl(),
    });
  }

  ngAfterViewInit(): void {
    this.search();

    this.subscriptions.push(
      merge(
        this.paginator.page,
        this.sort.sortChange
      ).pipe(
        tap(() => {
          this.search();
        })
      ).subscribe()
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  closeModal(invencion?: IInvencion): void {
    this.dialogRef.close(invencion);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.formGroup.reset();
    this.search(true);
  }

  search(reset?: boolean) {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator.pageIndex,
        size: this.paginator.pageSize
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.buildFilter()
    };

    this.invencionService.findAll(options).subscribe(result => {
      this.totalElementos = result.total;
      this.invenciones$.next(result.items);
    });
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, controls.id.value)
      .and('fechaComunicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaComunicacionDesde.value))
      .and('fechaComunicacion', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaComunicacionHasta.value?.plus({ hour: 23, minutes: 59, seconds: 59 })))
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('tipoProteccion', SgiRestFilterOperator.EQUALS, controls.tipoProteccion.value?.id?.toString());;
  }
}
