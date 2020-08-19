import { Component, ViewChild, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Acta } from '@core/models/eti/acta';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';

import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaAsistentesComponent } from '../acta-formulario/acta-asistentes/acta-asistentes.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';

import { ActaService } from '@core/services/eti/acta.service';

@Component({
  selector: 'sgi-acta-crear',
  templateUrl: './acta-crear.component.html',
  styleUrls: ['./acta-crear.component.scss']
})
export class ActaCrearComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGenerales', { static: true }) datosGenerales: ActaDatosGeneralesComponent;
  @ViewChild('memorias', { static: true }) memorias: ActaMemoriasComponent;
  @ViewChild('asistentes', { static: true }) asistentes: ActaAsistentesComponent;

  textoCrear = 'footer.eti.acta.guardar';

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

          this.snackBarService.showSuccess('eti.acta.crear.correcto');
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).mostrarError();
            this.snackBarService.showError('eti.acta.crear.error');
            this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
          }
        )
    );
  }
}
