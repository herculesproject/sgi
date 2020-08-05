import { Component, ViewChild } from '@angular/core';
import { ConvocatoriaReunionDatosGeneralesComponent } from '../convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router } from '@angular/router';
import { AbstractFormularioComponent } from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';

@Component({
  selector: 'app-convocatoria-reunion-crear',
  templateUrl: './convocatoria-reunion-crear.component.html',
  styleUrls: ['./convocatoria-reunion-crear.component.scss']
})
export class ConvocatoriaReunionCrearComponent extends AbstractFormularioComponent {

  @ViewChild('datosGenerales') datosGenerales: ConvocatoriaReunionDatosGeneralesComponent;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.eti.convocatoriaReunion.guardar');
  }

  protected inicializarTabs(): void {
    this.tabs.set(0, this.datosGenerales);
  }

  protected enviarDatos() {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    this.subscripciones.push(
      this.tabs.get(0).mandarPeticion().subscribe(
        (convocatoriaReunion: ConvocatoriaReunion) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).actualizarDatos(convocatoriaReunion);
          this.tabs.get(0).warning = false;

          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('eti.convocatoriaReunion.crear.correcto'));
          this.router.navigateByUrl(`${UrlUtils.eti.root}/${UrlUtils.eti.solicitudesConvocatoria}`).then();
          this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'end');
        },
        () => {
          // Si falla mostramos el error en la pestañña
          this.tabs.get(0).mostrarError();
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('eti.convocatoriaReunion.crear.error'));
          this.logger.error(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'end');
        }
      )
    );
  }

}
