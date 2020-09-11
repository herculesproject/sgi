import { Component, Input, AfterViewInit } from '@angular/core';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { switchMap, map, filter, catchError, delay } from 'rxjs/operators';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaReunionAsignacionMemoriasComponent } from '../convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias.component';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Persona } from '@core/models/sgp/persona';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';


@Component({
  selector: 'sgi-convocatoria-reunion-listado-memorias',
  templateUrl: './convocatoria-reunion-listado-memorias.component.html',
  styleUrls: ['./convocatoria-reunion-listado-memorias.component.scss']
})
export class ConvocatoriaReunionListadoMemoriasComponent extends AbstractTabComponent<any> implements AfterViewInit {

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  filter: SgiRestFilter[];

  evaluacionesAsignadas$: Observable<IEvaluacion[]> = of([]);
  evaluacionesAsignadas: IEvaluacion[];

  idConvocatoriaSeleccionada: number;
  @Input() datosGeneralesFormData: ConvocatoriaReunion;
  @Input() disableAsignarMemorias = false;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly matDialog: MatDialog,
    protected readonly evaluacionService: EvaluacionService,
    protected readonly dialogService: DialogService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger);

    this.displayedColumns = ['referencia', 'version', 'dictamen.nombre', 'acciones'];
    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];
  }

  ngAfterViewInit(): void {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'ngAfterViewInit()', 'start');

    this.loadTable();

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'ngAfterViewInit()', 'end');
  }

  @Input()
  set idConvocatoria(idConvocatoria: number) {
    this.idConvocatoriaSeleccionada = idConvocatoria;
  }

  private loadTable() {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'start');

    this.evaluacionesAsignadas = [];

    if (!this.idConvocatoriaSeleccionada) {
      this.evaluacionesAsignadas$ = of([]).pipe(delay(0));
      this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'end');
      return;
    }

    // Do the request with paginator/sort/filter values
    this.evaluacionesAsignadas$ = this.evaluacionService
      .findAllByConvocatoriaReunionIdAndNoEsRevMinima(
        this.idConvocatoriaSeleccionada,
        {
          filters: this.filter
        })
      .pipe(
        map((response) => {
          // Return the values
          return response.items;
        }),
        switchMap((evaluaciones: IEvaluacion[]) => {
          if (!evaluaciones || evaluaciones.length === 0) {
            this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'end');
            return of([]);
          }

          const personaRefsEvaluadores = new Set<string>();

          evaluaciones.forEach((evaluacion: IEvaluacion) => {
            personaRefsEvaluadores.add(evaluacion?.evaluador1?.personaRef);
            personaRefsEvaluadores.add(evaluacion?.evaluador2?.personaRef);
          });

          const evaluacionesWithDatosPersonaEvaluadores$ = this.personaFisicaService.findByPersonasRefs([...personaRefsEvaluadores]).pipe(
            map((result: SgiRestListResult<Persona>) => {
              const personas = result.items;

              evaluaciones.forEach((evaluacion: IEvaluacion) => {
                const datosPersonaEvaluador1 = personas.find((persona: Persona) =>
                  evaluacion.evaluador1.personaRef === persona.personaRef);
                evaluacion.evaluador1.nombre = datosPersonaEvaluador1?.nombre;
                evaluacion.evaluador1.primerApellido = datosPersonaEvaluador1?.primerApellido;
                evaluacion.evaluador1.segundoApellido = datosPersonaEvaluador1?.segundoApellido;

                const datosPersonaEvaluador2 = personas.find((persona: Persona) =>
                  evaluacion.evaluador2.personaRef === persona.personaRef);
                evaluacion.evaluador2.nombre = datosPersonaEvaluador2?.nombre;
                evaluacion.evaluador2.primerApellido = datosPersonaEvaluador2?.primerApellido;
                evaluacion.evaluador2.segundoApellido = datosPersonaEvaluador2?.segundoApellido;
              });

              this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'end');

              this.evaluacionesAsignadas = evaluaciones;
              return evaluaciones;
            }));

          return evaluacionesWithDatosPersonaEvaluadores$;
        }),
        catchError(() => {
          // On error reset pagination values
          this.snackBarService.showError('eti.convocatoriaReunion.listado.error');
          this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'loadTable()', 'end');
  }



  createFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'createFormGroup()', 'start');

    const formGroup = new FormGroup({

    });
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'createFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<any> {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'crearObservable()', 'start');
    const observable = of({});
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosFormulario(): {} {
    return null;
  }

  /**
   * Abre una ventana modal para añadir una nueva asignación de memoria al listado de memorias de una evaluación.
   *
   * Esta ventana solo se debería abrir si tenemos idConvocatoriaReunion (estamos modificando) o cuando estamos
   * creando una nueva ConvocatoriaReunion y ya tenemos establecidos los valores necesarios para obtener las memorias
   * asignables (Comité, Tipo Convocatoria y Fecha Límite)
   *
   */
  openDialogAsignarMemoria(): void {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'openDialogAsignarMemoria()', 'start');

    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      ...this.matDialog,
      data: {
        params: {
          idConvocatoria: this.datosGeneralesFormData.id,
          filterMemoriasAsignables: {
            idComite: this.datosGeneralesFormData.comite.id,
            idTipoConvocatoria: this.datosGeneralesFormData.tipoConvocatoriaReunion.id,
            fechaLimite: new Date(this.datosGeneralesFormData.fechaLimite),
          },
          memoriasAsignadas: this.evaluacionesAsignadas.map(evaluacion => evaluacion.memoria)
        }
      }
    };

    const dialogRef = this.matDialog.open(ConvocatoriaReunionAsignacionMemoriasComponent, config);

    this.suscripciones.push(
      dialogRef.afterClosed().subscribe(
        (evaluacion: IEvaluacion) => {
          if (evaluacion) {
            this.evaluacionesAsignadas.push(evaluacion);
            this.evaluacionesAsignadas$ = of(this.evaluacionesAsignadas.slice());
          }
        }
      ));

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'openDialogAsignarMemoria()', 'end');

  }


  /**
   * Elimina la convocatoria reunion.
   * @param evaluacionId id de la evaluacion a eliminar.
   * @param event evento lanzado.
   */
  borrar(evaluacionId: number, $event: Event): void {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name,
      'borrar(convocatoriaReunionId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    const dialogSubscription = this.dialogService.showConfirmation(
      'eti.convocatoriaReunion.listado.eliminar'
    ).pipe(
      filter((aceptado: boolean) => aceptado),
      switchMap(_ => {
        return this.evaluacionService
          .deleteById(evaluacionId).pipe(
            map(() => {
              return this.loadTable();
            })
          );
      })
    ).subscribe(_ => {
      this.snackBarService
        .showSuccess('eti.convocatoriaReunion.listado.eliminarConfirmado');
    });

    this.suscripciones.push(dialogSubscription);

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name,
      'borrar(convocatoriaReunionId: number, $event: Event) - end');
  }

}
