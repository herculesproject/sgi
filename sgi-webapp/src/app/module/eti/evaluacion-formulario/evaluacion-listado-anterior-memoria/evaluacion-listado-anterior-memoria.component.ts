import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { IEvaluacionWithNumComentario } from '@core/models/eti/dto/evaluacion-with-num-comentarios';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';

const MSG_ERROR = marker('eti.evaluacion.listado.anterior.error');

@Component({
  selector: 'sgi-evaluacion-listado-anterior-memoria',
  templateUrl: './evaluacion-listado-anterior-memoria.component.html',
  styleUrls: ['./evaluacion-listado-anterior-memoria.component.scss']
})
export class EvaluacionListadoAnteriorMemoriaComponent extends AbstractPaginacionComponent<IEvaluacionWithNumComentario>  {
  memoriaId: number;
  evaluacionId: number;
  evaluaciones$: Observable<IEvaluacionWithNumComentario[]>;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, snackBarService, MSG_ERROR);
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluacionWithNumComentario>> {
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'createObservable()', 'start');
    let observable$ = null;
    if (this.memoriaId && this.evaluacionId) {
      observable$ = this.memoriaService.getEvaluaciones(this.memoriaId, this.evaluacionId, this.getFindOptions());
    }
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'createObservable()', 'end');
    return observable$;
  }

  protected initColumnas(): void {
    this.columnas = ['memoria.numReferencia', 'version', 'fechaDictamen', 'dictamen.nombre', 'numComentarios', 'pdf'];
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'loadTable()', 'start');
    this.evaluaciones$ = this.getObservableLoadTable(reset);
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'loadTable()', 'end');
  }

  protected createFiltros(): SgiRestFilter[] {
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'createFiltros()', 'start');
    this.logger.debug(EvaluacionListadoAnteriorMemoriaComponent.name, 'createFiltros()', 'end');
    return [];
  }
}
