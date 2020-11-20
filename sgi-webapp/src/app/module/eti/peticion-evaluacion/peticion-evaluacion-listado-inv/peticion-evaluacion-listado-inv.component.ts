import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription, from } from 'rxjs';
import { catchError, map, startWith, switchMap, mergeMap } from 'rxjs/operators';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';

const MSG_ERROR = marker('eti.peticionEvaluacion.listado.error');
const MSG_FOOTER = marker('eti.peticionEvaluacion.listado.nuevaPeticionEvaluacion');
const LISTADO_ERROR = marker('eti.peticionEvaluacion.listado.error');
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
  comitesSubscription: Subscription;
  filteredComites: Observable<IComite[]>;


  dialogServiceSubscription: Subscription;
  dialogServiceSubscriptionGetSubscription: Subscription;
  peticionEvaluacionServiceDeleteSubscription: Subscription;
  memoriaServiceSubscription: Subscription;


  constructor(
    protected readonly logger: NGXLogger,
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly dialogService: DialogService,
    private readonly memoriaService: MemoriaService
  ) {

    super(logger, snackBarService, MSG_ERROR);

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
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'initColumns()', 'start');
    this.displayedColumns = ['codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'initColumns()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();


    this.formGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', [])
    });

    this.getComites();

    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    const observable$ = this.loadTableResponsable();

    return observable$;
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'start');
    this.filter = [];
    if (this.formGroup.controls.codigo.value) {
      this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'codigo');
      const filterCodigo: SgiRestFilter = {
        field: 'codigo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.codigo.value,
      };

      this.filter.push(filterCodigo);

    }

    if (this.formGroup.controls.titulo.value) {
      this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'titulo');
      const filterTitulo: SgiRestFilter = {
        field: 'titulo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.titulo.value,
      };

      this.filter.push(filterTitulo);

    }

    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'end');
    return this.filter;
  }



  protected loadTable(reset?: boolean) {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTable()', 'start');

    this.peticionesEvaluacion$ = this.getObservableLoadTable(reset);

    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTable()', 'end');
  }

  private loadTableResponsable(reset?: boolean): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTableResponsable()', 'start');
    let peticionesEvaluacion: IPeticionEvaluacion[] = new Array();
    // Do the request with paginator/sort/filter values
    return this.memoriaService
      .findAllByPersonaRef({
        filters: this.buildFiltersMemoria()
      }).pipe(
        switchMap(
          (response) => {
            const memorias: IMemoria[] = response.items;
            memorias.forEach(memoria => {
              if (!peticionesEvaluacion.some((current) => current.id === memoria.peticionEvaluacion.id)) {
                peticionesEvaluacion.push(memoria.peticionEvaluacion);
              }
            });

            // si el usuario tiene asignada una memoria quiere decir que es responsable de memoria,
            // se cargarán las peticiones de evaluación relacionadas a la memoria
            if (memorias.length > 0) {
              let peticionesListado: SgiRestListResult<IPeticionEvaluacion>;
              return of(peticionesListado = {
                page: response.page,
                total: response.total,
                items: peticionesEvaluacion
              });
              // si el usuario no tiene asignada una memoria cargaremos las peticiones de evaluación creadas por él mismo
            } else {
              return this.loadTableCreador();
            }
          }),
        catchError(() => {
          this.snackBarService.showError(MSG_ERROR);
          this.logger.debug(
            PeticionEvaluacionListadoInvComponent.name,
            'loadTableResponsable()',
            'end'
          );
          return of(null);

        })
      );


  }

  private loadTableCreador(reset?: boolean): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTableCreador()', 'start');
    // Do the request with paginator/sort/filter values
    return this.peticionesEvaluacionService
      .findAllByPersonaRef({
        page: {
          index: reset ? 0 : this.paginator.pageIndex,
          size: this.paginator.pageSize
        },
        sort: {
          direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
          field: this.sort.active
        },
        filters: this.createFilters()
      })
      .pipe(
        switchMap((response) => {
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTableCreador()', 'end');

          if (this.formGroup.controls.comite.value) {
            return this.filterPeticionEvaluacionByComite(response.items, this.formGroup.controls.comite.value);
          }

          // Return the values
          return of(response);
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.snackBarService.showError(LISTADO_ERROR);
          this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTableCreador()', 'end');
          return [];
        })
      );

  }

  private filterPeticionEvaluacionByComite(
    peticionesEvaluacion: IPeticionEvaluacion[], comite: string): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    const peticionesEvaluacionByComiteExists: IPeticionEvaluacion[] = new Array();
    let data: SgiRestListResult<IMemoriaPeticionEvaluacion>;
    let page;

    return from(peticionesEvaluacion).pipe(
      mergeMap((peticionEvaluacion) => {

        return this.memoriaService
          .findAll({
            filters: this.buildFiltersMemoria(peticionEvaluacion.id)
          })
          .pipe(
            map((response) => {
              const memorias: IMemoria[] = response.items;
              data = response;

              page = response.page;

              if (memorias.length > 0) {
                if (peticionesEvaluacionByComiteExists.indexOf(peticionEvaluacion) === -1) {
                  peticionesEvaluacionByComiteExists.push(peticionEvaluacion);
                }
              }


            }),
            catchError(() => {
              // On error reset pagination values
              this.paginator.firstPage();
              this.snackBarService.showError(LISTADO_ERROR);
              this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'loadTableCreador()', 'end');
              return [];
            }
            )
          );
      }),
      map(() => {
        let peticionesListadoComite: SgiRestListResult<IPeticionEvaluacion>;
        return (peticionesListadoComite = {
          page: page,
          total: peticionesEvaluacionByComiteExists.length,
          items: peticionesEvaluacion
        });
      })
    );




  }

  private buildFiltersMemoria(idPeticionEvaluacion?: number): SgiRestFilter[] {
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFiltersMemoria()', 'start');

    this.filter = [];
    if (this.formGroup.controls.comite.value) {
      this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFiltersMemoria()', 'comite');
      const filterComite: SgiRestFilter = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.formGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);

    }

    if (this.formGroup.controls.codigo.value) {
      this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'codigo');
      const filterCodigo: SgiRestFilter = {
        field: 'peticionEvaluacion.codigo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.codigo.value,
      };

      this.filter.push(filterCodigo);

    }

    if (this.formGroup.controls.titulo.value) {
      this.logger.debug(PeticionEvaluacionListadoInvComponent.name, 'buildFilters()', 'titulo');
      const filterTitulo: SgiRestFilter = {
        field: 'peticionEvaluacion.titulo',
        type: SgiRestFilterType.LIKE,
        value: this.formGroup.controls.titulo.value,
      };

      this.filter.push(filterTitulo);

    }

    if (idPeticionEvaluacion) {
      const filterPeticionEvaluacion: SgiRestFilter = {
        field: 'peticionEvaluacion.id',
        type: SgiRestFilterType.EQUALS,
        value: idPeticionEvaluacion.toString(),
      };
      this.filter.push(filterPeticionEvaluacion);
    }

    return this.filter;
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
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name,
      'getComites()',
      'start');

    this.comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });

    this.logger.debug(PeticionEvaluacionListadoInvComponent.name,
      'getComites()',
      'end');
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
    this.logger.debug(PeticionEvaluacionListadoInvComponent.name,
      'borrar(peticionEvaluacionId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(MSG_DELETE).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.peticionEvaluacionServiceDeleteSubscription = this.peticionesEvaluacionService
            .deleteById(peticionEvaluacionId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(MSG_SUCCESS);
            });
        }
        aceptado = false;
      });

    this.logger.debug(PeticionEvaluacionListadoInvComponent.name,
      'borrar(peticionEvaluacionId: number, $event: Event) - end');
  }





}
