import { Component, ViewChild, OnInit } from '@angular/core';
import { ConvocatoriaReunionDatosGeneralesComponent } from '../convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router } from '@angular/router';
import { AbstractFormularioComponent } from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { switchMap, map } from 'rxjs/operators';
import { Observable, of, zip } from 'rxjs';
import { IAsistente } from '@core/models/eti/asistente';
import { Evaluador } from '@core/models/eti/evaluador';
import { AsistenteService } from '@core/services/eti/asistente.service';

@Component({
  selector: 'app-convocatoria-reunion-crear',
  templateUrl: './convocatoria-reunion-crear.component.html',
  styleUrls: ['./convocatoria-reunion-crear.component.scss']
})
export class ConvocatoriaReunionCrearComponent extends AbstractFormularioComponent implements OnInit {

  @ViewChild('datosGenerales', { static: true }) datosGenerales: ConvocatoriaReunionDatosGeneralesComponent;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly asistenteService: AsistenteService
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.eti.convocatoriaReunion.guardar');
  }

  ngOnInit() {
    super.ngOnInit();

    this.datosGenerales.datosIniciales = new ConvocatoriaReunion();
    this.datosGenerales.datosFormulario = new ConvocatoriaReunion();
    this.datosGenerales.formGroup = this.datosGenerales.crearFormGroup();
  }

  protected inicializarTabs(): void {
    this.tabs.set(0, this.datosGenerales);
  }

  protected enviarDatos() {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    let nuevaConvocatoriaReunion: ConvocatoriaReunion;
    this.subscripciones.push(

      this.convocatoriaReunionService.create(this.datosGenerales.getDatosFormulario()).pipe(
        switchMap((convocatoriaReunion: ConvocatoriaReunion) => {
          nuevaConvocatoriaReunion = convocatoriaReunion;
          const convocantes = this.datosGenerales.getDatosConvocantesFormulario();

          const asistenteCreateSubscriptions: Observable<IAsistente>[] = [];
          convocantes.forEach((convocante: Evaluador) => {
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
            return of([]);
          } else {
            return zip(...asistenteCreateSubscriptions);
          }
        }),
        map((convocantes: IAsistente[]) => {
          nuevaConvocatoriaReunion.convocantes = convocantes;
          return nuevaConvocatoriaReunion;
        })
      ).subscribe(
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
