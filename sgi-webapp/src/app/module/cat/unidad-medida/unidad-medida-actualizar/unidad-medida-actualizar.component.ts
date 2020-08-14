import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { SnackBarService } from '@core/services/snack-bar.service';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { UrlUtils } from '@core/utils/url-utils';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-unidad-medida-actualizar',
  templateUrl: './unidad-medida-actualizar.component.html',
  styleUrls: ['./unidad-medida-actualizar.component.scss'],
})
export class UnidadMedidaActualizarComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  UrlUtils = UrlUtils;
  unidadMedida: UnidadMedida;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  unidadMedidaServiceGetSubscription: Subscription;
  unidadMedidaServiceUpdateSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router,
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
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.formGroup = new FormGroup({
      abreviatura: new FormControl('', [
        Validators.maxLength(10),
        Validators.minLength(2),
      ]),
      descripcion: new FormControl('', [Validators.maxLength(100)]),
    });
    this.getUnidadMedida(); // TODO usar mejor un resolver
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.unidadMedidaServiceGetSubscription?.unsubscribe();
    this.unidadMedidaServiceUpdateSubscription?.unsubscribe();
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'ngOnDestroy()',
      'end'
    );
  }

  /**
   * Obtiene los datos de la unidad de medida a actualizar si existe
   */
  getUnidadMedida(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'getUnidadMedida()',
      'start'
    );
    this.unidadMedida = new UnidadMedida();
    const id = this.activatedRoute.snapshot.params.id;
    if (id && !isNaN(id)) {
      this.unidadMedidaServiceGetSubscription = this.unidadMedidaService
        .findById(Number(id))
        .subscribe(
          (unidadMedida: UnidadMedida) => {
            this.unidadMedida = unidadMedida;
            FormGroupUtil.setValue(
              this.formGroup,
              'abreviatura',
              this.unidadMedida.abreviatura
            );
            FormGroupUtil.setValue(
              this.formGroup,
              'descripcion',
              this.unidadMedida.descripcion
            );
            this.logger.debug(
              UnidadMedidaActualizarComponent.name,
              'getUnidadMedida()',
              'start'
            );
          },
          () => {
            this.snackBarService.showSuccess('cat.unidad-medida.actualizar.no-encontrado');
            this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.unidadMedidas}`).then();
            this.logger.debug(
              UnidadMedidaActualizarComponent.name,
              'getUnidadMedida()',
              'end'
            );
          }
        );
    }
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  enviarForm(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarForm()',
      'start'
    );
    if (FormGroupUtil.valid(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.showError('form-group.error');
    }
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para crear o actualizar una unidad de medida
   */
  private enviarApi(): void {
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'enviarApi()',
      'start'
    );
    this.desactivarAceptar = true;
    this.getDatosForm();
    this.unidadMedidaServiceUpdateSubscription = this.unidadMedidaService
      .update(this.unidadMedida.id, this.unidadMedida)
      .subscribe(
        () => {
          this.snackBarService.showSuccess('cat.unidad-medida.actualizar.correcto');
          this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.unidadMedidas}`).then();
          this.logger.debug(
            UnidadMedidaActualizarComponent.name,
            'enviarApi()',
            'end'
          );
        },
        () => {
          this.snackBarService.showError('cat.unidad-medida.actualizar.error');
          this.desactivarAceptar = false;
          this.logger.debug(
            UnidadMedidaActualizarComponent.name,
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
      UnidadMedidaActualizarComponent.name,
      'getDatosForm()',
      'start'
    );
    this.unidadMedida.abreviatura = FormGroupUtil.getValue(
      this.formGroup,
      'abreviatura'
    );
    this.unidadMedida.descripcion = FormGroupUtil.getValue(
      this.formGroup,
      'descripcion'
    );
    this.logger.debug(
      UnidadMedidaActualizarComponent.name,
      'getDatosForm()',
      'end'
    );
  }
}
