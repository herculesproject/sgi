import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { NGXLogger } from 'ngx-logger';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@shared/config/form-group-util';

import { Subscription, Observable, of, zip } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { ServicioService } from '@core/services/servicio.service';
import { SupervisionService } from '@core/services/supervision.service';

import { Servicio } from '@core/models/servicio';
import { Supervision } from '@core/models/supervision';

import { UrlUtils } from '@core/utils/url-utils';

import { Filter, FilterType } from '@core/services/types';

import { AgrupacionServicioDatosGeneralesComponent } from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';
import { AgrupacionServicioGestorComponent } from '../agrupacion-servicio-formulario/agrupacion-servicio-gestor/agrupacion-servicio-gestor.component';


@Component({
  selector: 'app-agrupacion-servicio-actualizar',
  templateUrl: './agrupacion-servicio-actualizar.component.html',
  styleUrls: ['./agrupacion-servicio-actualizar.component.scss']
})
export class AgrupacionServicioActualizarComponent implements OnInit, OnDestroy {

  @ViewChild(AgrupacionServicioDatosGeneralesComponent, { static: true })
  datosGeneralesForm: AgrupacionServicioDatosGeneralesComponent;
  @ViewChild(AgrupacionServicioGestorComponent, { static: true }) gestorForm: AgrupacionServicioGestorComponent;

  servicio: Servicio;

  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  desactivarAceptar: boolean;

  UrlUtils = UrlUtils;

  actualizarServicioSubscription: Subscription;
  activatedRouteSubscription: Subscription;

  filter: Filter;

  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService,
    private snackBarService: SnackBarService,
    private servicioService: ServicioService,
    private supervisionService: SupervisionService,
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
      datosGenerales: this.datosGeneralesForm.createGroup(),
      gestor: this.gestorForm.createGroup()
    });

    this.getServicio();

    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * En caso de tener id en la url
   * se recupera el servicio que se va a actualizar.
   */
  getServicio(): void {
    this.logger.debug(AgrupacionServicioActualizarComponent.name, 'getServicio()', 'start');
    // Obtiene los parámetros de la url
    this.activatedRouteSubscription = this.activatedRoute.params.pipe(
      switchMap((params: Params) => {
        if (params.id) {
          const id = Number(params.id);
          return this.servicioService.findById(id);
        }
        return of(null);

      }),
      switchMap((servicio: Servicio) => {
        if (servicio) {
          this.servicio = servicio;

          this.datosGeneralesForm.setDatosForm(this.servicio);

          this.filter = {
            field: 'servicio.id',
            type: FilterType.EQUALS,
            value: servicio.id.toString(),
          };
          return this.supervisionService.findAll({
            filters: [this.filter]
          });
        } else {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('servicio.actualizar.no-encontrado')
          );
          this.router.navigateByUrl(UrlUtils.agrupacionServicios).then();
          return of(null);
        }

      })
    ).subscribe(
      (response) => {
        if (response) {
          this.gestorForm.setDatosForm(response.items);
        }
      },
      () => {
        this.snackBarService.mostrarMensajeSuccess(
          this.traductor.getTexto('servicio.actualizar.no-encontrado')
        );
        this.router.navigateByUrl(UrlUtils.agrupacionServicios).then();


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
      this.servicioService.update(this.servicio.id, this.servicio).pipe(
        switchMap(() => {
          let listObservables: Observable<any>[] = [];
          const listObservablesCrearSupervision: Observable<Supervision>[] =
            this.gestorForm.gestoresAniadidos.map(gestor => {
              const supervision = new Supervision(gestor.usuarioRef, this.servicio);
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
            this.traductor.getTexto('servicio.actualizar.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.agrupacionServicios}`).then();
          this.logger.debug(
            AgrupacionServicioActualizarComponent.name,
            'actualizarServicio()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('servicio.actualizar.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            AgrupacionServicioActualizarComponent.name,
            'actualizarServicio()',
            'end'
          );
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
    this.activatedRouteSubscription?.unsubscribe();

    this.logger.debug(AgrupacionServicioActualizarComponent.name,
      'ngOnDestroy()',
      'end');

  }
}
