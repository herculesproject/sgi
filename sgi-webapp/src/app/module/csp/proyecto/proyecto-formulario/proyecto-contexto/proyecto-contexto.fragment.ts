import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { FormFragment } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
export interface AreaTematicaProyectoData {
  root: IAreaTematica;
  areaTematica: IAreaTematica;
}
export class ProyectoContextoFragment extends FormFragment<IProyectoContexto>{

  private proyectoContexto: IProyectoContexto;
  areasTematicas$ = new BehaviorSubject<AreaTematicaProyectoData[]>([]);

  private areaTematica: IAreaTematica;
  ocultarTable: boolean;

  constructor(
    private logger: NGXLogger,
    private proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private contextoProyectoService: ContextoProyectoService,
    private convocatoriaService: ConvocatoriaService,
    private proyectoValue: IProyecto
  ) {
    super(proyecto?.id, true);
    this.logger.debug(ProyectoContextoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.proyectoContexto = {} as IProyectoContexto;
    this.logger.debug(ProyectoContextoFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoContextoFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup({
      objetivos: new FormControl('', [Validators.maxLength(2000)]),
      intereses: new FormControl('', [Validators.maxLength(2000)]),
      resultados_previstos: new FormControl('', [Validators.maxLength(2000)]),
      propiedadResultados: new FormControl(''),
    });
    this.logger.debug(ProyectoContextoFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(value: IProyectoContexto): { [key: string]: any; } {
    this.logger.debug(ProyectoContextoFragment.name,
      `buildPatch(contexto: ${value})`, 'start');
    const result = {
      proyecto: value.proyecto,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultados_previstos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados
    };
    this.logger.debug(ProyectoContextoFragment.name,
      `buildPatch(contexto: ${value})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IProyectoContexto> {
    this.logger.debug(ProyectoContextoFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.contextoProyectoService.findById(key).pipe(
      map(proyectoContexto => {
        const newProyecto: IProyectoContexto = {
          id: null,
          proyecto: this.proyectoValue,
          objetivos: null,
          intereses: null,
          resultadosPrevistos: null,
          propiedadResultados: null,
          areaTematica: null,
          areaTematicaConvocatoria: null
        };
        this.proyectoContexto = proyectoContexto ? proyectoContexto : newProyecto;
        this.ocultarTable = true;
        if (this.proyectoContexto?.proyecto?.convocatoria?.id) {
          this.ocultarTable = false;
        }
        return this.proyectoContexto;
      }),
      switchMap((proyectoContexto) => {

        let root: IAreaTematica;
        if (proyectoContexto?.areaTematicaConvocatoria?.id) {
          root = this.getFirstLevelAreaTematica(proyectoContexto.areaTematicaConvocatoria);
        } else if (proyectoContexto?.areaTematica?.id) {
          root = this.getFirstLevelAreaTematica(proyectoContexto.areaTematica);
        }

        const area: AreaTematicaProyectoData = {
          root,
          areaTematica: proyectoContexto.areaTematica
        };

        const loadData = this.loadAreaDataTable(area);
        this.areasTematicas$.next([loadData]);

        return of(proyectoContexto);
      }),
      tap(() => this.logger.debug(ProyectoContextoFragment.name, `initializer(key: ${key})`, 'end')),
      catchError(error => {
        this.logger.error(ProyectoContextoFragment.name, `initializer(key: ${key})`, error);
        return EMPTY;
      })
    );
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    this.logger.debug(ProyectoContextoFragment.name,
      `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'start');
    if (areaTematica.padre) {
      this.logger.debug(ProyectoContextoFragment.name,
        `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
      return this.getFirstLevelAreaTematica(areaTematica.padre);
    }
    this.logger.debug(ProyectoContextoFragment.name,
      `getFirstLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
    return areaTematica;
  }

  getValue(): IProyectoContexto {
    this.logger.debug(ProyectoContextoFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().value;
    this.proyectoContexto.objetivos = form.objetivos;
    this.proyectoContexto.intereses = form.intereses;
    this.proyectoContexto.resultadosPrevistos = form.resultados_previstos;
    this.proyectoContexto.propiedadResultados = form.propiedadResultados;
    this.proyectoContexto.areaTematica = this.areaTematica;
    this.logger.debug(ProyectoContextoFragment.name, `getValue()`, 'end');
    return this.proyectoContexto;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoContextoFragment.name, `saveOrUpdate()`, 'start');
    const proyectoContextoDatos = this.getValue();
    const observable$ = this.proyectoContexto.id ? this.update(proyectoContextoDatos) :
      this.create(proyectoContextoDatos);
    return observable$.pipe(
      map(value => {
        this.proyectoContexto = value;
        this.refreshInitialState(true);
        return this.proyectoContexto.id;
      }),
      tap(() => this.logger.debug(ProyectoContextoFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(proyectoContexto: IProyectoContexto): Observable<IProyectoContexto> {
    this.logger.debug(ProyectoContextoFragment.name,
      `create(proyectoContexto: ${proyectoContexto})`, 'start');
    proyectoContexto.proyecto = {
      id: this.getKey()
    } as IProyecto;
    return this.contextoProyectoService.create(proyectoContexto).pipe(
      tap(() => this.logger.debug(ProyectoContextoFragment.name,
        `create(proyectoContexto: ${proyectoContexto})`, 'end'))
    );
  }

  private update(proyectoContexto: IProyectoContexto): Observable<IProyectoContexto> {
    this.logger.debug(ProyectoContextoFragment.name,
      `update(proyectoContexto: ${proyectoContexto})`, 'start');
    return this.contextoProyectoService.update(proyectoContexto.id, proyectoContexto).pipe(
      tap(() => this.logger.debug(ProyectoContextoFragment.name,
        `update(proyectoContexto: ${proyectoContexto})`, 'end'))
    );
  }

  updateAreaTematica(data: AreaTematicaProyectoData) {
    this.logger.debug(ProyectoContextoFragment.name,
      `updateConvocatoriaAreaTematica(wrapper: ${data})`, 'start');
    if (data) {
      const element = this.loadAreaDataTable(data);
      const wrapper = new StatusWrapper<AreaTematicaProyectoData>(element);
      const current = this.areasTematicas$.value;
      const index = current.findIndex(value =>
        value.areaTematica.nombre === data.areaTematica.nombre);
      if (index >= 0) {
        wrapper.setEdited();
        this.areasTematicas$[index] = wrapper.value;
        this.areasTematicas$.next(current);
        this.setChanges(true);
      }
    }
    this.logger.debug(ProyectoContextoFragment.name,
      `${this.updateAreaTematica.name}(wrapper: ${data})`, 'end');
  }

  addAreaTematica(data: AreaTematicaProyectoData) {
    this.logger.debug(ProyectoContextoFragment.name,
      `addConvocatoriaAreaTematica(areaTematica: ${data})`, 'start');
    const element = this.loadAreaDataTable(data);
    const wrapper = new StatusWrapper<AreaTematicaProyectoData>(element);
    wrapper.setCreated();
    const current = this.areasTematicas$.value;
    current.push(wrapper.value);
    this.areasTematicas$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ProyectoContextoFragment.name,
      `addConvocatoriaAreaTematica(areaTematica: ${data})`, 'end');
  }

  loadAreaDataTable(data: AreaTematicaProyectoData): AreaTematicaProyectoData {
    this.logger.debug(ProyectoContextoFragment.name, `loadData(data: ${data})`, 'start');
    this.areaTematica = data.areaTematica;
    if (data.areaTematica) {
      const result = this.getSecondLevelAreaTematica(this.areaTematica);
      const padre = result.padre ? result.padre : this.areaTematica;
      const element: AreaTematicaProyectoData = {
        root: padre,
        areaTematica: this.areaTematica
      };
      this.logger.debug(ProyectoContextoFragment.name, `loadData(data: ${data})`, 'end');
      return element;
    }
    this.logger.debug(ProyectoContextoFragment.name, `loadData(data: ${data})`, 'end');
    return data;
  }

  private getSecondLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    this.logger.debug(ProyectoContextoFragment.name,
      `getSecondLevelAreaTematica(areaTematica: ${areaTematica})`, 'start');
    if (areaTematica?.padre?.padre) {
      this.logger.debug(ProyectoContextoFragment.name,
        `getSecondLevelAreaTematica(areaTematica: ${areaTematica})`, 'end');
      return this.getSecondLevelAreaTematica(areaTematica.padre);
    }
    this.logger.debug(ProyectoContextoFragment.name,
      `getSecondLevelPrograma(areaTematica: ${areaTematica})`, 'end');
    return areaTematica;
  }

  /**
   * Devolvemos la convocatoria de una area tematica
   */
  get convocatoriaAreaTematica(): IAreaTematica {
    return this.proyectoContexto?.areaTematicaConvocatoria;
  }

}
