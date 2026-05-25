import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@herculesproject/framework/http';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoDescriptorGrupoModalComponent } from '../tipo-descriptor-grupo-modal/tipo-descriptor-grupo-modal.component';

const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const TIPO_DESCRIPTOR_GRUPO_KEY = marker('csp.tipo-descriptor-grupo');

@Component({
  templateUrl: './tipo-descriptor-grupo-listado.component.html',
  styleUrls: ['./tipo-descriptor-grupo-listado.component.scss']
})
export class TipoDescriptorGrupoListadoComponent extends AbstractTablePaginationComponent<ITipoDescriptorGrupo> implements OnInit {

  tiposDescriptoresGrupo$: Observable<ITipoDescriptorGrupo[]>;

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
    private readonly tipoDescriptorGrupoService: TipoDescriptorGrupoService,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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
      TIPO_DESCRIPTOR_GRUPO_KEY,
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

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ITipoDescriptorGrupo>> {
    return this.tipoDescriptorGrupoService.findTodos(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    if (this.authService.hasAuthority('CSP-TDESG-R')) {
      this.columnas = ['nombre', 'activo', 'acciones'];
    } else {
      this.columnas = ['nombre', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.tiposDescriptoresGrupo$ = this.getObservableLoadTable(reset);
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
   * Abre un modal para añadir o actualizar un tipo de descriptor de grupo
   *
   * @param tipoDescriptorGrupo tipo de descriptor de grupo
   */
  openModal(tipoDescriptorGrupo?: ITipoDescriptorGrupo): void {
    const config: MatDialogConfig<ITipoDescriptorGrupo> = {
      data: tipoDescriptorGrupo ? Object.assign({}, tipoDescriptorGrupo) : { activo: true } as ITipoDescriptorGrupo
    };
    const dialogRef = this.matDialog.open(TipoDescriptorGrupoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITipoDescriptorGrupo) => {
        if (result) {
          this.snackBarService.showSuccess(tipoDescriptorGrupo ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      }
    );
  }

  /**
   * Desactiva un tipo de descriptor de grupo
   * @param tipoDescriptorGrupo tipo de descriptor de grupo
   */
  deactivateTipoDescriptorGrupo(tipoDescriptorGrupo: ITipoDescriptorGrupo): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tipoDescriptorGrupoService.desactivar(tipoDescriptorGrupo.id);
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
    this.suscripciones.push(subcription);
  }

  /**
   * Activa un tipo de descriptor de grupo
   * @param tipoDescriptorGrupo tipo de descriptor de grupo
   */
  activateTipoDescriptorGrupo(tipoDescriptorGrupo: ITipoDescriptorGrupo): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          tipoDescriptorGrupo.activo = true;
          return this.tipoDescriptorGrupoService.reactivar(tipoDescriptorGrupo.id);
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
          tipoDescriptorGrupo.activo = false;
          if (error instanceof SgiError) {
            this.processError(error);
          } else {
            this.processError(new SgiError(this.textoErrorReactivar));
          }
        }
      );
    this.suscripciones.push(subcription);
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
