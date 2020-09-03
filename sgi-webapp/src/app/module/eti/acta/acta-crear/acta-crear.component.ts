import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, ViewChild, OnInit, ViewEncapsulation } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { IActa } from '@core/models/eti/acta';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';


import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaAsistentesListadoComponent } from '../acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';
import { ActaService } from '@core/services/eti/acta.service';


const MSG_BUTTON_SAVE = marker('footer.eti.acta.guardar');
const MSG_SUCCESS = marker('eti.acta.crear.correcto');
const MSG_ERROR = marker('eti.acta.crear.error');

@Component({
  selector: 'sgi-acta-crear',
  templateUrl: './acta-crear.component.html',
  styleUrls: ['./acta-crear.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActaCrearComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGenerales', { static: true }) datosGenerales: ActaDatosGeneralesComponent;
  @ViewChild('memorias', { static: true }) memorias: ActaMemoriasComponent;
  @ViewChild('asistentes', { static: true }) asistentes: ActaAsistentesListadoComponent;

  textoCrear = MSG_BUTTON_SAVE;

  idConvocatoria: number;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly actaService: ActaService
  ) {
    super(logger, snackBarService);
  }

  ngOnInit() {
    this.logger.debug(ActaCrearComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();
    this.datosGenerales.datosIniciales = null;
    this.datosGenerales.datosFormulario = { ...this.datosGenerales.datosIniciales };
    this.datosGenerales.formGroup = this.datosGenerales.createFormGroup();

    this.memorias.datosIniciales = null;
    this.memorias.datosFormulario = { ...this.memorias.datosIniciales };
    this.memorias.formGroup = this.memorias.createFormGroup();

    this.asistentes.datosIniciales = null;
    this.asistentes.datosFormulario = { ...this.asistentes.datosIniciales };
    this.asistentes.formGroup = this.asistentes.createFormGroup();

    this.logger.debug(ActaCrearComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Setea el id de la convocatoria seleccionada
   * @param idConvocatoria id de la convocatoria seleccionada
   */
  public setConvocatoria(idConvocatoria: number) {
    this.idConvocatoria = idConvocatoria;
  }

  protected initTabs(): void {
    this.logger.debug(ActaCrearComponent.name, 'inicializarTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.memorias);
    this.tabs.set(2, this.asistentes);

    this.logger.debug(ActaCrearComponent.name, 'inicializarTabs()', 'end');
  }

  protected sendData() {
    this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'start');

    const acta: IActa = this.datosGenerales.getDatosFormulario();
    acta.activo = true;
    acta.inactiva = true;
    this.suscripciones.push(
      // Se crean los datos generales del acta.
      this.actaService.create(acta)
        .subscribe((actaCreada: IActa) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).updateDatos(actaCreada);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).showError();
            this.snackBarService.showError(MSG_ERROR);
            this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
          }
        )
    );
  }
}
