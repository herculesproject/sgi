import { FormControl, FormGroup } from '@angular/forms';
import { IPlan } from '@core/models/csp/tipos-configuracion';
import { FormFragment } from '@core/services/action-service';
import { PlanService } from '@core/services/csp/plan.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { PlanInvestigacionActionService } from '../../plan-investigacion.action.service';

export class PlanInvestigacionDatosGeneralesFragment extends FormFragment<IPlan> {

  private plan: IPlan;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private planService: PlanService,
    private actionService: PlanInvestigacionActionService
  ) {
    super(key);
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name, 'constructor()', 'start');
    this.plan = {} as IPlan;
    this.plan.activo = true;
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.buildFormGroup.name}()`, 'start');
    const fb = new FormGroup({
      nombre: new FormControl(''),
      descripcion: new FormControl(''),
      activo: new FormControl('')
    });
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.buildFormGroup.name}()`, 'end');
    return fb;
  }

  protected buildPatch(planes: IPlan): { [key: string]: any; } {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.buildPatch.name}(planes: ${planes})`, 'start');
    const result = {
      id: planes.id,
      activo: planes.activo,
      descripcion: planes.descripcion,
      nombre: planes.nombre
    } as IPlan;
    this.plan = planes;
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.buildPatch.name}(planes: ${planes})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IPlan> {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.initializer.name}(key: ${key})`, 'start');
    return this.planService.findById(key).pipe(
      catchError(() => {
        this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
          `${this.initializer.name}(key: ${key})`, 'end');
        return EMPTY;
      }),
      tap(() => {
        this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
          `${this.initializer.name}(key: ${key})`, 'end');
      })
    );
  }

  getValue(): IPlan {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name, `${this.getValue.name}()`, 'start');
    const form = this.getFormGroup().value;
    const planes = this.plan;
    planes.nombre = form.nombre;
    planes.descripcion = form.descripcion;
    planes.activo = form.activo;
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name, `${this.getValue.name}()`, 'end');
    return planes;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name, `${this.saveOrUpdate.name}()`, 'start');
    const planes = this.getValue();
    const observable$ = this.isEdit() ? this.update(planes) : this.create(planes);
    return observable$.pipe(
      map((result: IPlan) => {
        return result.id;
      }),
      tap(() => this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
        `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private create(planes: IPlan): Observable<IPlan> {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.create.name}(planes: ${planes})`, 'start');
    return this.planService.create(planes).pipe(
      tap((result: IPlan) => {
        this.plan = result;
        this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
          `${this.create.name}(planes: ${planes})`, 'end');
      })
    );
  }

  private update(planes: IPlan): Observable<IPlan> {
    this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
      `${this.update.name}(planes: ${planes})`, 'start');
    return this.planService.update(planes.id, planes).pipe(
      tap((result: IPlan) => {
        this.plan = result;
        this.logger.debug(PlanInvestigacionDatosGeneralesFragment.name,
          `${this.update.name}(planes: ${planes})`, 'end');
      })
    );
  }
}
