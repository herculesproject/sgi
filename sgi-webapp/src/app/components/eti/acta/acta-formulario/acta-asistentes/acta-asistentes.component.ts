import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatSort } from '@angular/material/sort';

import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';

import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';

import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';

import { IAsistente } from '@core/models/eti/asistente';

import { FormGroupUtil } from '@core/services/form-group-util';

import { AsistenteService } from '@core/services/eti/asistente.service';
import { UsuarioInfoService } from '@core/services/sgp/usuario-info.service';

@Component({
  selector: 'app-acta-asistentes',
  templateUrl: './acta-asistentes.component.html',
  styleUrls: ['./acta-asistentes.component.scss']
})
export class ActaAsistentesComponent extends AbstractTabComponent<any> implements OnInit {

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  FormGroupUtil = FormGroupUtil;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  asistentes$: Observable<IAsistente[]> = of();


  constructor(
    protected readonly logger: NGXLogger,
    protected readonly asistenteService: AsistenteService,
    protected readonly usuarioInfoService: UsuarioInfoService
  ) {
    super(logger);

    this.displayedColumns = ['evaluador.numIdentificadorPersonal', 'evaluador.nombre', 'asistencia', 'motivo', 'acciones'];

  }


  @Input()
  set idConvocatoria(idConvocatoria: number) {

    if (idConvocatoria) {

      this.asistentes$ = this.asistenteService.findAllByConvocatoriaReunionId(idConvocatoria).pipe(

        switchMap((response) => {

          if (response.items) {

            const listObservables: Observable<IAsistente>[] = [];

            response.items.forEach((asistente) => {
              const asistente$ = this.usuarioInfoService.findById(asistente.evaluador.usuarioRef).pipe(
                map((usuarioInfo) => {
                  asistente.evaluador.numIdentificadorPersonal = usuarioInfo.numIdentificadorPersonal;
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
    this.logger.debug(ActaAsistentesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();



    this.logger.debug(ActaAsistentesComponent.name, 'ngOnInit()', 'end');

  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ActaAsistentesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({

    });
    this.logger.debug(ActaAsistentesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }


  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
