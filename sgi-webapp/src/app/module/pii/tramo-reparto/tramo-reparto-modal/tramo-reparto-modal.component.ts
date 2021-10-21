import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TramoRepartoService } from '@core/services/pii/tramo-reparto/tramo-reparto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TRAMO_REPARTO_KEY = marker('pii.tramo-reparto');
const TRAMO_REPARTO_DESDE_KEY = marker('pii.tramo-reparto.desde');
const TRAMO_REPARTO_HASTA_KEY = marker('pii.tramo-reparto.hasta');
const TRAMO_REPARTO_PORCENTAJE_UNIVERSIDAD_KEY = marker('pii.tramo-reparto.porcentaje-universidad');
const TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY = marker('pii.tramo-reparto.porcentaje-inventores');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tramo-reparto-modal',
  templateUrl: './tramo-reparto-modal.component.html',
  styleUrls: ['./tramo-reparto-modal.component.scss']
})
export class TramoRepartoModalComponent extends DialogActionComponent<ITramoReparto, ITramoReparto> implements OnInit, OnDestroy {

  private readonly tramoReparto: ITramoReparto;
  msgParamDesdeEntity = {};
  msgParamHastaEntity = {};
  msgParamPorcentajeUniversidadEntity = {};
  msgParamPorcentajeInventoresEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<TramoRepartoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITramoReparto,
    private readonly tramoRepartoService: TramoRepartoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tramoReparto = { ...data };
    } else {
      this.tramoReparto = { activo: true } as ITramoReparto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITramoReparto {
    this.tramoReparto.desde = this.formGroup.controls.desde.value;
    this.tramoReparto.hasta = this.formGroup.controls.hasta.value;
    this.tramoReparto.porcentajeUniversidad = this.formGroup.controls.porcentajeUniversidad.value;
    this.tramoReparto.porcentajeInventores = this.formGroup.controls.porcentajeInventores.value;

    return this.tramoReparto;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      desde: new FormControl(this.tramoReparto?.desde ?? '', [Validators.min(0), Validators.pattern(/^[0-9]*$/)]),
      hasta: new FormControl(this.tramoReparto?.hasta ?? '', Validators.pattern(/^[0-9]*$/)),
      porcentajeUniversidad: new FormControl(
        this.tramoReparto?.porcentajeUniversidad ?? '',
        [
          Validators.min(0),
          Validators.max(100),
          NumberValidator.maxDecimalPlaces(2)
        ]
      ),
      porcentajeInventores: new FormControl(
        this.tramoReparto?.porcentajeInventores ?? '',
        [
          Validators.min(0),
          Validators.max(100),
          NumberValidator.maxDecimalPlaces(2)
        ]
      ),
    },
      [
        NumberValidator.isAfter('desde', 'hasta'),
        NumberValidator.fieldsSumEqualsToValue(100, 'porcentajeUniversidad', 'porcentajeInventores')
      ]);
    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITramoReparto> {
    const tramoReparto = this.getValue();
    return this.isEdit() ? this.tramoRepartoService.update(tramoReparto.id, tramoReparto) :
      this.tramoRepartoService.create(tramoReparto);
  }

  private setupI18N(): void {
    this.translate.get(
      TRAMO_REPARTO_DESDE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDesdeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_HASTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHastaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_PORCENTAJE_UNIVERSIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeUniversidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeInventoresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TRAMO_REPARTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TRAMO_REPARTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }
}
