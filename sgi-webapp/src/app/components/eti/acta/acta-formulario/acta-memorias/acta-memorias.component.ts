import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Acta } from '@core/models/eti/acta';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/services/form-group-util';
import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { MemoriaListado } from '@core/models/eti/memoria-listado';
import { switchMap } from 'rxjs/operators';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { Evaluacion } from '@core/models/eti/evaluacion';
import { SgiRestFilterType, SgiRestFilter, SgiRestSortDirection } from '@sgi/framework/http';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-acta-memorias',
  templateUrl: './acta-memorias.component.html',
  styleUrls: ['./acta-memorias.component.scss']
})
export class ActaMemoriasComponent extends AbstractTabComponent<any>  {

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  filter: SgiRestFilter[];

  filterActivo = {
    field: 'activo',
    type: SgiRestFilterType.EQUALS,
    value: 'true'
  };

  memorias$: Observable<MemoriaListado[]> = of();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly evaluacionService: EvaluacionService
  ) {
    super(logger);

    this.displayedColumns = ['referencia', 'version', 'dictamen.nombre', 'informe'];

    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];
  }

  @Input()
  set idConvocatoria(idConvocatoria: number) {
    this.filter = [];
    if (idConvocatoria) {
      const filterConvocatoria: SgiRestFilter = {
        field: 'convocatoriaReunion.id',
        type: SgiRestFilterType.EQUALS,
        value: idConvocatoria.toString()
      };
      this.filter.push(filterConvocatoria);

      this.filter.push(this.filterActivo);

      this.memorias$ = this.evaluacionService.findAll({
        sort: {
          direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
          field: this.sort.active
        }, filters: this.filter
      }).pipe(
        switchMap((response) => {
          if (response.items) {

            const evaluacionesSinDuplicados = response.items.reduce((evaluacionObject, evaluacion: Evaluacion) =>
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



  crearFormGroup(): FormGroup {
    this.logger.debug(ActaMemoriasComponent.name, 'crearFormGroup()', 'start');

    const formGroup = new FormGroup({

    });
    this.logger.debug(ActaMemoriasComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<any> {
    this.logger.debug(ActaMemoriasComponent.name, 'crearObservable()', 'start');
    const observable = of({});
    this.logger.debug(ActaMemoriasComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosIniciales(): Acta {
    this.logger.debug(ActaMemoriasComponent.name, 'crearObservable()', 'start');
    const datos = new Acta();
    this.logger.debug(ActaMemoriasComponent.name, 'crearObservable()', 'end');
    return datos;
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
