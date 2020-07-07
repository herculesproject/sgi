import { Component, OnDestroy, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { Servicio } from '@core/models/servicio';
import { TipoFungible } from '@core/models/tipo-fungible';
import { ServicioService } from '@core/services/servicio.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoFungibleService } from '@core/services/tipo-fungible.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { FormGroupUtil } from '@shared/config/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Filter, FilterType, Direction } from '@core/services/types';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-tipo-fungible-actualizar',
  templateUrl: './tipo-fungible-actualizar.component.html',
  styleUrls: ['./tipo-fungible-actualizar.component.scss'],
})
export class TipoFungibleActualizarComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  tipoFungible: TipoFungible;


  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  tipoFungibleServiceOneSubscritpion: Subscription;
  servicioServiceAllSubscription: Subscription;
  tipoFungibleServiceUpdateSubscritpion: Subscription;

  UrlUtils = UrlUtils;

  serviciosSubscription: Subscription;
  servicioListado: Servicio[];
  filteredServicios$: Observable<Servicio[]> = of();


  @ViewChild(MatSort, { static: true }) sort: MatSort;

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;


  constructor(
    private readonly logger: NGXLogger,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly tipoFungibleService: TipoFungibleService,
    private readonly servicioService: ServicioService,
    public readonly traductor: TraductorService,
    private readonly snackBarService: SnackBarService
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
      TipoFungibleActualizarComponent.name,
      'ngOnInit()',
      'start'
    );


    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      nombre: new FormControl('', [
        Validators.required,
        Validators.maxLength(100),
      ]),
      servicio: new FormControl('', [Validators.required]),
    });

    this.getTipoFungible();
    this.getServicios();
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Obtiene los datos del tipo fungible a actualizar si existe
   */
  getTipoFungible(): void {
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'getTipoFungible()',
      'start'
    );
    this.tipoFungible = new TipoFungible();
    const id = this.activatedRoute.snapshot.params.id;
    if (id && !isNaN(id)) {
      this.tipoFungibleServiceOneSubscritpion = this.tipoFungibleService
        .findById(Number(id))
        .subscribe(
          (tipoFungible: TipoFungible) => {
            this.tipoFungible = tipoFungible;
            FormGroupUtil.setValue(
              this.formGroup,
              'nombre',
              this.tipoFungible.nombre
            );
            FormGroupUtil.setValue(
              this.formGroup,
              'servicio',
              this.tipoFungible.servicio
            );
            this.logger.debug(
              TipoFungibleActualizarComponent.name,
              'getTipoFungible()',
              'end'
            );
          },
          () => {
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto('tipo-fungible.actualizar.no-encontrado')
            );
            this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.tipoFungible}`).then();
            this.logger.debug(
              TipoFungibleActualizarComponent.name,
              'getTipoFungible()',
              'end'
            );
          }
        );
    }
  }

  /**
   * Recupera el listado de los servicios
   */
  getServicios(): void {
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
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


    this.logger.debug(
      TipoFungibleActualizarComponent.name,
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
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'enviarForm()',
      'start'
    );
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'enviarForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para actualizar un tipo fungible
   */
  private enviarApi(): void {
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'enviarApi()',
      'start'
    );
    this.getDatosForm();
    this.desactivarAceptar = true;
    this.tipoFungibleServiceUpdateSubscritpion = this.tipoFungibleService
      .update(this.tipoFungible.id, this.tipoFungible)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('tipo-fungible.actualizar.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.tipoFungible}`).then();
          this.logger.debug(
            TipoFungibleActualizarComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('tipo-fungible.actualizar.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            TipoFungibleActualizarComponent.name,
            'enviarApi()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): void {
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'getDatosForm()',
      'start'
    );
    this.tipoFungible.nombre = FormGroupUtil.getValue(this.formGroup, 'nombre');
    this.tipoFungible.servicio = new Servicio();
    this.tipoFungible.servicio = FormGroupUtil.getValue(
      this.formGroup,
      'servicio'
    );
    this.tipoFungible.activo = true;
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'getDatosForm()',
      'end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.tipoFungibleServiceOneSubscritpion?.unsubscribe();
    this.servicioServiceAllSubscription?.unsubscribe();
    this.tipoFungibleServiceUpdateSubscritpion?.unsubscribe();
    this.serviciosSubscription?.unsubscribe();
    this.logger.debug(
      TipoFungibleActualizarComponent.name,
      'ngOnDestroy()',
      'end'
    );
  }
}
