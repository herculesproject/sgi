import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';

import { FormGroupUtil } from '@core/services/form-group-util';


import { HoraValidador } from '@core/validators/hora-validator';
import { MinutoValidador } from '@core/validators/minuto-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';


import { ActaService } from '@core/services/eti/acta.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';

import { Acta } from '@core/models/eti/acta';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { startWith, map } from 'rxjs/operators';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';


@Component({
  selector: 'app-acta-datos-generales',
  templateUrl: './acta-datos-generales.component.html',
  styleUrls: ['./acta-datos-generales.component.scss']
})
export class ActaDatosGeneralesComponent extends AbstractTabComponent<Acta> implements OnInit {

  constructor(
    protected readonly logger: NGXLogger,
    private readonly actaService: ActaService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly traductor: TraductorService,
    private readonly snackBarService: SnackBarService,
  ) {
    super(logger);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  convocatoriasReunionFitlered: ConvocatoriaReunion[];
  convocatoriasReunion: Observable<ConvocatoriaReunion[]>;

  acta: Acta;

  @Output()
  selectConvocatoria: EventEmitter<number> = new EventEmitter();


  /**
   * Compara dos convocatorias reunión por su código.
   *
   * @param elemento objeto del combo con el que se hace la comparacion
   * @param valor valor que se quiere comparar con los elementos del combo
   * @return true si tienen el mismo id o false en caso contrario
   */
  comparacionObjetosComboPorId = ((elemento: any, valor: any): boolean => elemento && valor && elemento.id === valor.id);

  ngOnInit() {
    super.ngOnInit();
    this.logger.debug(ActaDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    this.acta = new Acta();
    this.subscripciones.push(
      this.convocatoriaReunionService.findAll().subscribe(
        (res: SgiRestListResult<ConvocatoriaReunion>) => {
          this.convocatoriasReunionFitlered = res.items;
          this.convocatoriasReunion = this.formGroup.get('convocatoriaReunion').valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtro(value))
            );
          this.logger.debug(ActaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('eti.acta.crear.datosGenerales.convocatoriaReunion.error')
          );
          this.logger.debug(ActaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
        }
      )
    );
  }


  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtro(value: string): ConvocatoriaReunion[] {
    const filterValue = value.toString().toLowerCase();
    return this.convocatoriasReunionFitlered.filter(convocatoriaReunion => convocatoriaReunion.codigo.toLowerCase().includes(filterValue));
  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ActaDatosGeneralesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      convocatoriaReunion: new FormControl('', [new NullIdValidador().isValid()]),
      horaInicio: new FormControl('', [new HoraValidador().isValid()]),
      minutoInicio: new FormControl('', [new MinutoValidador().isValid()]),
      horaFin: new FormControl('', [new HoraValidador().isValid()]),
      minutoFin: new FormControl('', [new MinutoValidador().isValid()]),
      resumen: new FormControl('', [Validators.required]),
    });
    this.logger.debug(ActaDatosGeneralesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }


  /**
   * Recupera los datos del formulario.
   * @returns Acta
   */
  getDatosFormulario(): Acta {
    this.logger.debug(ActaDatosGeneralesComponent.name, 'getDatosFormulario()', 'start');
    const acta = this.datosFormulario;
    acta.convocatoriaReunion = FormGroupUtil.getValue(this.formGroup, 'convocatoriaReunion');
    acta.horaInicio = FormGroupUtil.getValue(this.formGroup, 'horaInicio');
    acta.minutoInicio = FormGroupUtil.getValue(this.formGroup, 'minutoInicio');
    acta.horaFin = FormGroupUtil.getValue(this.formGroup, 'horaFin');
    acta.minutoFin = FormGroupUtil.getValue(this.formGroup, 'minutoFin');
    acta.resumen = FormGroupUtil.getValue(this.formGroup, 'resumen');
    acta.numero = acta.convocatoriaReunion.numeroActa;

    this.logger.debug(ActaDatosGeneralesComponent.name, 'getDatosFormulario()', 'end');
    return acta;
  }


  /**
   * Devuelve el código de una convocatoria reunión
   * @param convocatoria convocatoria
   * @returns código de una convocatoria reunión
   */
  getConvocatoria(convocatoriaReunion?: ConvocatoriaReunion): string | undefined {

    return convocatoriaReunion?.codigo;
  }


  /**
   * Setea los datos en el formulario.
   * @params Acta
   */
  setDatosFormulario(acta: Acta) {
    this.logger.debug(ActaDatosGeneralesComponent.name, 'setDatosFormulario()', 'start');

    const convocatoriaReunion: ConvocatoriaReunion = acta.convocatoriaReunion;
    convocatoriaReunion.codigo = `ACTA${acta.numero}/${acta.convocatoriaReunion.anio}/${acta.convocatoriaReunion.comite.comite}`;

    this.formGroup.controls.convocatoriaReunion.setValue(convocatoriaReunion);
    this.formGroup.controls.horaInicio.setValue(acta.horaInicio);
    this.formGroup.controls.minutoInicio.setValue(acta.minutoInicio);
    this.formGroup.controls.horaFin.setValue(acta.horaFin);
    this.formGroup.controls.minutoFin.setValue(acta.minutoFin);
    this.formGroup.controls.resumen.setValue(acta.resumen);

    this.logger.debug(ActaDatosGeneralesComponent.name, 'setDatosFormulario()', 'end');
  }


  /**
   * Registra el evento de modificación de convocatoria reunión.
   * @param convocatoriaReunion  convocatoria reunión seleccionada
   */
  selectConvocatoriaReunion(convocatoriaReunion: ConvocatoriaReunion | string) {
    this.logger.debug(ActaDatosGeneralesComponent.name, 'selectConvocatoriaReunion(convocatoriaReunion: ConvocatoriaReunion)', 'start');
    this.selectConvocatoria.emit(convocatoriaReunion ? (convocatoriaReunion as ConvocatoriaReunion).id : null);
    this.logger.debug(ActaDatosGeneralesComponent.name, 'selectConvocatoriaReunion(convocatoriaReunion: ConvocatoriaReunion)', 'end');
  }

}
