import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, ViewChild, OnInit, ViewEncapsulation, OnDestroy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute, Data } from '@angular/router';
import { IAsistente } from '@core/models/eti/asistente';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { ConvocatoriaReunionListadoMemoriasComponent } from '../convocatoria-reunion-formulario/convocatoria-reunion-listado-memorias/convocatoria-reunion-listado-memorias.component';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { Observable, of, zip, from } from 'rxjs';
import { map, switchMap, first, tap } from 'rxjs/operators';

const MSG_BUTTON_SAVE = marker('footer.eti.convocatoriaReunion.guardar');
const MSG_SUCCESS = marker('eti.convocatoriaReunion.crear.correcto');
const MSG_ERROR = marker('eti.convocatoriaReunion.crear.error');

import {
  ConvocatoriaReunionDatosGeneralesComponent,
} from '../convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { MatTabChangeEvent } from '@angular/material/tabs/tab-group';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';

@Component({
  selector: 'sgi-convocatoria-reunion-crear',
  templateUrl: './convocatoria-reunion-crear.component.html',
  styleUrls: ['./convocatoria-reunion-crear.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ConvocatoriaReunionCrearComponent extends AbstractFormularioComponent implements OnInit, OnDestroy {


  @ViewChild('datosGenerales', { static: true }) datosGenerales: ConvocatoriaReunionDatosGeneralesComponent;
  @ViewChild('asignacionMemorias', { static: true }) asignacionMemorias: ConvocatoriaReunionListadoMemoriasComponent;

  idConvocatoria: number;
  datosGeneralesFormData: ConvocatoriaReunion;
  disableAsignarMemorias = false;
  disableCamposDatosGenerales = false;

  textoCrear = MSG_BUTTON_SAVE;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly asistenteService: AsistenteService,
    private readonly evaluacionService: EvaluacionService
  ) {
    super(logger, snackBarService);
  }

  ngOnInit() {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();
    this.datosGenerales.datosIniciales = new ConvocatoriaReunion();
    this.datosGenerales.datosFormulario = new ConvocatoriaReunion();

    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'ngOnInit()', 'end');
  }

  protected initTabs(): void {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'initTabs()', 'start');
    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.asignacionMemorias);
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'initTabs()', 'end');
  }

  /**
   * Setea el id de la convocatoria seleccionada
   * @param idConvocatoria id de la convocatoria seleccionada
   */
  public setConvocatoria(idConvocatoria: number) {
    this.idConvocatoria = idConvocatoria;
  }

  /**
   * Calcula y muestra los errores del formulario de la tab que inicia el
   * cambio de tab
   *
   * @param tabChangeEvent Tab origen desde la cual se inicia el cambio de tab
   */
  cambioTab(tabChangeEvent: MatTabChangeEvent): void {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'cambioTab(tabChangeEvent: MatTabChangeEvent)', 'start');

    super.cambioTab(tabChangeEvent);

    // Si se pasa al tab asignacionMemorias se recuperan los datos del tab datosGenerales necesarios para obtener memorias asignables
    if (tabChangeEvent.index === 1) {
      this.datosGeneralesFormData = this.datosGenerales.getDatosFormulario();

      // No se podrán asignar memorias si no tenemos los datos necesarios para determinar si son asignables
      this.disableAsignarMemorias = false;
      if (!this.datosGeneralesFormData.comite ||
        !this.datosGeneralesFormData.tipoConvocatoriaReunion ||
        !this.datosGeneralesFormData.fechaLimite) {

        this.disableAsignarMemorias = true;
      }
    }
    else // Si se pasa al tab datosGenerales
      if (tabChangeEvent.index === 0) {
        // Si existe alguna asignación de memoria, los campos de datosGenerales encargados
        // de determinar las memorias asignables no se podrán modificar
        this.disableCamposDatosGenerales = false;
        if (this.asignacionMemorias.evaluacionesAsignadas && this.asignacionMemorias.evaluacionesAsignadas.length > 0) {
          this.disableCamposDatosGenerales = true;
        }
      }

    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'cambioTab(tabChangeEvent: MatTabChangeEvent)', 'end');
  }

  protected sendData(): void {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'start');

    // Se hace el unsuscriber en el padre con este listado
    let nuevaConvocatoriaReunion: ConvocatoriaReunion;
    this.suscripciones.push(

      this.convocatoriaReunionService.create(this.datosGenerales.getDatosFormulario()).pipe(
        switchMap((convocatoriaReunion: ConvocatoriaReunion) => {
          nuevaConvocatoriaReunion = convocatoriaReunion;
          const convocantes = this.datosGenerales.getDatosConvocantesFormulario();

          const asistenteCreateSubscriptions: Observable<IAsistente>[] = [];

          convocantes.forEach((convocante: IEvaluador) => {
            const asistente: IAsistente = {
              id: null,
              convocatoriaReunion: nuevaConvocatoriaReunion,
              asistencia: true,
              evaluador: convocante,
              motivo: null
            };
            asistenteCreateSubscriptions.push(this.asistenteService.create(asistente));
          });

          if (asistenteCreateSubscriptions.length === 0) {
            return of(nuevaConvocatoriaReunion);
          } else {
            return zip(...asistenteCreateSubscriptions).pipe(
              map((asistentes: IAsistente[]) => {
                nuevaConvocatoriaReunion.convocantes = asistentes;
                return nuevaConvocatoriaReunion;
              }));
          }
        }),
        switchMap((convocatoriaReunion: ConvocatoriaReunion) => {

          const evaluacionesCreateSubscriptions: Observable<IEvaluacion>[] = [];

          this.asignacionMemorias.evaluacionesAsignadas.forEach(
            (evaluacion: IEvaluacion) => {
              // nuevas evaluaciones
              if (!evaluacion.id) {

                evaluacion.convocatoriaReunion = convocatoriaReunion;
                evaluacion.version = evaluacion.memoria.version + 1;
                evaluacion.activo = true;
                evaluacion.esRevMinima = false;
                evaluacion.fechaDictamen = convocatoriaReunion.fechaEvaluacion;
                evaluacion.tipoEvaluacion = new TipoEvaluacion();

                // Convocatoria Seguimiento
                if (convocatoriaReunion.tipoConvocatoriaReunion.id === 3) {
                  // mismo tipo seguimiento que la memoria Anual(3) Final(4)
                  evaluacion.tipoEvaluacion.id = (evaluacion.memoria.estadoActual.id === 12) ? 3 : 4;

                  // Convocatoria Ordinaria o Extraordinaria
                } else {

                  // memoria 'en secretaría' y retrospectiva 'en secretaría'
                  if (evaluacion.memoria.estadoActual.id !== 3 && evaluacion.memoria.requiereRetrospectiva) {
                    // tipo retrospectiva
                    evaluacion.tipoEvaluacion.id = 1;
                  } else {
                    // tipo 'memoria'
                    evaluacion.tipoEvaluacion.id = 2;
                  }
                }

                evaluacionesCreateSubscriptions.push(this.evaluacionService.create(evaluacion));
              }
            }
          );

          if (evaluacionesCreateSubscriptions.length === 0) {
            return of(nuevaConvocatoriaReunion);
          } else {
            return zip(...evaluacionesCreateSubscriptions);
          }
        })
      ).subscribe(
        (convocatoriaReunion: ConvocatoriaReunion) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).updateDatos(convocatoriaReunion);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'end');
        },
        () => {
          // Si falla mostramos el error en la pestañña
          this.tabs.get(0).showError();
          this.snackBarService.showError(MSG_ERROR);
          this.logger.error(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'end');
        }
      )
    );
  }
}
