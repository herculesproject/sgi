import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, ViewChild, OnInit, ViewEncapsulation } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { IAsistente } from '@core/models/eti/asistente';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { Evaluador } from '@core/models/eti/evaluador';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { Observable, of, zip } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_BUTTON_SAVE = marker('footer.eti.convocatoriaReunion.guardar');
const MSG_SUCCESS = marker('eti.convocatoriaReunion.crear.correcto');
const MSG_ERROR = marker('eti.convocatoriaReunion.crear.error');

import {
  ConvocatoriaReunionDatosGeneralesComponent,
} from '../convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';

@Component({
  selector: 'sgi-convocatoria-reunion-crear',
  templateUrl: './convocatoria-reunion-crear.component.html',
  styleUrls: ['./convocatoria-reunion-crear.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ConvocatoriaReunionCrearComponent extends AbstractFormularioComponent implements OnInit {

  @ViewChild('datosGenerales', { static: true }) datosGenerales: ConvocatoriaReunionDatosGeneralesComponent;

  textoCrear = MSG_BUTTON_SAVE;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly asistenteService: AsistenteService
  ) {
    super(logger, snackBarService);
  }

  ngOnInit() {
    super.ngOnInit();

    this.datosGenerales.datosIniciales = new ConvocatoriaReunion();
    this.datosGenerales.datosFormulario = new ConvocatoriaReunion();
  }

  protected initTabs(): void {
    this.tabs.set(0, this.datosGenerales);
  }

  protected sendData() {
    this.logger.debug(ConvocatoriaReunionCrearComponent.name, 'enviarDatos()', 'start');
    // Se hace el unsuscriber en el padre con este listado
    let nuevaConvocatoriaReunion: ConvocatoriaReunion;
    this.suscripciones.push(

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
