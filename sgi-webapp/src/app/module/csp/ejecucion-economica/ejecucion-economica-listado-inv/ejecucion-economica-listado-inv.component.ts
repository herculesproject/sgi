import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IRelacionEjecucionEconomica, TipoEntidad, TIPO_ENTIDAD_MAP } from '@core/models/csp/relacion-ejecucion-economica';
import { ROUTE_NAMES } from '@core/route.names';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { SgiRestFilter, SgiRestListResult } from '@herculesproject/framework/http';
import { Observable } from 'rxjs';

@Component({
  selector: 'sgi-ejecucion-economica-listado-inv',
  templateUrl: './ejecucion-economica-listado-inv.component.html',
  styleUrls: ['./ejecucion-economica-listado-inv.component.scss']
})
export class EjecucionEconomicaListadoInvComponent extends AbstractTablePaginationComponent<IRelacionEjecucionEconomica>
  implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  dataSource$: Observable<IRelacionEjecucionEconomica[]>;

  tipoEntidadSelected: TipoEntidad;
  columnasTipoEntidadGrupo: string[];
  columnasTipoEntidadProyecto: string[];

  get TIPO_ENTIDAD_MAP() {
    return TIPO_ENTIDAD_MAP;
  }

  get TipoEntidad() {
    return TipoEntidad;
  }

  constructor(
    private readonly relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService
  ) {
    super();

    this.resolveSortProperty = (column: string) => {
      if (column === 'nombre' && this.tipoEntidadSelected === TipoEntidad.GRUPO) {
        return 'nombre.value';
      }
      return column;
    };
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.createFormGroup();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    const onlyWithParticipacionActual: boolean = !!this.formGroup.controls.onlyWithParticipacionActual.value;

    switch (this.tipoEntidadSelected) {
      case TipoEntidad.GRUPO:
        return this.relacionEjecucionEconomicaService.findRelacionesGruposInvestigador(
          onlyWithParticipacionActual, this.getFindOptions(reset));
      case TipoEntidad.PROYECTO:
        return this.relacionEjecucionEconomicaService.findRelacionesProyectosInvestigador(
          onlyWithParticipacionActual, this.getFindOptions(reset));
      default:
        throw Error(`Invalid tipoEntidad "${this.tipoEntidadSelected}"`);
    }
  }

  protected initColumns(): void {
    this.columnasTipoEntidadGrupo = [
      'id',
      'proyectoSgeRef',
      'nombre',
      'fechaInicio',
      'fechaFin',
      'acciones'
    ];

    this.columnasTipoEntidadProyecto = [
      'id',
      'proyectoSgeRef',
      'nombre',
      'codigoExterno',
      'codigoInterno',
      'fechaInicio',
      'fechaFin',
      'acciones'
    ];

    this.columnas = this.columnasTipoEntidadProyecto;
  }

  protected loadTable(reset?: boolean): void {
    this.dataSource$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;
    this.columnas = this.tipoEntidadSelected === TipoEntidad.PROYECTO ?
      this.columnasTipoEntidadProyecto : this.columnasTipoEntidadGrupo;

    return undefined;
  }

  protected resetFilters(): void {
    this.initFormGroup(true);
  }

  private createFormGroup(): void {
    this.formGroup = new FormGroup({
      tipoEntidad: new FormControl(null),
      onlyWithParticipacionActual: new FormControl(false)
    });

    this.initFormGroup();
  }

  private initFormGroup(reset = false): void {
    if (reset) {
      this.formGroup.reset();
    }

    this.formGroup.controls.tipoEntidad.setValue(TipoEntidad.PROYECTO);
    this.formGroup.controls.onlyWithParticipacionActual.setValue(false);

    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;
  }

  protected setupI18N(): void { }
}
