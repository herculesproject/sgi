import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ConvocatoriaConceptoGastoActionService } from '../../convocatoria-concepto-gasto.action.service';
import { ConvocatoriaConceptoGastoDatosGeneralesFragment } from './convocatoria-concepto-gasto-datos-generales.fragment';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

const CONVOCATORIA_ELEGIBILIDAD_CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');
@Component({
  templateUrl: './convocatoria-concepto-gasto-datos-generales.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-datos-generales.component.scss']
})
export class ConvocatoriaConceptoGastoDatosGeneralesComponent
  extends FormFragmentComponent<IConvocatoriaConceptoGasto> implements OnInit, OnDestroy {
  formPart: ConvocatoriaConceptoGastoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  private subscriptions: Subscription[] = [];
  conceptoGastosFiltered: IConceptoGasto[];
  conceptoGastos$: Observable<IConceptoGasto[]>;

  textSaveOrUpdate: string;

  msgParamConceptoGastoEntity = {};

  constructor(
    private logger: NGXLogger,
    private snackBarService: SnackBarService,
    private conceptoGastoService: ConceptoGastoService,
    public readonly actionService: ConvocatoriaConceptoGastoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoDatosGeneralesFragment;
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadConceptoGastos();
    this.textSaveOrUpdate = this.fragment.getKey() ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ELEGIBILIDAD_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  loadConceptoGastos() {
    this.subscriptions.push(
      this.conceptoGastoService.findAll().subscribe(
        (res: SgiRestListResult<IConceptoGasto>) => {
          this.conceptoGastosFiltered = res.items;
          this.conceptoGastos$ = this.formGroup.controls.conceptoGasto.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroConceptoGasto(value))
            );
        },
        (error) => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(error);
        }
      )
    );
  }

  filtroConceptoGasto(value: string): IConceptoGasto[] {
    const filterValue = value.toString().toLowerCase();
    return this.conceptoGastosFiltered.filter(tipoLazoEnlace => tipoLazoEnlace.nombre.toLowerCase().includes(filterValue));
  }

  getNombreConceptoGasto(conceptoGasto?: IConceptoGasto): string | undefined {
    return typeof conceptoGasto === 'string' ? conceptoGasto : conceptoGasto?.nombre;
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

}
