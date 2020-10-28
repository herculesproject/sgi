import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');
const MSG_ERROR_INIT = marker('csp.convocatoria.plazos.enlace.error.cargar');

@Component({
  templateUrl: './convocatoria-enlace-modal.component.html',
  styleUrls: ['./convocatoria-enlace-modal.component.scss']
})
export class ConvocatoriaEnlaceModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  suscripciones: Subscription[];

  tiposEnlaceFiltered: ITipoEnlace[];
  tiposEnlace$: Observable<ITipoEnlace[]>;

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaEnlaceModalComponent>,
    private readonly tipoEnlaceService: TipoEnlaceService,
    @Inject(MAT_DIALOG_DATA) public enlace: IConvocatoriaEnlace,
  ) {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.formGroup = new FormGroup({
      url: new FormControl(this.enlace?.url),
      descripcion: new FormControl(this.enlace?.tipoEnlace?.descripcion),
      tipoEnlace: new FormControl(this.enlace?.tipoEnlace, [IsEntityValidator.isValid()]),
    });
    this.loadTiposEnlaces();
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga todos los tipos de enlace
   */
  loadTiposEnlaces() {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', 'start');
    this.suscripciones.push(
      this.tipoEnlaceService.findAll().subscribe(
        (res: SgiRestListResult<ITipoEnlace>) => {
          this.tiposEnlaceFiltered = res.items;
          this.tiposEnlace$ = this.formGroup.controls.tipoEnlace.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoEnlace(value))
            );
          this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', 'error');
        }
      )
    );
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoEnlace(value: string): ITipoEnlace[] {
    const filterValue = value.toString().toLowerCase();
    return this.tiposEnlaceFiltered.filter(tipoLazoEnlace => tipoLazoEnlace.nombre.toLowerCase().includes(filterValue));
  }

  getNombreTipoEnlace(tipoEnlace?: ITipoEnlace): string | undefined {
    return typeof tipoEnlace === 'string' ? tipoEnlace : tipoEnlace?.nombre;
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve la entidad financiadora
   *
   * @param enlace Entidad financiadora modificada
   */
  closeModal(enlace?: IConvocatoriaEnlace): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(enlace);
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'closeModal()', 'end');
  }

  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'updateComentario()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.enlace);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'updateComentario()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'start');
    this.enlace.url = FormGroupUtil.getValue(this.formGroup, 'url');
    this.enlace.descripcion = FormGroupUtil.getValue(this.formGroup, 'descripcion');
    this.enlace.tipoEnlace = FormGroupUtil.getValue(this.formGroup, 'tipoEnlace');
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'end');
  }

}
