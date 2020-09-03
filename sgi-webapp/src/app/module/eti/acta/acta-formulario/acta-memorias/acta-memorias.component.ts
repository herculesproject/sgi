import { Component, Input, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatSort } from '@angular/material/sort';

import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';

import { Memoria } from '@core/models/eti/memoria';

import { IEvaluacion } from '@core/models/eti/evaluacion';
import { MemoriaListado } from '@core/models/eti/memoria-listado';

import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';

@Component({
  selector: 'sgi-acta-memorias',
  templateUrl: './acta-memorias.component.html',
  styleUrls: ['./acta-memorias.component.scss']
})
export class ActaMemoriasComponent extends AbstractTabComponent<Memoria>  {
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  memorias$: Observable<MemoriaListado[]> = of();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService
  ) {
    super(logger);

    this.displayedColumns = ['referencia', 'version', 'dictamen.nombre', 'informe'];

  }

  @Input()
  set idConvocatoria(idConvocatoria: number) {
    if (idConvocatoria) {

      this.memorias$ = this.convocatoriaReunionService.findEvaluacionesActivas(idConvocatoria).pipe(
        switchMap((response) => {
          if (response.items) {

            const evaluacionesSinDuplicados = response.items.reduce((evaluacionObject, evaluacion: IEvaluacion) =>
              ({ ...evaluacionObject, [evaluacion.id]: evaluacion }), {});

            const memorias: MemoriaListado[] =
              Object.keys(evaluacionesSinDuplicados).map(idEvaluacion =>
                new MemoriaListado(evaluacionesSinDuplicados[idEvaluacion].memoria?.id,
                  evaluacionesSinDuplicados[idEvaluacion].memoria?.numReferencia,
                  evaluacionesSinDuplicados[idEvaluacion].memoria?.version,
                  evaluacionesSinDuplicados[idEvaluacion].dictamen));
            return of(memorias);
          } else {
            return of([]);
          }
        })
      );

    }
  }

  createFormGroup(): FormGroup {
    this.logger.debug(ActaMemoriasComponent.name, `createFormGroup()`, 'start');
    this.logger.debug(ActaMemoriasComponent.name, `createFormGroup()`, 'start');
    return null;
  }

  getDatosFormulario(): Memoria {
    this.logger.debug(ActaMemoriasComponent.name, `getDatosFormulario()`, 'start');
    this.logger.debug(ActaMemoriasComponent.name, `getDatosFormulario()`, 'end');
    return null;
  }
}
