import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Acta } from '@core/models/eti/acta';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/services/form-group-util';
import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { Asistente } from '@core/models/eti/asistente';
import { switchMap, map } from 'rxjs/operators';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { MatSort } from '@angular/material/sort';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
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


  filter: SgiRestFilter[];

  asistentes$: Observable<Asistente[]> = of();


  constructor(
    protected readonly logger: NGXLogger,
    protected readonly asistenteService: AsistenteService,
    protected readonly usuarioInfoService: UsuarioInfoService
  ) {
    super(logger);

    this.displayedColumns = ['evaluador.numIdentificadorPersonal', 'evaluador.nombre', 'asistencia', 'motivo', 'acciones'];


    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];
  }


  @Input()
  set idConvocatoria(idConvocatoria: number) {
    this.filter = [];
    if (idConvocatoria) {
      const filterConvocatoria: SgiRestFilter = {
        field: 'convocatoriaReunion.id',
        type: SgiRestFilterType.EQUALS,
        value: idConvocatoria.toString()
      };
      this.filter.push(filterConvocatoria);
      this.asistentes$ = this.asistenteService.findAll({
        sort: {
          direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
          field: this.sort.active
        }, filters: this.filter
      }).pipe(

        switchMap((response) => {

          if (response.items) {

            const listObservables: Observable<Asistente>[] = [];

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

  crearObservable(): Observable<any> {
    this.logger.debug(ActaAsistentesComponent.name, 'crearObservable()', 'start');
    const observable = of({});
    this.logger.debug(ActaAsistentesComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosIniciales(): Acta {
    this.logger.debug(ActaAsistentesComponent.name, 'crearObservable()', 'start');
    const datos = new Acta();
    this.logger.debug(ActaAsistentesComponent.name, 'crearObservable()', 'end');
    return datos;
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
