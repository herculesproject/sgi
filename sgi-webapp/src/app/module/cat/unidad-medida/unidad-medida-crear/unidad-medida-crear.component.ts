import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { SnackBarService } from '@core/services/snack-bar.service';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM = marker('form-group.error');
const MSG_SUCCESS = marker('cat.unidad-medida.crear.correcto');
const MSG_ERROR = marker('cat.unidad-medida.crear.error');

@Component({
  selector: 'sgi-unidad-medida-crear',
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
    private readonly route: ActivatedRoute,
    private readonly unidadMedidaService: UnidadMedidaService,
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
    if (FormGroupUtil.valid(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
    this.logger.debug(UnidadMedidaCrearComponent.name, 'enviarForm()', 'end');
  }

  /**
   * Envia los datos al back para crear o actualizar una unidad de medida
   */
  private enviarApi(): void {
    this.logger.debug(UnidadMedidaCrearComponent.name, 'enviarApi()', 'start');
    this.desactivarAceptar = true;
    this.unidadMedidaServiceCreateSubscription = this.unidadMedidaService
      .create(this.getDatosForm())
      .subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(
            UnidadMedidaCrearComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
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
