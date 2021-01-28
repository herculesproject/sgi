import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import moment from 'moment';


interface CodigoEconomicoInfo {
  codigoEconomicoRef: string;
  fechaInicio: string;
  fechaFin: string;
}

interface ConceptoGastoInfo {
  id: number;
  nombre: string;
  mesInicio: string;
  mesFin: string;
  importeMaximo: string;
  codigosEconomicos: CodigoEconomicoInfo[];
}


export interface SolicitudProyectoPresupuestoDataModal {
  solicitudProyectoPresupuesto: ISolicitudProyectoPresupuesto;
  convocatoriaId: number;
  readonly: boolean;
}

const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

@Component({
  selector: 'sgi-desglose-presupuesto-global-modal',
  templateUrl: './desglose-presupuesto-global-modal.component.html',
  styleUrls: ['./desglose-presupuesto-global-modal.component.scss']
})
export class DesglosePresupuestoGlobalModalComponent extends
  BaseModalComponent<ISolicitudProyectoPresupuesto, SolicitudProyectoPresupuestoDataModal> implements OnInit {

  conceptosGasto$: Observable<IConceptoGasto[]>;
  private conceptosGastoCodigoEcPermitidos: IConvocatoriaConceptoGastoCodigoEc[];
  private conceptosGastoCodigoEcNoPermitidos: IConvocatoriaConceptoGastoCodigoEc[];

  conceptosGastosPermitidos: ConceptoGastoInfo[];
  conceptosGastosNoPermitidos: ConceptoGastoInfo[];
  showCodigosEconomicosInfo = false;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesCodigosEconomicos: FxFlexProperties;
  textSaveOrUpdate: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoPresupuestoDataModal>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoPresupuestoDataModal,
    private conceptoGastoService: ConceptoGastoService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(logger, snackBarService, matDialogRef, data.solicitudProyectoPresupuesto);
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'constructor()', 'start');

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesCodigosEconomicos = new FxFlexProperties();
    this.fxFlexPropertiesCodigosEconomicos.sm = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.md = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.gtMd = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';


    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadConceptosGasto();
    this.loadConvocatoriaConceptoGastoCodigoEcPermitidos(this.data.convocatoriaId);
    this.loadConvocatoriaConceptoGastoCodigoEcNoPermitidos(this.data.convocatoriaId);

    this.formGroup.controls.conceptoGasto.valueChanges
      .subscribe((conceptoGasto) => {
        this.showCodigosEconomicosInfo = true;

        this.conceptosGastosPermitidos = this.toConceptoGastoInfo(this.conceptosGastoCodigoEcPermitidos
          .filter(codigoEconomico => conceptoGasto.id === codigoEconomico.convocatoriaConceptoGasto.conceptoGasto.id));

        this.conceptosGastosNoPermitidos = this.toConceptoGastoInfo(this.conceptosGastoCodigoEcNoPermitidos
          .filter(codigoEconomico => conceptoGasto.id === codigoEconomico.convocatoriaConceptoGasto.conceptoGasto.id));

        this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'ngOnInit()', 'end');
      });

    this.textSaveOrUpdate = this.data.solicitudProyectoPresupuesto?.conceptoGasto ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'ngOnInit()', 'end');
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup({
      conceptoGasto: new FormControl(this.data.solicitudProyectoPresupuesto.conceptoGasto,
        [
          Validators.required
        ]),
      anualidad: new FormControl(this.data.solicitudProyectoPresupuesto.anualidad,
        [
          Validators.min(0),
          Validators.max(2_147_483_647)
        ]),
      importeSolicitado: new FormControl(this.data.solicitudProyectoPresupuesto.importeSolicitado,
        [
          Validators.required,
          Validators.min(0),
          Validators.max(2_147_483_647)
        ]),
      observaciones: new FormControl(this.data.solicitudProyectoPresupuesto.observaciones,
        [
          Validators.maxLength(2000)
        ])
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): ISolicitudProyectoPresupuesto {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `getDatosForm()`, 'start');
    const entidad = this.data.solicitudProyectoPresupuesto;
    entidad.conceptoGasto = this.formGroup.controls.conceptoGasto.value;
    entidad.anualidad = this.formGroup.controls.anualidad.value;
    entidad.importeSolicitado = this.formGroup.controls.importeSolicitado.value;
    entidad.observaciones = this.formGroup.controls.observaciones.value;
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `getDatosForm()`, 'end');
    return entidad;
  }

  /**
   * Carga todos los conceptos de gasto
   */
  private loadConceptosGasto(): void {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConceptosGasto()`, 'start');
    this.conceptosGasto$ = this.conceptoGastoService.findAll()
      .pipe(
        map((conceptosGasto) => conceptosGasto.items)
      );
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConceptosGasto()`, 'end');
  }

  /**
   * Carga todos los ConceptoGastoCodigoEc permitidos de la convocatoria
   *
   * @param convocatoriaId Id de la convocatoria
   */
  private loadConvocatoriaConceptoGastoCodigoEcPermitidos(convocatoriaId: number): void {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConvocatoriaConceptoGastoCodigoEcPermitidos(${convocatoriaId})`, 'start');
    const subscription = this.convocatoriaService.getConvocatoriaConceptoGastoCodigoEcsPermitidos(convocatoriaId)
      .subscribe((conceptosGastoCodigoEc) => {
        this.conceptosGastoCodigoEcPermitidos = conceptosGastoCodigoEc.items;
      });
    this.subscriptions.push(subscription);
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConvocatoriaConceptoGastoCodigoEcPermitidos(${convocatoriaId})`, 'end');
  }

  /**
   * Carga todos los ConceptoGastoCodigoEc no permitidos de la convocatoria
   * 
   * @param convocatoriaId Id de la convocatoria
   */
  private loadConvocatoriaConceptoGastoCodigoEcNoPermitidos(convocatoriaId: number): void {
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConvocatoriaConceptoGastoCodigoEcNoPermitidos(${convocatoriaId})`, 'start');
    const subscription = this.convocatoriaService.getConvocatoriaConceptoGastoCodigoEcsNoPermitidos(convocatoriaId)
      .subscribe((conceptosGastoCodigoEc) => {
        this.conceptosGastoCodigoEcNoPermitidos = conceptosGastoCodigoEc.items;
      });
    this.subscriptions.push(subscription);
    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, `loadConvocatoriaConceptoGastoCodigoEcNoPermitidos(${convocatoriaId})`, 'end');
  }

  getNombreConceptoGasto(conceptoGasto: IConceptoGasto): string {
    return conceptoGasto?.nombre;
  }

  private toConceptoGastoInfo(convocatoriaConceptoGastos: IConvocatoriaConceptoGastoCodigoEc[]): ConceptoGastoInfo[] {
    const conceptosGastosInfo: ConceptoGastoInfo[] = [];
    convocatoriaConceptoGastos.forEach(codigoEconomico => {
      let conceptoGastoInfo = conceptosGastosInfo.find(c => c.id === codigoEconomico.convocatoriaConceptoGasto.id);

      if (!conceptoGastoInfo) {
        conceptoGastoInfo = {
          id: codigoEconomico.convocatoriaConceptoGasto.id,
          nombre: codigoEconomico.convocatoriaConceptoGasto.conceptoGasto.nombre,
          importeMaximo: codigoEconomico.convocatoriaConceptoGasto?.importeMaximo?.toString(),
          mesInicio: '',
          mesFin: '', // TODO: recuperar cuando se haga el cambio en ConvocatoriaConceptoGasto
          codigosEconomicos: []
        }

        conceptosGastosInfo.push(conceptoGastoInfo);
      }

      const codigoEconomicoInfo: CodigoEconomicoInfo = {
        codigoEconomicoRef: codigoEconomico.codigoEconomicoRef,
        fechaInicio: moment(codigoEconomico.fechaInicio).format('DD/MM/yyyy'),
        fechaFin: moment(codigoEconomico.fechaFin).format('DD/MM/yyyy')
      }

      conceptoGastoInfo.codigosEconomicos.push(codigoEconomicoInfo);
    });

    this.logger.debug(DesglosePresupuestoGlobalModalComponent.name, 'ngOnInit()', 'end');

    return conceptosGastosInfo;
  }

}
