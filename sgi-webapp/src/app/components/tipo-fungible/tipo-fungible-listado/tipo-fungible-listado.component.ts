import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { TipoFungible } from '@core/models/tipo-fungible';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { TipoFungibleService } from '@core/services/tipo-fungible.service';
import { NGXLogger } from 'ngx-logger';
import { UrlUtils } from '@core/utils/url-utils';


@Component({
  selector: 'app-tipo-fungible-listado',
  templateUrl: './tipo-fungible-listado.component.html',
  styleUrls: ['./tipo-fungible-listado.component.scss']
})
export class TipoFungibleListadoComponent implements OnInit, OnDestroy {
  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['nombre', 'servicio', 'acciones'];
  dataSource: MatTableDataSource<TipoFungible>;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private logger: NGXLogger,
    private tipoFungibleService: TipoFungibleService) { }

  ngOnInit(): void {
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<TipoFungible>([]);

    this.tipoFungibleService.findAll().subscribe(
      (tiposFungible: TipoFungible[]) => {
        this.dataSource.data = tiposFungible;
      });

    this.dataSource.sort = this.sort;
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    // Realiza la búsqueda también sobre los datos de objetos mostrados en la tabla
    this.dataSource.filterPredicate = (order, filter: string) => {
      const transformedFilter = filter.trim().toLowerCase();

      const listAsFlatString = (obj): string => {
        let returnVal = '';

        Object.values(obj).forEach((val) => {
          if (typeof val !== 'object') {
            returnVal = returnVal + ' ' + val;
          } else if (val !== null) {
            returnVal = returnVal + ' ' + listAsFlatString(val);
          }
        });

        return returnVal.trim().toLowerCase();
      };

      return listAsFlatString(order).includes(transformedFilter);
    };
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleListadoComponent.name, 'ngOnDestroy() - start');
    this.logger.debug(
      TipoFungibleListadoComponent.name, 'ngOnDestroy() - end');
  }

}
