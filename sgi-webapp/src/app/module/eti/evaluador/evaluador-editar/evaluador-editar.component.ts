import { Component, ViewChild, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IEvaluador } from '@core/models/eti/evaluador';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';
import { EvaluadorDatosGeneralesComponent } from '../evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { switchMap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const MSG_BUTTON_SAVE = marker('footer.eti.evaluador.actualizar');
const MSG_SUCCESS = marker('eti.evaluador.actualizar.correcto');
const MSG_ERROR = marker('eti.evaluador.actualizar.error');

@Component({
  selector: 'sgi-evaluador-editar',
  templateUrl: './evaluador-editar.component.html',
  styleUrls: ['./evaluador-editar.component.scss']
})
export class EvaluadorEditarComponent extends AbstractFormularioComponent implements OnInit {

  @ViewChild('datosGenerales', { static: true }) datosGenerales: EvaluadorDatosGeneralesComponent;

  idComite: number;
  idCargoComite: number;
  idEvaluador: number;

  evaluadorServiceOneSubscritpion: Subscription;

  textoActualizar = MSG_BUTTON_SAVE;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly evaluadorService: EvaluadorService
  ) {
    super(logger, snackBarService);
  }


  /**
   * Setea el id del comité
   * @param idComite id del comité seleccionado
   */
  public setComite(idComite: number) {
    this.idComite = idComite;
  }

  /**
   * Setea el id del cargo comité
   * @param idCargoComite id del cargo comité seleccionado
   */
  public setCargoComite(idCargoComite: number) {
    this.idCargoComite = idCargoComite;
  }

  ngOnInit() {
    this.logger.debug(EvaluadorEditarComponent.name, 'ngOnInit()', 'start');
    this.datosGenerales.formGroup = this.datosGenerales.createFormGroup();
    super.ngOnInit();
    const id = this.route.snapshot.params.id;
    if (id && !isNaN(id)) {
      this.getDatosEvaluador(Number(id));
    }

    this.logger.debug(EvaluadorEditarComponent.name, 'ngOnInit()', 'end');
  }

  protected inicializarTabs(): void {

  }

  protected initTabs(): void {
    this.logger.debug(EvaluadorEditarComponent.name, 'initTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);

    this.logger.debug(EvaluadorEditarComponent.name, 'initTabs()', 'end');
  }


  getDatosEvaluador(evaluadorId: number): void {
    this.evaluadorServiceOneSubscritpion = this.evaluadorService
      .findById(Number(evaluadorId))
      .subscribe(
        (evaluador: IEvaluador) => {

          this.datosGenerales.datosIniciales = evaluador;
          this.datosGenerales.datosFormulario = { ...this.datosGenerales.datosIniciales };
          this.datosGenerales.setDatosFormulario(evaluador);
          this.idEvaluador = evaluador.id;

          this.logger.debug(
            EvaluadorDatosGeneralesComponent.name,
            'getDatosEvaluador()',
            'end'
          );
        },
        () => {
          this.snackBarService.showSuccess('eti.evaluador.actualizar.no-encontrado');
          this.logger.debug(
            EvaluadorDatosGeneralesComponent.name,
            'getDatosEvaluador()',
            'end'
          );
        }
      );
  }

  protected sendData() {
    this.logger.debug(EvaluadorEditarComponent.name, 'sendData()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    this.suscripciones.push(
      this.evaluadorService.update(this.idEvaluador, this.datosGenerales.getDatosFormulario())
        .subscribe((evaluador: IEvaluador) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).updateDatos(evaluador);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(EvaluadorEditarComponent.name, 'sendData()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).showError();
            this.snackBarService.showSuccess(MSG_ERROR);
            this.logger.error(EvaluadorEditarComponent.name, 'sendData()', 'end');
          }
        )
    );
  }
}
