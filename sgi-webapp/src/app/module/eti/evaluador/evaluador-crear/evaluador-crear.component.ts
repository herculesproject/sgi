import { Component, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { IEvaluador } from '@core/models/eti/evaluador';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';
import { EvaluadorDatosGeneralesComponent } from '../evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { switchMap } from 'rxjs/operators';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';


const MSG_BUTTON_SAVE = marker('footer.eti.evaluador.guardar');
const MSG_SUCCESS = marker('eti.evaluador.crear.correcto');
const MSG_ERROR = marker('eti.evaluador.crear.error');

@Component({
  selector: 'sgi-evaluador-crear',
  templateUrl: './evaluador-crear.component.html',
  styleUrls: ['./evaluador-crear.component.scss']
})
export class EvaluadorCrearComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGenerales', { static: true }) datosGenerales: EvaluadorDatosGeneralesComponent;

  idComite: number;
  idCargoComite: number;

  textoCrear = MSG_BUTTON_SAVE;

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
    this.logger.debug(EvaluadorCrearComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();
    this.datosGenerales.datosIniciales = {
      activo: null,
      cargoComite: null,
      comite: null,
      fechaAlta: null,
      fechaBaja: null,
      id: null,
      nombre: '',
      primerApellido: '',
      resumen: '',
      segundoApellido: '',
      personaRef: '',
      identificadorLetra: '',
      identificadorNumero: ''
    };
    this.datosGenerales.datosFormulario = { ...this.datosGenerales.datosIniciales };
    this.datosGenerales.formGroup = this.datosGenerales.createFormGroup();

    this.logger.debug(EvaluadorCrearComponent.name, 'ngOnInit()', 'end');
  }

  protected initTabs(): void {
    this.logger.debug(EvaluadorCrearComponent.name, 'initTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);

    this.logger.debug(EvaluadorCrearComponent.name, 'initTabs()', 'end');
  }

  protected sendData() {
    this.logger.debug(EvaluadorCrearComponent.name, 'sendData()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    this.suscripciones.push(
      this.evaluadorService.create(this.datosGenerales.getDatosFormulario())
        .subscribe((evaluador: IEvaluador) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).updateDatos(evaluador);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(EvaluadorCrearComponent.name, 'sendData()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).showError();
            this.snackBarService.showSuccess(MSG_ERROR);
            this.logger.error(EvaluadorCrearComponent.name, 'sendData()', 'end');
          }
        )
    );
  }
}
