import { IProyectoProrroga, TipoProrrogaEnum } from '@core/models/csp/proyecto-prorroga';
import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { tap, map } from 'rxjs/operators';
import { IProyecto } from '@core/models/csp/proyecto';
import { DateValidator } from '@core/validators/date-validator';
import { NumberValidator } from '@core/validators/number-validator';

export class ProyectoProrrogaDatosGeneralesFragment extends FormFragment<IProyectoProrroga> {

  constructor(
    private logger: NGXLogger,
    key: number,
    private service: ProyectoProrrogaService,
    private proyecto: IProyecto,
    public selectedProyectoProrrogas: IProyectoProrroga[],
    private proyectoProrroga: IProyectoProrroga,
    private readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, 'constructor()', 'start');
    if (this.proyectoProrroga) {
      this.proyectoProrroga.proyecto = this.proyecto;
    } else {
      this.proyectoProrroga = {
        id: key,
        proyecto: this.proyecto as IProyecto
      } as IProyectoProrroga
    }
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup(
      {
        numProrroga: new FormControl({
          value: this.getLastProrroga() ? this.getLastProrroga()?.numProrroga + 1 : 1,
          disabled: true
        }),
        fechaConcesion: new FormControl('', [
          Validators.required
        ]),
        tipoProrroga: new FormControl('', [
          Validators.required
        ]),
        fechaFin: new FormControl({ value: '', disabled: true }),
        importe: new FormControl({ value: '', disabled: true }),
        observaciones: new FormControl('', [Validators.maxLength(250)]),
        fechaUltimaConcesion: new FormControl(this?.getLastProrroga()?.fechaConcesion)
      },
      {
        validators: [
          DateValidator.isAfter('fechaUltimaConcesion', 'fechaConcesion', false),
        ]
      }
    );

    this.subscriptions.push(form.controls.tipoProrroga.valueChanges.subscribe((value: TipoProrrogaEnum) => {
      this.addValidations(value);
    }));

    if (this.readonly) {
      form.disable();
    }

    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(proyectoProrroga: IProyectoProrroga): { [key: string]: any; } {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoProrroga})`, 'start');
    const result = {
      numProrroga: proyectoProrroga.numProrroga,
      fechaConcesion: proyectoProrroga.fechaConcesion,
      fechaFin: proyectoProrroga.fechaFin,
      tipoProrroga: proyectoProrroga.tipoProrroga,
      importe: proyectoProrroga.importe,
      observaciones: proyectoProrroga.observaciones
    };

    this.addValidations(proyectoProrroga.tipoProrroga);
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
      `buildPatch(value: ${proyectoProrroga})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IProyectoProrroga> {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.service.findById(key)
      .pipe(
        tap((proyectoProrroga) => this.proyectoProrroga = proyectoProrroga),
        tap(() => this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
          `initializer(key: ${key})`, 'end'))
      );
  }

  getValue(): IProyectoProrroga {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.proyectoProrroga.numProrroga = form.numProrroga.value;
    this.proyectoProrroga.fechaConcesion = form.fechaConcesion.value;
    this.proyectoProrroga.fechaFin = form.fechaFin.value;
    this.proyectoProrroga.tipoProrroga = form.tipoProrroga.value;
    this.proyectoProrroga.importe = form.importe.value;
    this.proyectoProrroga.observaciones = form.observaciones.value;
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.proyectoProrroga;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const proyectoProrroga = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoProrroga) : this.create(proyectoProrroga);
    return observable$.pipe(
      map(result => {
        this.proyectoProrroga = result;
        this.refreshInitialState(true);
        return this.proyectoProrroga.id;
      }),
      tap(() => this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(proyectoProrroga: IProyectoProrroga): Observable<IProyectoProrroga> {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
      `create(proyectoProrroga: ${proyectoProrroga})`, 'start');

    return this.service.create(proyectoProrroga)
      .pipe(
        tap(() => this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
          `create(proyectoProrroga: ${proyectoProrroga})`, 'end'))
      );
  }

  private update(proyectoProrroga: IProyectoProrroga): Observable<IProyectoProrroga> {
    this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
      `update(proyectoProrroga: ${proyectoProrroga})`, 'start');

    return this.service.update(proyectoProrroga.id, proyectoProrroga)
      .pipe(
        tap(() => this.logger.debug(ProyectoProrrogaDatosGeneralesFragment.name,
          `update(proyectoProrroga: ${proyectoProrroga})`, 'end'))
      );
  }

  private addValidations(value: TipoProrrogaEnum) {
    const form = this.getFormGroup().controls;
    if (!this.readonly) {
      if (value === TipoProrrogaEnum.TIEMPO) {
        form.fechaFin.setValidators([Validators.required]);
        form.fechaFin.enable();
        form.importe.setValidators([
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]);
        form.importe.disable();
      } else if (value === TipoProrrogaEnum.IMPORTE) {
        form.importe.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]);
        form.importe.enable();
        form.fechaFin.setValidators(null);
        form.fechaFin.disable();
      } else {
        form.fechaFin.setValidators([Validators.required]);
        form.fechaFin.enable();
        form.importe.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]);
        form.importe.enable();
      }
    }
  }

  /**
   * Obtiene la última prórroga del listado
   */
  getLastProrroga(): IProyectoProrroga {
    if (this.selectedProyectoProrrogas && this.selectedProyectoProrrogas.length > 0) {
      if (this.proyectoProrroga) {
        if (this.proyectoProrroga.id !== this.selectedProyectoProrrogas[this.selectedProyectoProrrogas.length - 1].id) {
          return this.selectedProyectoProrrogas[this.selectedProyectoProrrogas.length - 1];
        } else if (this.selectedProyectoProrrogas.length > 1) {
          return this.selectedProyectoProrrogas[this.selectedProyectoProrrogas.length - 2];
        }
      }
    }
    return null;
  }
}
