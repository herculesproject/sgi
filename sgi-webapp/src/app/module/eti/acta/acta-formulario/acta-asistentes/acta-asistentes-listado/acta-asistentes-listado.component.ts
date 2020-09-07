import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IAsistente } from '@core/models/eti/asistente';
import { MatSort } from '@angular/material/sort';
import { ActaAsistentesEditarModalComponent } from '../acta-asistentes-editar-modal/acta-asistentes-editar-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';

@Component({
  selector: 'sgi-acta-asistentes',
  templateUrl: './acta-asistentes-listado.component.html',
  styleUrls: ['./acta-asistentes-listado.component.scss']
})
export class ActaAsistentesListadoComponent extends AbstractTabComponent<IAsistente> implements OnInit {

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  FormGroupUtil = FormGroupUtil;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  asistentes$: Observable<IAsistente[]> = of();

  convocatoriaId: number;

  asistenteSuscripcion: Subscription;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected matDialog: MatDialog
  ) {
    super(logger);

    this.displayedColumns = ['evaluador.identificadorNumero', 'evaluador.nombre', 'asistencia', 'motivo', 'acciones'];

  }

  @Input()
  set idConvocatoria(idConvocatoria: number) {

    this.convocatoriaId = idConvocatoria;

    if (idConvocatoria) {

      this.asistentes$ = this.convocatoriaReunionService.findAsistentes(this.convocatoriaId).pipe(

        switchMap((response) => {

          if (response.items) {

            const listObservables: Observable<IAsistente>[] = [];

            response.items.forEach((asistente) => {
              const asistente$ = this.personaFisicaService.getInformacionBasica(asistente.evaluador.personaRef).pipe(
                map((usuarioInfo) => {
                  asistente.evaluador.identificadorNumero = usuarioInfo.identificadorNumero;
                  asistente.evaluador.nombre = usuarioInfo.nombre;
                  asistente.evaluador.primerApellido = usuarioInfo.primerApellido;
                  asistente.evaluador.segundoApellido = usuarioInfo.segundoApellido;
                  return asistente;
                })
              );
              listObservables.push(asistente$);
            });

            return zip(...listObservables);
          } else {
            return of([]);
          }
        })
      );
    }
  }


  ngOnInit() {
    this.logger.debug(ActaAsistentesListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.logger.debug(ActaAsistentesListadoComponent.name, 'ngOnInit()', 'end');

  }

  createFormGroup(): FormGroup {
    this.logger.debug(ActaAsistentesListadoComponent.name, 'createFormGroup()', 'start');
    const formGroup = new FormGroup({});
    this.logger.debug(ActaAsistentesListadoComponent.name, 'createFormGroup()', 'end');
    return formGroup;
  }

  /**
   * Abre la ventana modal para modificar una asistencia
   *
   * @param asistente asistente a modificar
   */
  openUpdateModal(asistente: IAsistente): void {
    this.logger.debug(ActaAsistentesListadoComponent.name, 'openUpdateModal()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.minWidthModal,
      maxHeight: GLOBAL_CONSTANTS.minHeightModal,
      data: asistente
    };
    const dialogRef = this.matDialog.open(ActaAsistentesEditarModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: IAsistente) => {
        if (resultado) {
          this.asistentes$ = this.convocatoriaReunionService.findAsistentes(this.convocatoriaId).pipe(

            switchMap((response) => {

              if (response.items) {

                const listObservables: Observable<IAsistente>[] = [];

                response.items.forEach((iasistente) => {
                  const asistente$ = this.personaFisicaService.getInformacionBasica(iasistente.evaluador.personaRef).pipe(
                    map((usuarioInfo) => {
                      iasistente.evaluador.identificadorNumero = usuarioInfo.identificadorNumero;
                      iasistente.evaluador.nombre = usuarioInfo.nombre;
                      iasistente.evaluador.primerApellido = usuarioInfo.primerApellido;
                      iasistente.evaluador.segundoApellido = usuarioInfo.segundoApellido;
                      return iasistente;
                    })
                  );
                  listObservables.push(asistente$);
                });

                return zip(...listObservables);
              } else {
                return of([]);
              }
            })
          );
        }
        this.logger.debug(ActaAsistentesListadoComponent.name, 'openUpdateModal()', 'end');
      }
    );
  }

  getDatosFormulario(): IAsistente {
    return this.formGroup.value;
  }
}
