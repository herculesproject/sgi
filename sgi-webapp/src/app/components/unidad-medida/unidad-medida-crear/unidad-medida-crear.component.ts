import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { UnidadMedida } from '@core/models/unidad-medida';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { UnidadMedidaService } from '@core/services/unidad-medida.service';
import { UrlUtils } from '@core/utils/url-utils';
import { FormGroupUtil } from '@shared/config/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-unidad-medida-crear',
  templateUrl: './unidad-medida-crear.component.html',
  styleUrls: ['./unidad-medida-crear.component.scss'],
})
export class UnidadMedidaCrearComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  unidadMedidaServiceCreateSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly router: Router,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly traductor: TraductorService,
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
    this.logger.debug(UnidadMedidaCrearComponent.name, 'ngOnInit()', 'start');
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      abreviatura: new FormControl('', [
        Validators.maxLength(10),
        Validators.minLength(2),
      ]),
      descripcion: new FormControl('', [Validators.maxLength(100)]),
    });
    this.logger.debug(UnidadMedidaCrearComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(
      UnidadMedidaCrearComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.unidadMedidaServiceCreateSubscription?.unsubscribe();
    this.logger.debug(UnidadMedidaCrearComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  enviarForm(): void {
    this.logger.debug(UnidadMedidaCrearComponent.name, 'enviarForm()', 'start');
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(UnidadMedidaCrearComponent.name, 'enviarForm()', 'end');
  }

  /**
   * Envia los datos al back para agregar o actualizar una unidad de medida
   */
  private enviarApi(): void {
    this.logger.debug(UnidadMedidaCrearComponent.name, 'enviarApi()', 'start');
    this.desactivarAceptar = true;
    this.unidadMedidaServiceCreateSubscription = this.unidadMedidaService
      .create(this.getDatosForm())
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('unidad-medida.agregar.correcto')
          );
          this.router.navigateByUrl(UrlUtils.unidadMedida).then();
          this.logger.debug(
            UnidadMedidaCrearComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('unidad-medida.agregar.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            UnidadMedidaCrearComponent.name,
            'enviarApi()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): UnidadMedida {
    this.logger.debug(UnidadMedidaCrearComponent.name, 'getDatosForm()', 'start');
    const unidadMedida = new UnidadMedida();
    unidadMedida.abreviatura = FormGroupUtil.getValue(
      this.formGroup,
      'abreviatura'
    );
    unidadMedida.descripcion = FormGroupUtil.getValue(
      this.formGroup,
      'descripcion'
    );
    this.logger.debug(UnidadMedidaCrearComponent.name, 'getDatosForm()', 'end');
    return unidadMedida;
  }
}
