import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IInvencion } from '@core/models/pii/invencion';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestListResult, SgiRestFilter } from '@sgi/framework/http';
import { Observable } from 'rxjs';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-invencion-listado',
  templateUrl: './invencion-listado.component.html',
  styleUrls: ['./invencion-listado.component.scss']
})
export class InvencionListadoComponent extends AbstractTablePaginationComponent<IInvencion> implements OnInit {

  invencion$: Observable<IInvencion[]>;

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly invencionService: InvencionService,
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }


  protected createObservable(): Observable<SgiRestListResult<IInvencion>> {
    const observable$ = this.invencionService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    let columns = ['id', 'fechaComunicacion', 'titulo', 'activo', 'acciones'];

    this.columnas = columns;
  }

  protected loadTable(reset?: boolean): void {
    this.invencion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }
}
