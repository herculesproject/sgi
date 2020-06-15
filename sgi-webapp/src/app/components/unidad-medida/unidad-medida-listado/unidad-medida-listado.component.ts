import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { UnidadMedida } from '@core/models/unidad-medida';
import { MatTableDataSource } from '@angular/material/table';
import { NGXLogger } from 'ngx-logger';
import { MatSort } from '@angular/material/sort';
import { UnidadMedidaService } from '@core/services/unidad-medida.service';
import { UrlUtils } from '@core/utils/url-utils';

@Component({
  selector: 'app-unidad-medida-listado',
  templateUrl: './unidad-medida-listado.component.html',
  styleUrls: ['./unidad-medida-listado.component.scss'],
})
export class UnidadMedidaListadoComponent implements OnInit, OnDestroy {
  UrlUtils = UrlUtils;
  displayedColumns: string[] = ['abreviatura', 'descripcion', 'acciones'];
  dataSource: MatTableDataSource<UnidadMedida>;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private logger: NGXLogger,
    private unidadMedidaService: UnidadMedidaService
  ) {}

  ngOnInit(): void {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<UnidadMedida>([]);

    this.unidadMedidaService
      .findAll()
      .subscribe((unidadesMedida: UnidadMedida[]) => {
        this.dataSource.data = unidadesMedida;
      });

    this.dataSource.sort = this.sort;
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Realiza una búsqueda rápida sobre la tabla
   * @param event variable filtro
   */
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  ngOnDestroy(): void {
    this.logger.debug(
      UnidadMedidaListadoComponent.name,
      'ngOnDestroy() - start'
    );
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnDestroy() - end');
  }
}
