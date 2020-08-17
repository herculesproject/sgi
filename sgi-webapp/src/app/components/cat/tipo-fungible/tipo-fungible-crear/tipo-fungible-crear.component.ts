import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Servicio } from '@core/models/cat/servicio';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ServicioService } from '@core/services/cat/servicio.service';
import { TipoFungibleService } from '@core/services/cat/tipo-fungible.service';
import { FormGroupUtil } from '@core/services/form-group-util';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-tipo-fungible-crear',
  templateUrl: './tipo-fungible-crear.component.html',
  styleUrls: ['./tipo-fungible-crear.component.scss'],
})
export class TipoFungibleCrearComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;

  serviciosSubscription: Subscription;
  servicioListado: Servicio[];
  filteredServicios$: Observable<Servicio[]>;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  servicioServiceAllSubscription: Subscription;
  tipoFungibleServiceCreateSubscription: Subscription;

  UrlUtils = UrlUtils;

  constructor(
    private readonly logger: NGXLogger,
    private readonly router: Router,
    private readonly tipoFungibleService: TipoFungibleService,
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
    this.logger.debug(TipoFungibleCrearComponent.name, 'ngOnInit()', 'start');
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      nombre: new FormControl('', [
        Validators.required,
        Validators.maxLength(100),
      ]),
      servicio: new FormControl('', [Validators.required]),
    });
    this.getServicios();
    this.logger.debug(TipoFungibleCrearComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Recupera el listado de los servicios
   */
  getServicios(): void {
    this.logger.debug(
      TipoFungibleCrearComponent.name,
      'getServicios()',
      'start'
    );

    this.serviciosSubscription = this.servicioService
      .findAll({})
      .subscribe((response) => {
        this.servicioListado = response.items;
        this.filteredServicios$ = this.formGroup.controls.servicio.valueChanges.pipe(
          startWith(''),
          map((value) => this._filter(value)));
        return response.items;
      });

    this.logger.debug(TipoFungibleCrearComponent.name, 'getServicios()', 'end');
  }

  getServicio(servicio: Servicio): string {
    return servicio.nombre;
  }

  /* Autocompletar */
  private _filter(nombre: string): Servicio[] {
    const filterValue = nombre.toLowerCase();
    return this.servicioListado.filter((servicio) =>
      servicio.nombre.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  enviarForm(): void {
    this.logger.debug(TipoFungibleCrearComponent.name, 'enviarForm()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(TipoFungibleCrearComponent.name, 'enviarForm()', 'end');
  }

  /**
   * Envia los datos al back para crear o actualizar un tipo fungible
   */
  private enviarApi(): void {
    this.logger.debug(TipoFungibleCrearComponent.name, 'enviarApi()', 'start');
    this.desactivarAceptar = true;
    this.tipoFungibleServiceCreateSubscription = this.tipoFungibleService
      .create(this.getDatosForm())
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('cat.tipo-fungible.crear.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.tipoFungible}`).then();
          this.logger.debug(
            TipoFungibleCrearComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('cat.tipo-fungible.crear.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            TipoFungibleCrearComponent.name,
            'enviarApi()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): TipoFungible {
    this.logger.debug(
      TipoFungibleCrearComponent.name,
      'getDatosForm()',
      'start'
    );
    const tipoFungible = new TipoFungible();
    tipoFungible.nombre = FormGroupUtil.getValue(this.formGroup, 'nombre');
    tipoFungible.servicio = FormGroupUtil.getValue(this.formGroup, 'servicio');
    tipoFungible.activo = true;
    this.logger.debug(TipoFungibleCrearComponent.name, 'getDatosForm()', 'end');
    return tipoFungible;
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleCrearComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.tipoFungibleServiceCreateSubscription?.unsubscribe();
    this.servicioServiceAllSubscription?.unsubscribe();
    this.serviciosSubscription?.unsubscribe();
    this.logger.debug(TipoFungibleCrearComponent.name, 'ngOnDestroy()', 'end');
  }
}
