import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from '@shared/config/form-group-util';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { TipoFungibleService } from '@core/services/tipo-fungible.service';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { UrlUtils } from '@core/utils/url-utils';
import { TipoFungible } from '@core/models/tipo-fungible';
import { Servicio } from '@core/models/servicio';
import { ServicioService } from '@core/services/servicio.service';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SnackBarService } from '@core/services/snack-bar.service';

@Component({
  selector: 'app-tipo-fungible-agregar-actualizar',
  templateUrl: './tipo-fungible-agregar-actualizar.component.html',
  styleUrls: ['./tipo-fungible-agregar-actualizar.component.scss']
})
export class TipoFungibleAgregarActualizarComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  tipoFungible: TipoFungible;
  servicioListado: Servicio[];
  filteredServicios: Observable<Servicio[]>;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  activatedRouteSubscription: Subscription;
  tipoFungibleServiceOneSubscritpion: Subscription;
  servicioServiceAllSubscription: Subscription;
  tipoFungibleServiceCreateSubscription: Subscription;
  tipoFungibleServiceUpdateSubscritpion: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
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
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      nombre: new FormControl('', [
        Validators.required,
        Validators.maxLength(100),
      ]),
      servicio: new FormControl('', [
        Validators.required,
      ]),
    });
    this.getTipoFungible();
    this.getServicios();
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Obtiene los datos del tipo fungible a actualizar si existe
   */
  getTipoFungible(): void {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'getTipoFungible()', 'start');

    this.tipoFungible = new TipoFungible();
    // Obtiene los parámetros de la url
    this.activatedRouteSubscription = this.activatedRoute.params.subscribe((params: Params) => {
      // Combrueba que exista el parámetro id
      if (params.id) {
        const id = Number(params.id);
        // Obtiene los datos del back
        this.tipoFungibleServiceOneSubscritpion = this.tipoFungibleService.getOne(id).subscribe(
          (tipoFungible: TipoFungible) => {
            this.tipoFungible = tipoFungible;
            // Actualiza el formGroup
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
          },
          () => {
            // Añadimos esta comprobación para que no nos eche al agregar uno nuevo
            if (id) {
              this.snackBarService.mostrarMensajeSuccess(
                this.traductor.getTexto('tipo-fungible.actualizar.no-encontrado')
              );
              this.router.navigateByUrl(UrlUtils.tipoFungible).then();
            }
            this.logger.debug(
              TipoFungibleAgregarActualizarComponent.name,
              'getTipoFungible()',
              'end'
            );
          }
        );
      }
    });

    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'getTipoFungible()', 'end');
  }

  /**
   * Recupera el listado de los servicios
   */

  getServicios(): void {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'getServicios()', 'start');

    this.servicioServiceAllSubscription = this.servicioService.findAll().subscribe(
      (servicioListado: Servicio[]) => {
        this.servicioListado = servicioListado;

        this.filteredServicios = this.formGroup.controls.servicio.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filter(value))
          );
      });

    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'getServicios()', 'end');
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
      TipoFungibleAgregarActualizarComponent.name,
      'sendForm()',
      'start'
    );
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.sendApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'sendForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para agregar o actualizar un tipo fungible
   */
  private sendApi(): void {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'sendApi()',
      'start'
    );
    this.createData();
    this.desactivarAceptar = true;

    // Si no tiene id, lo creamos
    if (this.tipoFungible.id === null) {
      this.agregarTipoFungible();
    }
    // Si tiene id, lo actualizamos
    else {
      this.actualizarTipoFungible();
    }
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'sendApi()',
      'end'
    );
  }

  /**
   * Crea un nuevo tipo fungible en el back
   */
  private agregarTipoFungible() {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'agregarTipoFungible()',
      'start'
    );
    this.tipoFungibleServiceCreateSubscription = this.tipoFungibleService.create(this.tipoFungible).subscribe(
      () => {
        this.snackBarService.mostrarMensajeSuccess(
          this.traductor.getTexto('tipo-fungible.agregar.correcto')
        );
        this.router.navigateByUrl(UrlUtils.tipoFungible).then();
        this.logger.debug(
          TipoFungibleAgregarActualizarComponent.name,
          'agregarTipoFungible()',
          'end'
        );
      },
      () => {
        this.snackBarService.mostrarMensajeError(
          this.traductor.getTexto('tipo-fungible.agregar.error')
        );
        this.desactivarAceptar = false;
        this.logger.debug(
          TipoFungibleAgregarActualizarComponent.name,
          'agregarTipoFungible()',
          'end'
        );
      }
    );
  }

  /**
   * Actualiza un tipo fungible existente en el back
   */
  private actualizarTipoFungible() {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'actualizarTipoFungible()',
      'start'
    );
    this.tipoFungibleServiceUpdateSubscritpion = this.tipoFungibleService
      .update(this.tipoFungible, this.tipoFungible.id)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('tipo-fungible.actualizar.correcto')
          );
          this.router.navigateByUrl(UrlUtils.tipoFungible).then();
          this.logger.debug(
            TipoFungibleAgregarActualizarComponent.name,
            'actualizarTipoFungible()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('tipo-fungible.actualizar.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            TipoFungibleAgregarActualizarComponent.name,
            'actualizarTipoFungible()',
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
      TipoFungibleAgregarActualizarComponent.name,
      'createData()',
      'start'
    );
    this.tipoFungible.nombre = FormGroupUtil.getValue(
      this.formGroup,
      'nombre'
    );
    this.tipoFungible.servicio = new Servicio();
    this.tipoFungible.servicio = FormGroupUtil.getValue(
      this.formGroup,
      'servicio'
    );

    this.tipoFungible.activo = true;

    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'createData()',
      'end'
    );
  }
  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'ngOnDestroy()', 'start');

    this.activatedRouteSubscription?.unsubscribe();
    this.tipoFungibleServiceUpdateSubscritpion?.unsubscribe();
    this.tipoFungibleServiceOneSubscritpion?.unsubscribe();
    this.tipoFungibleServiceCreateSubscription?.unsubscribe();
    this.servicioServiceAllSubscription?.unsubscribe();

    this.logger.debug(
      TipoFungibleAgregarActualizarComponent.name,
      'ngOnDestroy()', 'end');
  }

}

