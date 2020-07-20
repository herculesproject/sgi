import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormGroupUtil } from '@core/services/form-group-util';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { TipoReservableService } from '@core/services/tipo-reservable.service';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { UrlUtils } from '@core/utils/url-utils';
import { TipoReservable } from '@core/models/tipo-reservable';
import { Servicio } from '@core/models/servicio';
import { ServicioService } from '@core/services/servicio.service';
import { Observable, Subscription } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { SnackBarService } from '@core/services/snack-bar.service';
import { EstadoTipoReservableEnum } from '@core/enums/estado-tipo-reservable-enum';

@Component({
  selector: 'app-tipo-reservable-actualizar',
  templateUrl: './tipo-reservable-actualizar.component.html',
  styleUrls: ['./tipo-reservable-actualizar.component.scss']
})
export class TipoReservableActualizarComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  tipoReservable: TipoReservable;

  servicioListado: Servicio[];
  filteredServicios$: Observable<Servicio[]>;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  activatedRouteSubscription: Subscription;
  tipoReservableServiceOneSubscription: Subscription;
  servicioServiceAllSubscription: Subscription;
  tipoReservableServiceCreateSubscription: Subscription;
  tipoReservableServiceUpdateSubscription: Subscription;

  UrlUtils = UrlUtils;

  constructor(
    private readonly logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly tipoReservableService: TipoReservableService,
    private readonly servicioService: ServicioService,
    public readonly traductor: TraductorService,
    private snackBarService: SnackBarService
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

  ngOnInit() {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      descripcion: new FormControl('', [
        Validators.required,
        Validators.maxLength(100),
      ]),
      servicio: new FormControl('', [
        Validators.required,
      ]),
      duracionMin: new FormControl('', [
        Validators.maxLength(3),
      ]),
      diasAnteMax: new FormControl('', [
        Validators.maxLength(2),
      ]),
      diasVistaMaxCalen: new FormControl('', [
        Validators.maxLength(2),
      ]),
      horasAnteAnular: new FormControl('', [
        Validators.maxLength(3),
      ]),
      horasAnteMin: new FormControl('', [
        Validators.maxLength(3),
      ]),
      reservaMulti: new FormControl('', [
      ]),
      estado: new FormControl('', [
      ]),
    });
    this.getTipoReservable();
    this.getServicios();
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Obtiene los datos del tipo reservable a actualizar si existe
   */
  getTipoReservable(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'getTipoReservable()',
      'start'
    );

    let id: number;
    // Obtiene el id
    this.activatedRoute.params
      .pipe(
        switchMap((params: Params) => {
          id = Number(params.id);
          if (id) {
            return this.tipoReservableService.findById(Number(id));
          }
        })
      )
      .subscribe(
        // Obtiene el objeto existente
        (tipoReservable: TipoReservable) => {
          this.tipoReservable = tipoReservable;
          // Actualiza el formGroup
          FormGroupUtil.setValue(
            this.formGroup,
            'descripcion',
            this.tipoReservable.descripcion
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'servicio',
            this.tipoReservable.servicio
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'duracionMin',
            this.tipoReservable.duracionMin
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'diasAnteMax',
            this.tipoReservable.diasAnteMax
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'diasVistaMaxCalen',
            this.tipoReservable.diasVistaMaxCalen
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'horasAnteAnular',
            this.tipoReservable.horasAnteAnular
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'horasAnteMin',
            this.tipoReservable.horasAnteMin
          );
          FormGroupUtil.setValue(
            this.formGroup,
            'reservaMulti',
            this.tipoReservable.reservaMulti
          );

          FormGroupUtil.setValue(
            this.formGroup,
            'estado',
            this.tipoReservable.estado
          );
        },
        // Si no encuentra
        () => {
          // Añadimos esta comprobación para que no nos eche al crear uno nuevo
          if (id) {
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto('tipo-reservable.actualizar.no-encontrado')
            );
            this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.tipoReservables}`).then();
          }
          this.logger.debug(
            TipoReservableActualizarComponent.name,
            'getTipoReservable()',
            'end'
          );
        }
      );
  }

  /**
   * Recupera el listado de los servicios
   */

  getServicios(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'getServicios()',
      'start'
    );

    this.servicioServiceAllSubscription = this.servicioService
      .findAll({})
      .subscribe((response) => {
        this.servicioListado = response.items;
        this.filteredServicios$ = this.formGroup.controls.servicio.valueChanges.pipe(
          startWith(''),
          map((value) => this._filter(value)));
        return response.items;
      });


    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'getServicios()',
      'end'
    );
  }

  getServicio(servicio: Servicio): string {
    return servicio.nombre;
  }

  /* Autocompletar */

  private _filter(nombre: string): Servicio[] {
    const filterValue = nombre.toLowerCase();

    return this.servicioListado.filter
      (servicio => servicio.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  sendForm(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'sendForm()',
      'start'
    );
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.sendApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error'));
    }
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'sendForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para actualizar un tipo reservable
   */
  private sendApi(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'sendApi()',
      'start'
    );
    this.createData();
    this.desactivarAceptar = true;
    this.actualizarTipoReservable();

    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'sendApi()',
      'end'
    );
  }

  /**
   * Actualiza un tipo reservable existente en el back
   */
  private actualizarTipoReservable() {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'actualizarTipoReservable()',
      'start'
    );
    this.tipoReservableServiceUpdateSubscription = this.tipoReservableService
      .update(this.tipoReservable.id, this.tipoReservable)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('tipo-reservable.actualizar.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.tipoReservables}`).then();
          this.logger.debug(
            TipoReservableActualizarComponent.name,
            'actualizarTipoReservable()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('tipo-reservable.actualizar.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            TipoReservableActualizarComponent.name,
            'actualizarTipoReservable()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private createData(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'createData()',
      'start'
    );

    this.tipoReservable.descripcion = FormGroupUtil.getValue(
      this.formGroup,
      'descripcion'
    );
    this.tipoReservable.servicio = new Servicio();
    this.tipoReservable.servicio = FormGroupUtil.getValue(
      this.formGroup,
      'servicio'
    );
    this.tipoReservable.duracionMin = FormGroupUtil.getValue(
      this.formGroup,
      'duracionMin'
    );
    this.tipoReservable.diasAnteMax = FormGroupUtil.getValue(
      this.formGroup,
      'diasAnteMax'
    );
    this.tipoReservable.diasVistaMaxCalen = FormGroupUtil.getValue(
      this.formGroup,
      'diasVistaMaxCalen'
    );
    this.tipoReservable.horasAnteAnular = FormGroupUtil.getValue(
      this.formGroup,
      'horasAnteAnular'
    );
    this.tipoReservable.horasAnteMin = FormGroupUtil.getValue(
      this.formGroup,
      'horasAnteMin'
    );
    this.tipoReservable.reservaMulti = FormGroupUtil.getValue(
      this.formGroup,
      'reservaMulti'
    );
    this.tipoReservable.estado = FormGroupUtil.getValue(
      this.formGroup,
      'estado'
    );

    this.tipoReservable.activo = true;

    console.log('TipoReservable: ' + JSON.stringify(this.tipoReservable));

    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'createData()',
      'end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'ngOnDestroy()',
      'start'
    );

    this.activatedRouteSubscription?.unsubscribe();
    this.tipoReservableServiceOneSubscription?.unsubscribe();
    this.servicioServiceAllSubscription?.unsubscribe();
    this.tipoReservableServiceCreateSubscription?.unsubscribe();
    this.tipoReservableServiceUpdateSubscription?.unsubscribe();

    this.logger.debug(
      TipoReservableActualizarComponent.name,
      'ngOnDestroy()',
      'end'
    );
  }

  listaEstados(): Array<string> {
    return Object.keys(EstadoTipoReservableEnum);
  }

}
