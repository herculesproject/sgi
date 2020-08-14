import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';
import { ActaAsistentesComponent } from '../acta-formulario/acta-asistentes/acta-asistentes.component';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ActaService } from '@core/services/eti/acta.service';
import { of } from 'rxjs';
import { Acta } from '@core/models/eti/acta';
import { UrlUtils } from '@core/utils/url-utils';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';

@Component({
  selector: 'app-acta-editar',
  templateUrl: './acta-editar.component.html',
  styleUrls: ['./acta-editar.component.scss']
})
export class ActaEditarComponent extends AbstractFormularioComponent implements OnInit {

  @ViewChild('datosGenerales', { static: true }) datosGenerales: ActaDatosGeneralesComponent;
  @ViewChild('memorias', { static: true }) memorias: ActaMemoriasComponent;
  @ViewChild('asistentes', { static: true }) asistentes: ActaAsistentesComponent;

  textoCrear = 'footer.eti.acta.actualizar';

  idConvocatoria: number;

  private idActa: number;

  constructor(protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private activatedRoute: ActivatedRoute,
    private actaService: ActaService) {

    super(logger, snackBarService);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.datosGenerales.formGroup = this.datosGenerales.crearFormGroup();
    this.memorias.formGroup = this.memorias.crearFormGroup();
    this.asistentes.formGroup = this.asistentes.crearFormGroup();

    this.getActa();

  }

  /**
   * Se recupera el acta que se va a editar.
   */
  getActa(): void {
    this.logger.debug(ActaEditarComponent.name, 'getActa()', 'start');
    // Obtiene los parámetros de la url
    this.subscripciones.push(this.activatedRoute.params.pipe(
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
          this.memorias.datosIniciales = acta;
          this.memorias.datosFormulario = { ...this.memorias.datosIniciales };
          this.asistentes.datosIniciales = acta;
          this.asistentes.datosFormulario = { ...this.asistentes.datosIniciales };
          this.idActa = acta.id;

        }
      },
      () => {
        this.snackBarService.showError('eti.acta.editar.no-encontrado');
        this.router.navigateByUrl(UrlUtils.eti.actas).then();
      }));
    this.logger.debug(ActaEditarComponent.name, 'getActa()', 'end');
  }


  protected inicializarTabs(): void {
    this.logger.debug(ActaEditarComponent.name, 'inicializarTabs()', 'start');

    this.tabs.set(0, this.datosGenerales);
    this.tabs.set(1, this.memorias);
    this.tabs.set(2, this.asistentes);

    this.logger.debug(ActaEditarComponent.name, 'inicializarTabs()', 'end');
  }


  protected enviarDatos() {
    this.logger.debug(ActaEditarComponent.name, 'enviarDatos()', 'start');
    this.subscripciones.push(
      // Se actualizan los datos generales del acta.
      this.actaService.update(this.idActa, this.datosGenerales.getDatosFormulario())
        .subscribe((acta: Acta) => {
          // Actualizamos los datos por si falla alguna de las restantes pestañas
          this.tabs.get(0).actualizarDatos(acta);
          this.tabs.get(0).warning = false;

          this.snackBarService.showSuccess('eti.acta.editar.correcto');
          this.router.navigateByUrl(`${UrlUtils.eti.root}/${UrlUtils.eti.actas}`).then();
          this.logger.debug(ActaEditarComponent.name, 'enviarDatos()', 'end');

        },
          () => {
            // Si falla mostramos el error en la pestaña
            this.tabs.get(0).mostrarError();
            this.snackBarService.showError('eti.acta.editar.error');
            this.logger.error(ActaEditarComponent.name, 'enviarDatos()', 'end');
          }
        )
    );
  }

  public setConvocatoria(idConvocatoria: number) {
    this.idConvocatoria = idConvocatoria;
  }

}
