import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { ICodigoEconomico } from '@core/models/sge/codigo-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CodigoEconomicoService } from '@core/services/sge/codigo-economico.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { DateValidator } from '@core/validators/date-validator';
import { SelectValidator } from '@core/validators/select-validator';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico');
const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.sge');
const CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.permitido');
const CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.no-permitido');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface IConvocatoriaConceptoGastoCodigoEcModalComponent {
  convocatoriaConceptoGastoCodigoEc: IConvocatoriaConceptoGastoCodigoEc;
  convocatoriaConceptoGastoCodigoEcsTabla: IConvocatoriaConceptoGastoCodigoEc[];
  permitido: boolean;
  editModal: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './convocatoria-concepto-gasto-codigo-ec-modal.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-codigo-ec-modal.component.scss']
})
export class ConvocatoriaConceptoGastoCodigoEcModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private suscripciones: Subscription[] = [];

  convocatoriaConceptoGastosFiltered: IConvocatoriaConceptoGasto[];
  convocatoriaConceptoGastos$: Observable<IConvocatoriaConceptoGasto[]>;

  codigosEconomicosFiltered: ICodigoEconomico[];
  codigosEconomicos$: Observable<ICodigoEconomico[]>;
  textSaveOrUpdate: string;

  msgParamEntity = {};
  msgParamCodigoEconomigoSgeEntity = {};
  title: string;

  constructor(
    private logger: NGXLogger,
    private snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ConvocatoriaConceptoGastoCodigoEcModalComponent>,
    private codigoEconomicoService: CodigoEconomicoService,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaConceptoGastoCodigoEcModalComponent,
    private readonly translate: TranslateService
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.xs = 'column';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
    this.loadCodigosEconomicos();
    this.textSaveOrUpdate = this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEconomigoSgeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef && this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef && !this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (!this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef && this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  private initFormGroup() {
    this.formGroup = new FormGroup(
      {
        codigoEconomicoRef: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.codigoEconomicoRef),
        fechaInicio: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.fechaInicio),
        fechaFin: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.fechaFin),
        observaciones: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.observaciones),
      },
      {
        validators: [
          DateValidator.isBefore('fechaFin', 'fechaInicio'),
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          this.notOverlapsSameCodigoEconomico('fechaInicio', 'fechaFin', 'codigoEconomicoRef')
        ]
      }
    );
    if (this.data.readonly) {
      this.formGroup.disable();
    }
    if (this.data.editModal) {
      this.formGroup.controls.codigoEconomicoRef.disable();
      this.formGroup.controls.observaciones.disable();
    }
  }

  private loadCodigosEconomicos() {
    const subscription = this.codigoEconomicoService.findByGastos().subscribe(
      (res: SgiRestListResult<ICodigoEconomico>) => {
        this.codigosEconomicosFiltered = res.items;
        this.formGroup.controls.codigoEconomicoRef.setValidators(SelectValidator.isSelectOption(
          this.codigosEconomicosFiltered.map(cod => cod.codigoEconomicoRef), true));
        this.codigosEconomicos$ = this.formGroup.controls.codigoEconomicoRef.valueChanges.pipe(
          startWith(''),
          map(value => this.filtroCodigoEconomico(value))
        );
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_INIT);
        this.logger.error(error);
      }
    );
    this.suscripciones.push(subscription);
  }

  filtroCodigoEconomico(value: string): ICodigoEconomico[] {
    const filterValue = value.toString().toLowerCase();
    return this.codigosEconomicosFiltered
      .filter(codigo => codigo.codigoEconomicoRef.toLowerCase().includes(filterValue));
  }

  getCodigoEconomico(codigoEconomico?: ICodigoEconomico): string {
    if (this.formGroup.controls.fechaInicio.value === null && this.formGroup.controls.fechaInicio.value === null) {
      this.formGroup.controls.fechaInicio.setValue(codigoEconomico.fechaInicio);
      this.formGroup.controls.fechaFin.setValue(codigoEconomico.fechaFin);
    }
    return codigoEconomico && typeof codigoEconomico === 'string'
      ? codigoEconomico
      : codigoEconomico?.codigoEconomicoRef;
  }

  closeModal(convocatoriaConceptoGasto?: IConvocatoriaConceptoGastoCodigoEc): void {
    this.matDialogRef.close(convocatoriaConceptoGasto);
  }

  private loadDatosForm(): void {
    const convocatoriaConceptoGastoCodigoEc = this.getDatosForm();
    this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef = convocatoriaConceptoGastoCodigoEc.codigoEconomicoRef;
    this.data.convocatoriaConceptoGastoCodigoEc.observaciones = convocatoriaConceptoGastoCodigoEc.observaciones;
    this.data.convocatoriaConceptoGastoCodigoEc.fechaInicio = convocatoriaConceptoGastoCodigoEc.fechaInicio;
    this.data.convocatoriaConceptoGastoCodigoEc.fechaFin = convocatoriaConceptoGastoCodigoEc.fechaFin;
  }

  private getDatosForm(): IConvocatoriaConceptoGastoCodigoEc {
    const convocatoriaConceptoGastoCodigoEc = {
      id: this.data.convocatoriaConceptoGastoCodigoEc.id,
      codigoEconomicoRef: this.formGroup.controls.codigoEconomicoRef.value,
      fechaInicio: this.formGroup.controls.fechaInicio.value,
      fechaFin: this.formGroup.controls.fechaFin.value,
      observaciones: this.formGroup.controls.observaciones.value
    } as IConvocatoriaConceptoGastoCodigoEc;
    return convocatoriaConceptoGastoCodigoEc;
  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.data.convocatoriaConceptoGastoCodigoEc);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  ngOnDestroy(): void {
    this.suscripciones.forEach(x => x.unsubscribe());
  }

  /**
   * Comprueba que el rango de fechas entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   * filtrada por códigos económicos
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param filterFieldName Filtro para obtener la lista de rangos con los que se quiere comprobar.
   */
  private notOverlapsSameCodigoEconomico(startRangeFieldName: string, endRangeFieldName: string, filterFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const filterFieldControl = formGroup.controls[filterFieldName];
      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];

      if (filterFieldControl.value) {
        if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlapped)
          || (finRangoControl.errors && !finRangoControl.errors.overlapped)) {
          return;
        }

        const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value.toMillis() : Number.MIN_VALUE;
        const finRangoNumber = finRangoControl.value ? finRangoControl.value.toMillis() : Number.MAX_VALUE;

        const ranges = this.data.convocatoriaConceptoGastoCodigoEcsTabla.filter(
          conceptoGasto => conceptoGasto.codigoEconomicoRef === filterFieldControl.value
        ).map(conceptoGasto => {
          return {
            inicio: conceptoGasto.fechaInicio ? conceptoGasto.fechaInicio.toMillis() : Number.MIN_VALUE,
            fin: conceptoGasto.fechaFin ? conceptoGasto.fechaFin.toMillis() : Number.MAX_VALUE
          };
        });

        if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
          inicioRangoControl.setErrors({ overlapped: true });
          inicioRangoControl.markAsTouched({ onlySelf: true });

          finRangoControl.setErrors({ overlapped: true });
          finRangoControl.markAsTouched({ onlySelf: true });
        } else {
          if (inicioRangoControl.errors) {
            delete inicioRangoControl.errors.overlapped;
            inicioRangoControl.updateValueAndValidity({ onlySelf: true });
          }

          if (finRangoControl.errors) {
            delete finRangoControl.errors.overlapped;
            finRangoControl.updateValueAndValidity({ onlySelf: true });
          }
        }
      }
    };
  }
}
