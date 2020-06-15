import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from '@shared/config/form-group-util';
import { UnidadMedida } from '@core/models/unidad-medida';
import { Router } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { UnidadMedidaService } from '@core/services/unidad-medida.service';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';

@Component({
  selector: 'app-unidad-medida-agregar-actualizar',
  templateUrl: './unidad-medida-agregar-actualizar.component.html',
  styleUrls: ['./unidad-medida-agregar-actualizar.component.scss'],
})
export class UnidadMedidaAgregarActualizarComponent implements OnInit {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  private unidadMedida: UnidadMedida;

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    private readonly logger: NGXLogger,
    private readonly router: Router,
    private readonly unidadMedidaService: UnidadMedidaService,
    public readonly traductor: TraductorService
  ) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '10px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.unidadMedida = new UnidadMedida();
    this.formGroup = new FormGroup({
      abreviatura: new FormControl(this.unidadMedida.abreviatura, [
        Validators.required,
        Validators.maxLength(10),
        Validators.minLength(2),
      ]),
      descripcion: new FormControl(this.unidadMedida.descripcion, [
        Validators.maxLength(100),
      ]),
    });
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  sendForm(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendForm()',
      'start'
    );
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
      this.sendApi();
    } else {
      alert(this.traductor.getTexto('form-group.error'));
    }
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para guardar una unidad de medida
   */
  private sendApi(): void {
    this.logger.debug(
      UnidadMedidaAgregarActualizarComponent.name,
      'sendApi()',
      'start'
    );
    this.createData();
    this.desactivarAceptar = true;
    this.unidadMedidaService.create(this.unidadMedida).subscribe(
      () => {
        alert(this.traductor.getTexto('unidad-medida.agregar.correcto'));
        this.router.navigateByUrl('/unidadMedida').then();
        this.logger.debug(
          UnidadMedidaAgregarActualizarComponent.name,
          'sendApi()',
          'end'
        );
      },
      () => {
        alert(this.traductor.getTexto('unidad-medida.agregar.error'));
        this.desactivarAceptar = false;
        this.logger.debug(
          UnidadMedidaAgregarActualizarComponent.name,
          'sendApi()',
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
      UnidadMedidaAgregarActualizarComponent.name,
      'createData()',
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
      UnidadMedidaAgregarActualizarComponent.name,
      'createData()',
      'end'
    );
  }
}
