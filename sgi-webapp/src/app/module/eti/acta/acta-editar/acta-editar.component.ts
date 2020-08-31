import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { ActaAsistentesListadoComponent } from '../acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaService } from '@core/services/eti/acta.service';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Acta } from '@core/models/eti/acta';


const MSG_BUTTON_EDIT = marker('footer.eti.acta.actualizar');
const MSG_SUCCESS = marker('eti.acta.editar.correcto');
const MSG_ERROR = marker('eti.acta.editar.error');
const MSG_NOT_FOUND = marker('eti.acta.editar.no-encontrado');

@Component({
  selector: 'sgi-acta-editar',
  templateUrl: './acta-editar.component.html',
  styleUrls: ['./acta-editar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActaEditarComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGenerales', { static: true }) datosGenerales: ActaDatosGeneralesComponent;
  @ViewChild('memorias', { static: true }) memorias: ActaMemoriasComponent;
  @ViewChild('asistentes', { static: true }) asistentes: ActaAsistentesListadoComponent;

  textoCrear = MSG_BUTTON_EDIT;

  idConvocatoria: number;

  private idActa: number;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private route: ActivatedRoute,
    private actaService: ActaService) {

    super(logger, snackBarService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.getActa();
  }

  /**
   * Se recupera el acta que se va a editar.
   */
  getActa(): void {
    this.logger.debug(ActaEditarComponent.name, 'getActa()', 'start');
    // Obtiene los parámetros de la url
    this.suscripciones.push(this.route.params.pipe(
      switchMap((params: Params) => {
        if (params.id) {
          const id = Number(params.id);
          return this.actaService.findById(id);
        }
        return of(null);

      })
    ).subscribe(
      (acta: Acta) => {
        if (acta) {
          this.datosGenerales.datosIniciales = acta;
          this.datosGenerales.datosFormulario = { ...this.datosGenerales.datosIniciales };
          this.datosGenerales.setDatosFormulario(acta);
          this.idActa = acta.id;

        }
      },
      () => {
        this.snackBarService.showError(MSG_NOT_FOUND);
        this.router.navigate(['../'], { relativeTo: this.route });
      }));
    this.logger.debug(ActaEditarComponent.name, 'getActa()', 'end');
  }


  protected initTabs(): void {
    this.logger.debug(ActaEditarComponent.name, 'inicializarTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.memorias);
    this.tabs.set(2, this.asistentes);

    this.logger.debug(ActaEditarComponent.name, 'inicializarTabs()', 'end');
  }


  protected sendData() {
    this.logger.debug(ActaEditarComponent.name, 'enviarDatos()', 'start');
    this.suscripciones.push(
      // Se actualizan los datos generales del acta.
      this.actaService.update(this.idActa, this.datosGenerales.getDatosFormulario())
        .subscribe((acta: Acta) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).updateDatos(acta);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(ActaEditarComponent.name, 'enviarDatos()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).showError();
            this.snackBarService.showError(MSG_ERROR);
            this.logger.error(ActaEditarComponent.name, 'enviarDatos()', 'end');
          }
        )
    );
  }

  public setConvocatoria(idConvocatoria: number) {
    this.idConvocatoria = idConvocatoria;
  }
}
