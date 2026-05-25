import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { TipoConfidencialidadService } from '@core/services/csp/tipo-confidencialidad/tipo-confidencialidad.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@herculesproject/framework/http';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoConfidencialidadModalComponent } from '../tipo-confidencialidad-modal/tipo-confidencialidad-modal.component';

const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const TIPO_CONFIDENCIALIDAD_KEY = marker('csp.tipo-confidencialidad');

@Component({
  templateUrl: './tipo-confidencialidad-listado.component.html',
  styleUrls: ['./tipo-confidencialidad-listado.component.scss']
})
export class TipoConfidencialidadListadoComponent extends AbstractTablePaginationComponent<ITipoConfidencialidad> implements OnInit {

  tiposConfidencialidad$: Observable<ITipoConfidencialidad[]>;

  msgParamEntity = {};

  textoCrearSuccess: string;
  textoUpdateSuccess: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly tipoConfidencialidadService: TipoConfidencialidadService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private authService: SgiAuthService
  ) {
    super(translate);

    this.resolveSortProperty = (column: string) => {
      if (column === 'nombre') {
        return 'nombre.value';
      }
      return column;
    };
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.buildFilterFormGroup();
    this.filter = this.createFilter();
  }

  protected setupI18N(): void {
    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      TIPO_CONFIDENCIALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ITipoConfidencialidad>> {
    return this.tipoConfidencialidadService.findTodos(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    if (this.authService.hasAuthority('CSP-TCONF-R')) {
      this.columnas = ['nombre', 'activo', 'acciones'];
    } else {
      this.columnas = ['nombre', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.tiposConfidencialidad$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('nombre.value', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }
    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.buildFilterFormGroup();
  }

  /**
   * Abre un modal para añadir o actualizar un tipo de confidencialidad
   *
   * @param tipoConfidencialidad tipo de confidencialidad
   */
  openModal(tipoConfidencialidad?: ITipoConfidencialidad): void {
    const config: MatDialogConfig<ITipoConfidencialidad> = {
      data: tipoConfidencialidad ?? { activo: true } as ITipoConfidencialidad
    };
    const dialogRef = this.matDialog.open(TipoConfidencialidadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITipoConfidencialidad) => {
        if (result) {
          this.snackBarService.showSuccess(tipoConfidencialidad ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      }
    );
  }

  /**
   * Desactiva un tipo de confidencialidad
   * @param tipoConfidencialidad tipo de confidencialidad
   */
  deactivateTipoConfidencialidad(tipoConfidencialidad: ITipoConfidencialidad): void {
    const subscription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tipoConfidencialidadService.desactivar(tipoConfidencialidad.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.processError(error);
          } else {
            this.processError(new SgiError(this.textoErrorDesactivar));
          }
        }
      );
    this.suscripciones.push(subscription);
  }

  /**
   * Activa un tipo de confidencialidad
   * @param tipoConfidencialidad tipo de confidencialidad
   */
  activateTipoConfidencialidad(tipoConfidencialidad: ITipoConfidencialidad): void {
    const subscription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          tipoConfidencialidad.activo = true;
          return this.tipoConfidencialidadService.reactivar(tipoConfidencialidad.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessReactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          tipoConfidencialidad.activo = false;
          if (error instanceof SgiError) {
            this.processError(error);
          } else {
            this.processError(new SgiError(this.textoErrorReactivar));
          }
        }
      );
    this.suscripciones.push(subscription);
  }

  /**
   * Inicializa el formulario de búsqueda con los valores por defecto
   */
  private buildFilterFormGroup() {
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      activo: new FormControl('true')
    });
  }

}
