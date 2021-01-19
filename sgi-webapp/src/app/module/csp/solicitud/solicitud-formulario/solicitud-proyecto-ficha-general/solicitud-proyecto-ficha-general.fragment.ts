import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudProyectoDatosService } from '@core/services/csp/solicitud-proyecto-datos.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { SolicitudActionService } from '../../solicitud.action.service';

export interface AreaTematicaSolicitudData {
  rootTree: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
  areaTematicaSolicitud: IAreaTematica;
}
export class SolicitudProyectoFichaGeneralFragment extends FormFragment<ISolicitudProyectoDatos>{
  solicitudProyectoDatos: ISolicitudProyectoDatos;
  areasTematicas$ = new BehaviorSubject<AreaTematicaSolicitudData[]>([]);

  constructor(
    private logger: NGXLogger,
    private solicitud: ISolicitud,
    private solicitudService: SolicitudService,
    private solicitudProyectoDatosService: SolicitudProyectoDatosService,
    private convocatoriaService: ConvocatoriaService,
    private actionService: SolicitudActionService
  ) {
    super(solicitud?.id, true);
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.solicitudProyectoDatos = {} as ISolicitudProyectoDatos;
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup({
      titulo: new FormControl('', [Validators.required, Validators.maxLength(250)]),
      acronimo: new FormControl('', [Validators.maxLength(50)]),
      duracion: new FormControl('', [Validators.min(1), Validators.max(9999)]),
      colaborativo: new FormControl('', [Validators.required]),
      coordinadorExterno: new FormControl('', [Validators.required]),
      universidadSubcontratada: new FormControl('', []),
      presupuestoPorEntidades: new FormControl('', [Validators.required]),
      objetivos: new FormControl('', [Validators.maxLength(2000)]),
      intereses: new FormControl('', [Validators.maxLength(2000)]),
      resultadosPrevistos: new FormControl('', [Validators.maxLength(2000)]),
      envioEtica: new FormControl('', [])
    });
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(proyectoDatos: ISolicitudProyectoDatos): { [key: string]: any; } {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `buildPatch(convocatoria: ${proyectoDatos})`, 'start');
    const result = {
      titulo: proyectoDatos.titulo,
      acronimo: proyectoDatos.acronimo,
      duracion: proyectoDatos.duracion,
      colaborativo: proyectoDatos.colaborativo,
      coordinadorExterno: proyectoDatos.coordinadorExterno,
      universidadSubcontratada: proyectoDatos.universidadSubcontratada,
      presupuestoPorEntidades: proyectoDatos.presupuestoPorEntidades,
      objetivos: proyectoDatos.objetivos,
      intereses: proyectoDatos.intereses,
      resultadosPrevistos: proyectoDatos.resultadosPrevistos,
      envioEtica: proyectoDatos.envioEtica
    };
    this.actionService.setSociosColaboradores(proyectoDatos.colaborativo);
    this.actionService.setEnableAddSocioColaborador(proyectoDatos.coordinadorExterno);

    const form = this.getFormGroup();
    const coordinadorExterno = form.get('coordinadorExterno');
    this.subscriptions.push(
      form.get('colaborativo').valueChanges.pipe(
        tap((colaborativo) => {
          if (colaborativo === false) {
            coordinadorExterno.disable();
          } else {
            coordinadorExterno.enable();
            coordinadorExterno.setValue(undefined);
          }
          this.actionService.setSociosColaboradores = colaborativo;
        })
      ).subscribe()
    );
    this.subscriptions.push(
      coordinadorExterno.valueChanges.pipe(
        tap((value) => this.actionService.setEnableAddSocioColaborador = value)
      ).subscribe()
    );

    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `buildPatch(convocatoria: ${proyectoDatos})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<ISolicitudProyectoDatos> {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.solicitudService.findSolicitudProyectoDatos(key).pipe(
      map(solicitudProyectoDatos => {
        const newProyecto: ISolicitudProyectoDatos = {
          acronimo: undefined,
          areaTematica: undefined,
          checkListRef: undefined,
          colaborativo: undefined,
          coordinadorExterno: undefined,
          duracion: undefined,
          envioEtica: undefined,
          id: undefined,
          intereses: undefined,
          objetivos: undefined,
          presupuestoPorEntidades: undefined,
          resultadosPrevistos: undefined,
          solicitud: this.solicitud,
          titulo: undefined,
          universidadSubcontratada: undefined
        };
        this.solicitudProyectoDatos = solicitudProyectoDatos ? solicitudProyectoDatos : newProyecto;
        return this.solicitudProyectoDatos;
      }),
      switchMap((solicitudProyectoDatos) => {
        if (solicitudProyectoDatos.solicitud.convocatoria) {
          const id = solicitudProyectoDatos.solicitud.convocatoria.id;
          return this.convocatoriaService.findAreaTematicas(id).pipe(
            map((results) => {
              const nodes = results.items.map(convocatoriaAreaTematica => {
                const area: AreaTematicaSolicitudData = {
                  rootTree: this.getFirstLevelAreaTematica(convocatoriaAreaTematica.areaTematica),
                  areaTematicaConvocatoria: convocatoriaAreaTematica.areaTematica,
                  areaTematicaSolicitud: solicitudProyectoDatos.areaTematica,
                };
                return area;
              });
              this.areasTematicas$.next(nodes);
              return results;
            }),
            switchMap(() => of(solicitudProyectoDatos))
          );
        }
        return of(solicitudProyectoDatos);
      }),
      tap(() => this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, `initializer(key: ${key})`, 'end')),
      catchError(error => {
        this.logger.error(SolicitudProyectoFichaGeneralFragment.name, `initializer(key: ${key})`, error);
        return EMPTY;
      })
    );
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'start');
    if (areaTematica.padre) {
      this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
        `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
      return this.getFirstLevelAreaTematica(areaTematica.padre);
    }
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
    return areaTematica;
  }

  getValue(): ISolicitudProyectoDatos {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().value;
    this.solicitudProyectoDatos.titulo = form.titulo;
    this.solicitudProyectoDatos.acronimo = form.acronimo;
    this.solicitudProyectoDatos.duracion = form.duracion;
    this.solicitudProyectoDatos.colaborativo = form.colaborativo;
    if (this.solicitudProyectoDatos.colaborativo) {
      this.solicitudProyectoDatos.coordinadorExterno = form.coordinadorExterno;
    } else {
      this.solicitudProyectoDatos.coordinadorExterno = false;
    }
    this.solicitudProyectoDatos.universidadSubcontratada = form.universidadSubcontratada;
    this.solicitudProyectoDatos.objetivos = form.objetivos;
    this.solicitudProyectoDatos.intereses = form.intereses;
    this.solicitudProyectoDatos.resultadosPrevistos = form.resultadosPrevistos;
    this.solicitudProyectoDatos.envioEtica = form.envioEtica;
    this.solicitudProyectoDatos.presupuestoPorEntidades = form.presupuestoPorEntidades;
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, `getValue()`, 'end');
    return this.solicitudProyectoDatos;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name, `saveOrUpdate()`, 'start');
    const solicitudProyectoDatos = this.getValue();
    const observable$ = this.solicitudProyectoDatos.id ? this.update(solicitudProyectoDatos) :
      this.create(solicitudProyectoDatos);
    return observable$.pipe(
      map(value => {
        this.solicitudProyectoDatos = value;
        this.refreshInitialState(true);
        return this.solicitudProyectoDatos.id;
      }),
      tap(() => this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(solicitudProyectoDatos: ISolicitudProyectoDatos): Observable<ISolicitudProyectoDatos> {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `create(solicitudProyectoDatos: ${solicitudProyectoDatos})`, 'start');
    solicitudProyectoDatos.solicitud = {
      id: this.getKey()
    } as ISolicitud;
    return this.solicitudProyectoDatosService.create(solicitudProyectoDatos).pipe(
      tap(() => this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
        `create(solicitudProyectoDatos: ${solicitudProyectoDatos})`, 'end'))
    );
  }

  private update(solicitudProyectoDatos: ISolicitudProyectoDatos): Observable<ISolicitudProyectoDatos> {
    this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
      `update(solicitudProyectoDatos: ${solicitudProyectoDatos})`, 'start');
    return this.solicitudProyectoDatosService.update(solicitudProyectoDatos.id, solicitudProyectoDatos).pipe(
      tap(() => this.logger.debug(SolicitudProyectoFichaGeneralFragment.name,
        `update(solicitudProyectoDatos: ${solicitudProyectoDatos})`, 'end'))
    );
  }
}
