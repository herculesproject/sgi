import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';

const INVENCION_TITULO_KEY = marker('pii.invencion.titulo');
const INVENCION_FECHACOMUNICACION_KEY = marker('pii.invencion.fecha-comunicacion');
const INVENCION_DESCRIPCION_KEY = marker('pii.invencion.descripcion');
const INVENCION_TIPOPROTECCION_KEY = marker('pii.invencion.tipo-proteccion');
const INVENCION_COMENTARIOS_KEY = marker('pii.invencion.comentarios');

@Component({
  selector: 'sgi-invencion-datos-generales',
  templateUrl: './invencion-datos-generales.component.html',
  styleUrls: ['./invencion-datos-generales.component.scss']
})
export class InvencionDatosGeneralesComponent extends FormFragmentComponent<IInvencion> {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamTituloEntity = {};
  msgParamFechaComunicacionEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoProteccionEntity = {};
  msgParamComentariosEntity = {};

  readonly tiposProteccion$: Observable<ITipoProteccion[]>;
  subtiposProteccion$: Observable<ITipoProteccion[]>;

  constructor(
    readonly actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly tipoProteccionService: TipoProteccionService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';
    this.tiposProteccion$ = this.tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subtiposProteccion$ = this.formGroup.controls.tipoProteccion.valueChanges.pipe(
      tap(_ => this.resetSubtipoProteccionControl()),
      switchMap(
        ({ id }) => this.tipoProteccionService.findAll(this.createPadreIdFilter(id)).pipe(
          map(({ items }) => items)
        )));
  }

  private resetSubtipoProteccionControl(): void {
    this.formGroup.controls.subtipoProteccion.reset();
  }

  private createPadreIdFilter(padreId: number): SgiRestFindOptions {
    return {
      filter: new RSQLSgiRestFilter('padre.id', SgiRestFilterOperator.EQUALS, padreId.toString())
    };
  }

  private setupI18N(): void {
    this.translate.get(
      INVENCION_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_FECHACOMUNICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaComunicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_TIPOPROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoProteccionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_COMENTARIOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }
}
