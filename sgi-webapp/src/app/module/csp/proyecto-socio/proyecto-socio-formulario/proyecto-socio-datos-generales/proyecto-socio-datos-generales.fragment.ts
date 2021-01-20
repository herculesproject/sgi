import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { tap, map, switchMap } from 'rxjs/operators';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { IProyecto } from '@core/models/csp/proyecto';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { DateValidator } from '@core/validators/date-validator';

export class ProyectoSocioDatosGeneralesFragment extends FormFragment<IProyectoSocio> {
  proyectoSocio: IProyectoSocio;

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoId: number,
    private service: ProyectoSocioService,
    private empresaEconomicaService: EmpresaEconomicaService,
    private actionService: ProyectoSocioActionService
  ) {
    super(key);
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, 'constructor()', 'start');
    this.proyectoSocio = {
      proyecto: {
        id: this.proyectoId
      } as IProyecto
    } as IProyectoSocio;
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup(
      {
        empresa: new FormControl({
          value: '',
          disabled: this.isEdit()
        }, [
          Validators.required
        ]),
        rolSocio: new FormControl('', [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        numInvestigadores: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
        importeConcedido: new FormControl('', [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        fechaInicio: new FormControl(''),
        fechaFin: new FormControl(''),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(proyectoSocio: IProyectoSocio): { [key: string]: any; } {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoSocio})`, 'start');
    const result = {
      empresa: proyectoSocio.empresa,
      rolSocio: proyectoSocio.rolSocio,
      numInvestigadores: proyectoSocio.numInvestigadores,
      importeConcedido: proyectoSocio.importeConcedido,
      fechaInicio: proyectoSocio.fechaInicio,
      fechaFin: proyectoSocio.fechaFin,
    };
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoSocio})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IProyectoSocio> {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.service.findById(key)
      .pipe(
        switchMap(proyectoSocio => {
          const personaRef = proyectoSocio.empresa.personaRef;
          return this.empresaEconomicaService.findById(personaRef)
            .pipe(
              map(empresa => {
                proyectoSocio.empresa = empresa;
                return proyectoSocio;
              })
            );
        }),
        tap((proyectoSocio) => this.proyectoSocio = proyectoSocio),
        tap(() => this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
          `initializer(key: ${key})`, 'end'))
      );
  }

  getValue(): IProyectoSocio {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.proyectoSocio.empresa = form.empresa.value;
    this.proyectoSocio.rolSocio = form.rolSocio.value;
    this.proyectoSocio.numInvestigadores = form.numInvestigadores.value;
    this.proyectoSocio.importeConcedido = form.importeConcedido.value;
    this.proyectoSocio.fechaInicio = form.fechaInicio.value;
    this.proyectoSocio.fechaFin = form.fechaFin.value;
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.proyectoSocio;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const proyectoSocio = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoSocio) : this.create(proyectoSocio);
    return observable$.pipe(
      map(result => {
        this.proyectoSocio = result;
        return this.proyectoSocio.id;
      }),
      tap(() => this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(proyectoSocio: IProyectoSocio): Observable<IProyectoSocio> {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
      `create(proyectoSocio: ${proyectoSocio})`, 'start');

    return this.service.create(proyectoSocio)
      .pipe(
        tap(() => this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
          `create(proyectoSocio: ${proyectoSocio})`, 'end'))
      );
  }

  private update(proyectoSocio: IProyectoSocio): Observable<IProyectoSocio> {
    this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
      `update(proyectoSocio: ${proyectoSocio})`, 'start');

    return this.service.update(proyectoSocio.id, proyectoSocio)
      .pipe(
        tap(() => this.logger.debug(ProyectoSocioDatosGeneralesFragment.name,
          `update(proyectoSocio: ${proyectoSocio})`, 'end'))
      );
  }
}
