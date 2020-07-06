import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';

import {Subscription} from 'rxjs';

import {NGXLogger} from 'ngx-logger';

import {FxFlexProperties} from '@core/models/flexLayout/fx-flex-properties';
import {FxLayoutProperties} from '@core/models/flexLayout/fx-layout-properties';

import {Servicio} from '@core/models/servicio';

import {UrlUtils} from '@core/utils/url-utils';

import {TraductorService} from '@core/services/traductor.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {ServicioService} from '@core/services/servicio.service';

import {FormGroupUtil} from '@shared/config/form-group-util';

import {AgrupacionServicioDatosGeneralesComponent} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';

@Component({
  selector: 'app-agrupacion-servicio-crear',
  templateUrl: './agrupacion-servicio-crear.component.html',
  styleUrls: ['./agrupacion-servicio-crear.component.scss']
})
export class AgrupacionServicioCrearComponent implements OnInit, OnDestroy {
  @ViewChild(AgrupacionServicioDatosGeneralesComponent, { static: true }) datosGeneralesForm: AgrupacionServicioDatosGeneralesComponent;

  servicio: Servicio;

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
    private readonly router: Router,
    private formBuilder: FormBuilder
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
      datosGenerales: this.datosGeneralesForm.createGroup()
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
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
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

    this.crearServicioSubscription = this.servicioService.create(this.servicio).subscribe(
      () => {
        this.snackBarService.mostrarMensajeSuccess(
          this.traductor.getTexto('servicio.crear.correcto')
        );
        this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.agrupacionServicios}`).then();
        this.logger.debug(
          AgrupacionServicioCrearComponent.name,
          'crearServicio()',
          'end'
        );
      },
      () => {
        this.snackBarService.mostrarMensajeError(
          this.traductor.getTexto('servicio.crear.error')
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
