import { Component, ViewChild, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Acta } from '@core/models/eti/acta';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { AbstractFormularioComponent } from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';

import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaAsistentesComponent } from '../acta-formulario/acta-asistentes/acta-asistentes.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';

import { ActaService } from '@core/services/eti/acta.service';

@Component({
  selector: 'app-acta-crear',
  templateUrl: './acta-crear.component.html',
  styleUrls: ['./acta-crear.component.scss']
})
export class ActaCrearComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGenerales', { static: true }) datosGenerales: ActaDatosGeneralesComponent;
  @ViewChild('memorias', { static: true }) memorias: ActaMemoriasComponent;
  @ViewChild('asistentes', { static: true }) asistentes: ActaAsistentesComponent;

  idConvocatoria: number;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly actaService: ActaService
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.eti.acta.guardar');
  }

  ngOnInit() {
    this.logger.debug(ActaCrearComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();
    this.datosGenerales.datosIniciales = new Acta();
    this.datosGenerales.datosFormulario = { ...this.datosGenerales.datosIniciales };
    this.datosGenerales.formGroup = this.datosGenerales.crearFormGroup();

    this.memorias.datosIniciales = new Acta();
    this.memorias.datosFormulario = { ...this.memorias.datosIniciales };
    this.memorias.formGroup = this.memorias.crearFormGroup();

    this.asistentes.datosIniciales = new Acta();
    this.asistentes.datosFormulario = { ...this.asistentes.datosIniciales };
    this.asistentes.formGroup = this.asistentes.crearFormGroup();

    this.logger.debug(ActaCrearComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Setea el id de la convocatoria seleccionada
   * @param idConvocatoria id de la convocatoria seleccionada
   */
  public setConvocatoria(idConvocatoria: number) {
    this.idConvocatoria = idConvocatoria;
  }

  protected inicializarTabs(): void {
    this.logger.debug(ActaCrearComponent.name, 'inicializarTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.memorias);
    this.tabs.set(2, this.asistentes);

    this.logger.debug(ActaCrearComponent.name, 'inicializarTabs()', 'end');
  }

  protected enviarDatos() {
    this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'start');
    this.subscripciones.push(
      // Se crean los datos generales del acta.
      this.actaService.create(this.datosGenerales.getDatosFormulario())
        .subscribe((acta: Acta) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).actualizarDatos(acta);
          this.tabs.get(0).warning = false;

          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('eti.acta.crear.correcto'));
          this.router.navigateByUrl(`${UrlUtils.eti.root}/${UrlUtils.eti.actas}`).then();
          this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).mostrarError();
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto('eti.acta.crear.error'));
            this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
          }
        )
    );
  }
}
