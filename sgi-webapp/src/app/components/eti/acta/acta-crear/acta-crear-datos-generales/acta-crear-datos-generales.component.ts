import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';

import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { FormGroupUtil } from '@core/services/form-group-util';

;
import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';

import { HoraValidador } from '@core/validators/hora-validator';
import { MinutoValidador } from '@core/validators/minuto-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';

import { SgiRestListResult } from '@sgi/framework/http'

import { ActaService } from '@core/services/eti/acta.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';

import { Acta } from '@core/models/eti/acta';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';

@Component({
  selector: 'app-acta-crear-datos-generales',
  templateUrl: './acta-crear-datos-generales.component.html',
  styleUrls: ['./acta-crear-datos-generales.component.scss']
})
export class ActaCrearDatosGeneralesComponent extends AbstractTabComponent<Acta> implements OnInit {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  convocatoriasReunion: ConvocatoriaReunion[];
  acta: Acta;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly actaService: ActaService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly traductor: TraductorService,
    private readonly snackBarService: SnackBarService
  ) {
    super(logger);
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
    super.ngOnInit();
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    this.convocatoriasReunion = [];
    this.acta = new Acta();
    this.subscripciones.push(
      this.convocatoriaReunionService.findAll().subscribe(
        (res: SgiRestListResult<ConvocatoriaReunion>) => {
          this.convocatoriasReunion = res.items;
          this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'ngOnInit()', 'end');
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('eti.acta.crear.datosGenerales.convocatoriaReunion.error')
          );
          this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'ngOnInit()', 'end');
        }
      )
    );
  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      convocatoriaReunion: new FormControl(this.datosFormulario.convocatoriaReunion, [new NullIdValidador().isValid()]),
      horaInicio: new FormControl(this.datosFormulario.horaInicio, [new HoraValidador().isValid()]),
      minutoInicio: new FormControl(this.datosFormulario.minutoInicio, [new MinutoValidador().isValid()]),
      horaFin: new FormControl(this.datosFormulario.horaFin, [new HoraValidador().isValid()]),
      minutoFin: new FormControl(this.datosFormulario.minutoFin, [new MinutoValidador().isValid()]),
      resumen: new FormControl(this.datosFormulario.resumen, [Validators.required]),
    });
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<Acta> {
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'crearObservable()', 'start');
    const observable = this.actaService.create(this.getDatosFormulario());
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosIniciales(): Acta {
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'getDatosIniciales()', 'start');
    const datos = new Acta();
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'getDatosIniciales()', 'end');
    return datos;
  }

  getDatosFormulario(): Acta {
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'getDatosIntroducidos()', 'start');
    const acta = this.datosFormulario;
    acta.convocatoriaReunion = FormGroupUtil.getValue(this.formGroup, 'convocatoriaReunion');
    acta.horaInicio = FormGroupUtil.getValue(this.formGroup, 'horaInicio');
    acta.minutoInicio = FormGroupUtil.getValue(this.formGroup, 'minutoInicio');
    acta.horaFin = FormGroupUtil.getValue(this.formGroup, 'horaFin');
    acta.minutoFin = FormGroupUtil.getValue(this.formGroup, 'minutoFin');
    acta.resumen = FormGroupUtil.getValue(this.formGroup, 'resumen');
    this.logger.debug(ActaCrearDatosGeneralesComponent.name, 'getDatosIntroducidos()', 'end');
    return acta;
  }
}
