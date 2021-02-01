import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR = marker('eti.peticionEvaluacion.listado.error');
const MSG_FOOTER = marker('eti.peticionEvaluacion.listado.nuevaPeticionEvaluacion');
const MSG_DELETE = marker('eti.peticionEvaluacion.listado.eliminar');
const MSG_SUCCESS = marker('eti.peticionEvaluacion.listado.eliminarConfirmado');

@Component({
  selector: 'sgi-peticion-evaluacion-listado-inv',
  templateUrl: './peticion-evaluacion-listado-inv.component.html',
  styleUrls: ['./peticion-evaluacion-listado-inv.component.scss']
})
export class PeticionEvaluacionListadoInvComponent extends AbstractTablePaginationComponent<IPeticionEvaluacion> implements OnInit {


  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  textoCrear = MSG_FOOTER;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  peticionesEvaluacion$: Observable<IPeticionEvaluacion[]> = of();
  memorias$: Observable<IMemoria[]> = of();

  comiteListado: IComite[];
  filteredComites: Observable<IComite[]>;

  constructor(
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly dialogService: DialogService
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
  }


  protected initColumns(): void {
    this.displayedColumns = ['codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', [])
    });

    this.getComites();
  }

  protected createObservable(): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    const observable$ = this.peticionesEvaluacionService.findAllPeticionEvaluacionMemoria(this.getFindOptions());
    return observable$;
  }

  protected createFilters(): SgiRestFilter[] {
    this.filter = [];

    if (this.formGroup.controls.comite.value) {
      const filterComite: SgiRestFilter = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.formGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);
    }

    if (this.formGroup.controls.codigo.value) {
      const filterCodigo: SgiRestFilter = {
        field: 'peticionEvaluacion.codigo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.codigo.value,
      };

      this.filter.push(filterCodigo);
    }

    if (this.formGroup.controls.titulo.value) {
      const filterTitulo: SgiRestFilter = {
        field: 'peticionEvaluacion.titulo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.titulo.value,
      };

      this.filter.push(filterTitulo);
    }

    return this.filter;
  }

  protected loadTable(reset?: boolean) {
    this.peticionesEvaluacion$ = this.getObservableLoadTable(reset);
  }


  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    const comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;
        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });
    this.suscripciones.push(comitesSubscription);
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | IComite): IComite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Elimina la peticion de evaluación con el id recibido por parametro.
   * @param peticionEvaluacionId id de la petición de evaluación
   * @param event evento lanzado
   */
  borrar(peticionEvaluacionId: number, $event: Event): void {
    $event.stopPropagation();
    $event.preventDefault();

    const dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(MSG_DELETE).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          const peticionEvaluacionServiceDeleteSubscription = this.peticionesEvaluacionService
            .deleteById(peticionEvaluacionId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(MSG_SUCCESS);
            });
          this.suscripciones.push(peticionEvaluacionServiceDeleteSubscription);
        }
        aceptado = false;
      });
    this.suscripciones.push(dialogServiceSubscriptionGetSubscription);
  }

}
