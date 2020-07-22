import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Acta } from '@core/models/eti/acta';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { AbstractFormularioComponent } from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';

import { ActaCrearAsistentesComponent } from './acta-crear-asistentes/acta-crear-asistentes.component';
import { ActaCrearDatosGeneralesComponent } from './acta-crear-datos-generales/acta-crear-datos-generales.component';
import { ActaCrearMemoriasComponent } from './acta-crear-memorias/acta-crear-memorias.component';

@Component({
  selector: 'app-acta-crear',
  templateUrl: './acta-crear.component.html',
  styleUrls: ['./acta-crear.component.scss']
})
export class ActaCrearComponent extends AbstractFormularioComponent {
  @ViewChild('datosGenerales') datosGenerales: ActaCrearDatosGeneralesComponent;
  @ViewChild('memorias') memorias: ActaCrearMemoriasComponent;
  @ViewChild('asistentes') asistentes: ActaCrearAsistentesComponent;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.eti.acta.guardar');
  }

  protected inicializarTabs(): void {
    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.memorias);
    this.tabs.set(2, this.asistentes);
  }

  protected enviarDatos() {
    this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    this.subscripciones.push(
      this.tabs.get(0).mandarPeticion().subscribe(
        (acta: Acta) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).actualizarDatos(acta);
          this.tabs.get(0).warning = false;

          // Cuando la petición de una pestaña sale bien realizamos la petición de la siguiente

          /* this.subscripciones.push(this.tabs.get(1).mandarPeticion().subscribe(
             (res2) => {
               // this.tabs.get(1).actualizarDatos(res2);
               this.tabs.get(1).warning = false;
               this.subscripciones.push(this.tabs.get(2).mandarPeticion().subscribe(
                 (res3) => {
                   // this.tabs.get(2).actualizarDatos(res3);
                   this.tabs.get(2).warning = false;*/
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('eti.acta.crear.correcto'));
          this.router.navigateByUrl(`${UrlUtils.eti.root}/${UrlUtils.eti.actas}`).then();
          this.logger.debug(ActaCrearComponent.name, 'enviarDatos()', 'end');
          /*  },
            () => {
              this.tabs.get(2).mostrarError();
              this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
            }));
        },
        () => {
          this.tabs.get(1).mostrarError();
          this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
        }));*/
        },
        () => {
          // Si falla mostramos el error en la pestañña
          this.tabs.get(0).mostrarError();
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('eti.acta.crear.error.form'));
          this.logger.error(ActaCrearComponent.name, 'enviarDatos()', 'end');
        }
      )
    );
  }
}
