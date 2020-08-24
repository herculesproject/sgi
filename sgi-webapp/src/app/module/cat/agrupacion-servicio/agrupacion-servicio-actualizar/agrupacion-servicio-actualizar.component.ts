import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Servicio } from '@core/models/cat/servicio';
import { Supervision } from '@core/models/cat/supervision';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ServicioService } from '@core/services/cat/servicio.service';
import { SupervisionService } from '@core/services/cat/supervision.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription, zip } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import {
  AgrupacionServicioDatosGeneralesComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';
import {
  AgrupacionServicioGestorComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-gestor/agrupacion-servicio-gestor.component';

const MSG_ERROR_NOT_FOUND = marker('cat.servicio.actualizar.no-encontrado');
const MSG_ERROR_FORM = marker('form-group.error');
const MSG_SUCCESS = marker('cat.servicio.actualizar.correcto');
const MSG_ERROR = marker('cat.servicio.actualizar.error');

@Component({
  selector: 'sgi-agrupacion-servicio-actualizar',
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

  actualizarServicioSubscription: Subscription;
  activatedRouteSubscription: Subscription;

  filter: SgiRestFilter;

  constructor(
    private readonly logger: NGXLogger,
    private snackBarService: SnackBarService,
    private servicioService: ServicioService,
    private supervisionService: SupervisionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
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
    this.activatedRouteSubscription = this.route.params.pipe(
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
            type: SgiRestFilterType.EQUALS,
            value: servicio.id.toString(),
          };
          return this.supervisionService.findAll({
            filters: [this.filter]
          });
        } else {
          this.snackBarService.showError(MSG_ERROR_NOT_FOUND);
          this.router.navigate(['../'], { relativeTo: this.route });
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
        this.snackBarService.showError(MSG_ERROR_NOT_FOUND);
        this.router.navigate(['../'], { relativeTo: this.route });
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
    if (FormGroupUtil.valid(this.formGroup)) {
      this.actualizarServicio();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
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
          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(
            AgrupacionServicioActualizarComponent.name,
            'actualizarServicio()',
            'end'
          );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
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
