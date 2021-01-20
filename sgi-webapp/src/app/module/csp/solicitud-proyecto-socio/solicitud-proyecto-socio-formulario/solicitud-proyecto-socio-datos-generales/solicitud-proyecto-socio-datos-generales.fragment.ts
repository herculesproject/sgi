import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FormFragment } from '@core/services/action-service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { NumberValidator } from '@core/validators/number-validator';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { tap, map, switchMap } from 'rxjs/operators';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SolicitudService } from '@core/services/csp/solicitud.service';

export class SolicitudProyectoSocioDatosGeneralesFragment extends FormFragment<ISolicitudProyectoSocio> {
  solicitudProyectoSocio: ISolicitudProyectoSocio;

  constructor(
    private logger: NGXLogger,
    key: number,
    private service: SolicitudProyectoSocioService,
    private solicitudService: SolicitudService,
    private actionService: SolicitudProyectoSocioActionService
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, 'constructor()', 'start');
    this.solicitudProyectoSocio = {} as ISolicitudProyectoSocio;
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, 'constructor()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    const form = new FormGroup(
      {
        empresa: new FormControl('', [Validators.required]),
        rolSocio: new FormControl('', [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        numInvestigadores: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
        importeSolicitado: new FormControl('', [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        mesInicio: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
        mesFin: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
      },
      {
        validators: [NumberValidator.isAfter('mesInicio', 'mesFin')]
      }
    );
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, 'buildFormGroup()', 'start');
    return form;
  }

  protected buildPatch(solicitudProyectoSocio: ISolicitudProyectoSocio): { [key: string]: any; } {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
      `buildPatch(value: ${solicitudProyectoSocio})`, 'start');
    const result = {
      empresa: solicitudProyectoSocio.empresa,
      rolSocio: solicitudProyectoSocio.rolSocio,
      numInvestigadores: solicitudProyectoSocio.numInvestigadores,
      importeSolicitado: solicitudProyectoSocio.importeSolicitado,
      mesInicio: solicitudProyectoSocio.mesInicio,
      mesFin: solicitudProyectoSocio.mesFin,
    };
    this.solicitudProyectoSocio = solicitudProyectoSocio;
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
      `buildPatch(value: ${solicitudProyectoSocio})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<ISolicitudProyectoSocio> {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
      `initializer(key: ${key})`, 'start');
    return this.service.findById(key).pipe(
      tap(() => this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
        `initializer(key: ${key})`, 'end'))
    );
  }

  getValue(): ISolicitudProyectoSocio {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().controls;
    this.solicitudProyectoSocio.empresa = form.empresa.value;
    this.solicitudProyectoSocio.rolSocio = form.rolSocio.value;
    this.solicitudProyectoSocio.numInvestigadores = form.numInvestigadores.value;
    this.solicitudProyectoSocio.importeSolicitado = form.importeSolicitado.value;
    this.solicitudProyectoSocio.mesInicio = form.mesInicio.value;
    this.solicitudProyectoSocio.mesFin = form.mesFin.value;
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, `getValue()`, 'end');
    return this.solicitudProyectoSocio;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name, `saveOrUpdate()`, 'start');
    const solicitudProyectoSocio = this.getValue();
    const observable$ = this.isEdit() ? this.update(solicitudProyectoSocio) :
      this.create(solicitudProyectoSocio);
    return observable$.pipe(
      map(result => {
        this.solicitudProyectoSocio = result;
        this.actionService.solicitudProyectoSocio = result;
        return this.solicitudProyectoSocio.id;
      }),
      tap(() => this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
        `saveOrUpdate()`, 'end'))
    );
  }

  private create(solicitudProyectoSocio: ISolicitudProyectoSocio): Observable<ISolicitudProyectoSocio> {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
      `create(solicitudProyectoSocio: ${solicitudProyectoSocio})`, 'start');
    return this.solicitudService.findSolicitudProyectoDatos(this.actionService.solicitudId).pipe(
      switchMap((solicitudProyectoDatos) => {
        solicitudProyectoSocio.solicitudProyectoDatos = solicitudProyectoDatos;
        return this.service.create(solicitudProyectoSocio);
      }),
      tap(() => this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
        `create(solicitudProyectoSocio: ${solicitudProyectoSocio})`, 'end'))
    );
  }

  private update(solicitudProyectoSocio: ISolicitudProyectoSocio): Observable<ISolicitudProyectoSocio> {
    this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
      `update(solicitudProyectoSocio: ${solicitudProyectoSocio})`, 'start');
    return this.service.update(solicitudProyectoSocio.id, solicitudProyectoSocio).pipe(
      tap(() => this.logger.debug(SolicitudProyectoSocioDatosGeneralesFragment.name,
        `update(solicitudProyectoSocio: ${solicitudProyectoSocio})`, 'end'))
    );
  }
}
