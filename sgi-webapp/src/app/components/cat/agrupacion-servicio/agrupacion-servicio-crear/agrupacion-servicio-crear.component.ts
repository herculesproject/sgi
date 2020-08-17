import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Gestor } from '@core/models/cat/gestor';
import { Servicio } from '@core/models/cat/servicio';
import { Supervision } from '@core/models/cat/supervision';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ServicioService } from '@core/services/cat/servicio.service';
import { SupervisionService } from '@core/services/cat/supervision.service';
import { FormGroupUtil } from '@core/services/form-group-util';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription, zip } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import {
  AgrupacionServicioDatosGeneralesComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';
import {
  AgrupacionServicioGestorComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-gestor/agrupacion-servicio-gestor.component';



@Component({
  selector: 'app-agrupacion-servicio-crear',
  templateUrl: './agrupacion-servicio-crear.component.html',
  styleUrls: ['./agrupacion-servicio-crear.component.scss']
})
export class AgrupacionServicioCrearComponent implements OnInit, OnDestroy {
  @ViewChild(AgrupacionServicioDatosGeneralesComponent, { static: true }) datosGeneralesForm: AgrupacionServicioDatosGeneralesComponent;
  @ViewChild(AgrupacionServicioGestorComponent, { static: true }) gestorForm: AgrupacionServicioGestorComponent;

  servicio: Servicio;
  gestoresServicio: Gestor[];

  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  desactivarAceptar: boolean;

  crearServicioSubscription: Subscription;
  UrlUtils = UrlUtils;


  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService,
    private snackBarService: SnackBarService,
    private servicioService: ServicioService,
    private supervisionService: SupervisionService,
    private readonly router: Router,
    private formBuilder: FormBuilder,

  ) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }


  ngOnInit(): void {
    this.logger.debug(AgrupacionServicioCrearComponent.name, 'ngOnInit()', 'start');
    this.desactivarAceptar = false;
    this.formGroup = this.formBuilder.group({
      datosGenerales: this.datosGeneralesForm.createGroup(),
      gestores: this.gestorForm.createGroup()
    });
    this.logger.debug(AgrupacionServicioCrearComponent.name, 'ngOnInit()', 'end');
  }


  /**
   * Comprueba si el formulario de servicios es correcto.
   * De ser así creará el servicio,
   * en caso contrario se muestra mensaje de error.
   */
  enviarForm(): void {
    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'enviarForm()',
      'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.crearServicio();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'enviarForm()',
      'end');
  }


  /**
   * Se crea el servicio.
   * Si va bien redirige a la pantalla de listado de servicios,
   * en caso contrario muestra mensaje de error.
   */
  crearServicio() {
    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'crearServicio()',
      'start');

    this.desactivarAceptar = true;

    this.servicio = this.datosGeneralesForm.getDatosForm(new Servicio());

    this.crearServicioSubscription = this.servicioService.create(this.servicio).pipe(
      switchMap((servicioCreado: Servicio) => {

        let listObservables: Observable<any>[] = [];

        const listObservablesCrearSupervision: Observable<Supervision>[] =
          this.gestorForm.gestores.map(gestor => {
            const supervision = new Supervision(gestor.usuarioRef, servicioCreado);
            return this.supervisionService.create(supervision);

          });

        listObservables = listObservables.concat(listObservablesCrearSupervision);

        if (listObservables.length === 0) {
          return of([]);
        } else {
          return zip(...listObservables);
        }

      })
    ).subscribe(
      () => {
        this.snackBarService.mostrarMensajeSuccess(
          this.traductor.getTexto('cat.servicio.crear.correcto')
        );
        this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.agrupacionServicios}`).then();
        this.logger.debug(
          AgrupacionServicioCrearComponent.name,
          'crearServicio()',
          'end'
        );
      },
      () => {
        this.snackBarService.mostrarMensajeError(
          this.traductor.getTexto('cat.servicio.crear.error')
        );
        this.desactivarAceptar = false;
        this.logger.debug(
          AgrupacionServicioCrearComponent.name,
          'crearServicio()',
          'end'
        );
      }
    );

    this.desactivarAceptar = false;

    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'crearServicio()',
      'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'ngOnDestroy()',
      'start');

    this.crearServicioSubscription?.unsubscribe();

    this.logger.debug(AgrupacionServicioCrearComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
