import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

const MSG_LISTADO_ERROR = marker('eti.evaluador.listado.error');

@Component({
  templateUrl: './buscar-persona-dialogo.component.html',
  styleUrls: ['./buscar-persona-dialogo.component.scss']
})
export class BuscarPersonaDialogoComponent implements AfterViewInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  personas$: Observable<IPersona[]> = of();

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<BuscarPersonaDialogoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IPersona,
    private personaFisicaService: PersonaFisicaService,
    private snackBarService: SnackBarService) {

    this.displayedColumns = ['nombre', 'primerApellido', 'segundoApellido', 'numIdentificadorPersonal'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;

    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];

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

  onNoClick(): void {
    this.dialogRef.close();
  }

  buscarPersona(reset?: boolean) {
    this.personas$ = this.personaFisicaService
      .findAllPersonas(
        {
          page: {
            index: reset ? 0 : this.paginator.pageIndex,
            size: this.paginator.pageSize
          },
          sort: {
            direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
            field: 'personaRef'
          },
          filters: this.buildFilters(this.data)
        }
      )
      .pipe(
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          // Return the values
          return response.items;
        }),
        catchError((error) => {
          this.logger.error(error);
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_LISTADO_ERROR);
          return of([]);
        })
      );
  }

  private buildFilters(persona: IPersona): SgiRestFilter[] {
    this.filter = [];
    if (persona.nombre) {
      const filterNombre: SgiRestFilter = {
        field: 'nombre',
        type: SgiRestFilterType.EQUALS,
        value: persona.nombre,
      };
      this.filter.push(filterNombre);
    }
    if (persona.primerApellido) {
      const filterPrimerApellido: SgiRestFilter = {
        field: 'primerApellido',
        type: SgiRestFilterType.EQUALS,
        value: persona.primerApellido,
      };
      this.filter.push(filterPrimerApellido);
    }
    if (persona.segundoApellido) {
      const filterSegundoApellido: SgiRestFilter = {
        field: 'segundoApellido',
        type: SgiRestFilterType.EQUALS,
        value: persona.segundoApellido,
      };
      this.filter.push(filterSegundoApellido);
    }
    if (persona.identificadorNumero) {
      const filterNumIdentificadorPersonal: SgiRestFilter = {
        field: 'identificadorNumero',
        type: SgiRestFilterType.EQUALS,
        value: persona.identificadorNumero,
      };
      this.filter.push(filterNumIdentificadorPersonal);
    }
    if (persona.identificadorLetra) {
      const filterLetraIdentificadorPersonal: SgiRestFilter = {
        field: 'identificadorLetra',
        type: SgiRestFilterType.EQUALS,
        value: persona.identificadorLetra,
      };
      this.filter.push(filterLetraIdentificadorPersonal);
    }
    return this.filter;
  }

  ngAfterViewInit(): void {
    // Merge events that trigger load table data
    merge(
      // Link pageChange event to fire new request
      this.paginator.page,
      // Link sortChange event to fire new request
      this.sort.sortChange
    )
      .pipe(
        tap(() => {
          // Load table
          this.buscarPersona();
        })
      )
      .subscribe();
    // First load
    // this.buscarPersona();
  }

}

