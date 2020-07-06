import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {FxFlexProperties} from '@core/models/flexLayout/fx-flex-properties';
import {FxLayoutProperties} from '@core/models/flexLayout/fx-layout-properties';
import {Servicio} from '@core/models/servicio';
import {ServicioService} from '@core/services/servicio.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TraductorService} from '@core/services/traductor.service';
import {UrlUtils} from '@core/utils/url-utils';
import {FormGroupUtil} from '@shared/config/form-group-util';
import {NGXLogger} from 'ngx-logger';
import {Subscription} from 'rxjs';

import {AgrupacionServicioDatosGeneralesComponent} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';


@Component({
  selector: 'app-agrupacion-servicio-actualizar',
  templateUrl: './agrupacion-servicio-actualizar.component.html',
  styleUrls: ['./agrupacion-servicio-actualizar.component.scss']
})
export class AgrupacionServicioActualizarComponent implements OnInit, OnDestroy {

  @ViewChild(AgrupacionServicioDatosGeneralesComponent, {static: true})
  datosGeneralesForm: AgrupacionServicioDatosGeneralesComponent;

  servicio: Servicio;

  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  desactivarAceptar: boolean;

  actualizarServicioSubscription: Subscription;
  UrlUtils = UrlUtils;

  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService,
    private snackBarService: SnackBarService,
    private servicioService: ServicioService,
    private readonly router: Router,
    private activatedRoute: ActivatedRoute,
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
    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'ngOnInit()', 'start');

    this.desactivarAceptar = false;
    this.formGroup = this.formBuilder.group({
      datosGenerales: this.datosGeneralesForm.createGroup()
    });

    this.getServicio();

    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * En caso de tener id en la url
   * se recupera el servicio que se va a actualizar.
   */
  getServicio() {
    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'getServicio()', 'start');
    // Obtiene los parámetros de la url
    this.activatedRoute.params.subscribe((params: Params) => {
      // Combrueba que exista el parámetro id
      if (params.id) {
        const id = Number(params.id);
        // Obtiene los datos del back
        this.servicioService.getOne(id).subscribe(
          (servicio: Servicio) => {
            this.servicio = servicio;

            this.datosGeneralesForm.setDatosForm(this.servicio);

          },
          () => {
            alert(
              this.traductor.getTexto('servicio.actualizar.no-encontrado')
            );
            this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.agrupacionServicios}`).then();
          }
        );
      }
    });

    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'getServicio()', 'end');
  }

  /**
   * Comprueba si el formulario de servicios es correcto.
   * De ser así actualizará el servicio,
   * en caso contrario se muestra mensaje de error.
   */
  enviarForm(): void {
    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'enviarForm()',
      'start');
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.actualizarServicio();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'enviarForm()',
      'end');
  }


  /**
   * Se actualiza el servicio con los datos del formulario.
   * Si va bien se redirige al listado de agrupación servicio,
   * en caso contrario se muestra mensaje de error.
   */
  actualizarServicio(): void {
    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'actualizarServicio()',
      'start');

    this.desactivarAceptar = true;

    this.servicio = this.datosGeneralesForm.getDatosForm(this.servicio);

    this.actualizarServicioSubscription =
      this.servicioService.update(this.servicio, this.servicio.id)
        .subscribe(() => {
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto('servicio.actualizar.correcto')
            );
            this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.agrupacionServicios}`).then();
            this.logger.debug(AgrupacionServicioActualizarComponent.name,
              'actualizarServicio()',
              'end');
          },
          () => {
            this.snackBarService.mostrarMensajeError(
              this.traductor.getTexto('servicio.actualizar.error')
            );
            this.desactivarAceptar = false;
            this.logger.debug(AgrupacionServicioActualizarComponent.name,
              'actualizarServicio()',
              'end');
          }
        );

    this.desactivarAceptar = false;

    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'actualizarGuardarServicio()',
      'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'ngOnDestroy()',
      'start');
    this.actualizarServicioSubscription?.unsubscribe();

    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'ngOnDestroy()',
      'end');

  }
}
