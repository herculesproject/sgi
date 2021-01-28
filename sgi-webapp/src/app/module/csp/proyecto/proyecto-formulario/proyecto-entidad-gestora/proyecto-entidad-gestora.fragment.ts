import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { FormFragment } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, tap, switchMap } from 'rxjs/operators';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { IProyecto } from '@core/models/csp/proyecto';
import { ProyectoActionService } from '../../proyecto.action.service';

export class ProyectoEntidadGestoraFragment extends FormFragment<IProyectoEntidadGestora> {

  private proyectoEntidadGestora: IProyectoEntidadGestora;

  private padreRef: IEmpresaEconomica;
  ocultarSubEntidad: boolean;

  constructor(
    private fb: FormBuilder,
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEntidadGestoraService: ProyectoEntidadGestoraService,
    private empresaEconomicaService: EmpresaEconomicaService,
    private actionService: ProyectoActionService
  ) {
    super(key, true);
    this.setComplete(true);
    this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
    this.logger.debug(ProyectoEntidadGestoraFragment.name, 'constructor()', 'start');
    this.logger.debug(ProyectoEntidadGestoraFragment.name, 'constructor()', 'end');
  }

  protected initializer(key: number): Observable<IProyectoEntidadGestora> {
    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.initializer.name}(key: number)`, 'start');
    if (this.getKey()) {
      return this.proyectoService.findEntidadGestora(key).pipe(
        switchMap((entidadGestora) => {
          const proyectoEntidadesGestoras = entidadGestora.items;
          if (proyectoEntidadesGestoras.length > 0) {
            this.proyectoEntidadGestora = proyectoEntidadesGestoras[0];
            return this.empresaEconomicaService.findById(this.proyectoEntidadGestora.empresaEconomica.personaRef).pipe(
              map((empresaEconomica) => {
                this.getFormGroup().get('entidadGestora').setValue(empresaEconomica);
                return this.proyectoEntidadGestora;
              })
            );
          }
          return of(this.proyectoEntidadGestora);
        })
      );
    }
    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.initializer.name}(${key})`, 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.buildFormGroup.name}()`, 'start');
    const form = this.fb.group({
      entidadGestora: new FormControl({
        value: '',
        disabled: false
      }),
      identificadorFiscal: new FormControl({
        value: '',
        disabled: true
      }),
      numeroIdentificadorFiscal: new FormControl({
        value: '',
        disabled: true
      }),
      nombre: new FormControl({
        value: '',
        disabled: true
      }),
      razonSocial: new FormControl({
        value: '',
        disabled: true
      }),
      entidad: new FormControl({
        value: '',
        disabled: true
      }),
      codigoSubentidad: new FormControl({
        value: '',
        disabled: true
      }),
      direccionPostal: new FormControl({
        value: '',
        disabled: true
      }),
      tipoEmpresa: new FormControl({
        value: '',
        disabled: true
      }),
      nombrePrincipal: new FormControl({
        value: '',
        disabled: true
      }),
      raazonSocialPrincipal: new FormControl({
        value: '',
        disabled: true
      })
    });

    this.subscriptions.push(
      form.controls.entidadGestora.valueChanges.subscribe(
        (entidadGestora) => this.onEntidadGestoraChange(entidadGestora)
      )
    );

    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.buildFormGroup.name}()`, 'end');

    return form;
  }

  buildPatch(entidadGestora: IProyectoEntidadGestora): { [key: string]: any } {
    this.logger.debug(ProyectoEntidadGestoraFragment.name,
      `${this.buildPatch.name}(entidadGestora: ${entidadGestora})`, 'start');
    return {};
  }

  getValue(): IProyectoEntidadGestora {
    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.getValue.name}()`, 'start');

    if (this.proyectoEntidadGestora === null) {
      this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
    }

    this.proyectoEntidadGestora.proyecto = this.actionService.proyectoDatosGenerales;

    this.logger.debug(ProyectoEntidadGestoraFragment.name, `${this.getValue.name}()`, 'end');
    return this.proyectoEntidadGestora;
  }

  /**
   * Setea la entidad gestora seleccionada en el formulario
   * @param entidadGestora empresa
   */
  private onEntidadGestoraChange(entidadGestora: IEmpresaEconomica): void {
    this.logger.debug(ProyectoEntidadGestoraFragment.name, `onEntidadGestoraChange(${entidadGestora})`, 'start');

    if (entidadGestora) {

      this.getFormGroup().controls.identificadorFiscal.setValue(entidadGestora.tipoDocumento);
      this.getFormGroup().controls.razonSocial.setValue(entidadGestora.razonSocial);
      this.getFormGroup().controls.direccionPostal.setValue(entidadGestora.direccion);
      this.getFormGroup().controls.tipoEmpresa.setValue(entidadGestora.tipoEmpresa);

      if (entidadGestora.personaRefPadre) {

        this.getFormGroup().controls.entidad.setValue(2);
        this.getFormGroup().controls.nombre.setValue(entidadGestora.tipoEmpresa);
        this.getFormGroup().controls.razonSocial.setValue(entidadGestora.razonSocial);
        this.getFormGroup().controls.codigoSubentidad.setValue(entidadGestora.numeroDocumento);

        this.subscriptions.push(
          this.empresaEconomicaService.findById(entidadGestora.personaRefPadre).subscribe(
            personaRef => {
              if (personaRef) {
                this.getFormGroup().controls.raazonSocialPrincipal.setValue(personaRef.razonSocial);
                this.getFormGroup().controls.nombrePrincipal.setValue(personaRef.tipoEmpresa);
                this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(personaRef.numeroDocumento);
              } else {
                this.getFormGroup().controls.raazonSocialPrincipal.setValue('');
                this.getFormGroup().controls.nombrePrincipal.setValue('');
                this.getFormGroup().controls.numeroIdentificadorFiscal.setValue('');
              }

            }
          )
        );

        this.ocultarSubEntidad = true;
      } else {
        this.getFormGroup().controls.entidad.setValue(1);
        this.getFormGroup().controls.razonSocial.setValue(entidadGestora.razonSocial);
        this.getFormGroup().controls.nombre.setValue(entidadGestora.tipoEmpresa);
        this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(entidadGestora.numeroDocumento);

        this.getFormGroup().controls.nombrePrincipal.setValue('');
        this.getFormGroup().controls.raazonSocialPrincipal.setValue('');
        this.getFormGroup().controls.codigoSubentidad.setValue('');
        this.ocultarSubEntidad = false;
      }
    }

    this.logger.debug(ProyectoEntidadGestoraFragment.name, `onEntidadGestoraChange(${entidadGestora})`, 'end');
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ProyectoEntidadGestoraFragment.name, 'saveOrUpdate()', 'start');
    let observable$: Observable<any>;
    const fichaGeneral = this.getValue();
    fichaGeneral.proyecto = {
      id: this.getKey()
    } as IProyecto;
    const entidadRef = this.getFormGroup().controls.entidadGestora.value?.personaRef;
    if (entidadRef !== this.proyectoEntidadGestora.empresaEconomica?.personaRef) {
      if (!entidadRef) {
        observable$ = this.deleteProyectoEntidadGestora();
      }
      else {
        observable$ = this.proyectoEntidadGestora.id ?
          this.updateProyectoEntidadGestora() : this.createProyectoEntidadGestora();
      }
      return observable$.pipe(
        map((value) => {
          if (entidadRef) {
            this.proyectoEntidadGestora = value;
          }
          this.logger.debug(ProyectoEntidadGestoraFragment.name, 'saveOrUpdate()', 'end');
          return this.proyectoEntidadGestora.id;
        }),
        tap(() => this.logger.debug(ProyectoEntidadGestoraFragment.name,
          `saveOrUpdate()`, 'end'))
      );
    }
  }


  private createProyectoEntidadGestora(): Observable<IProyectoEntidadGestora> {
    this.logger.debug(ProyectoEntidadGestoraFragment.name,
      `createProyectoEntidadGestora()`, 'start');
    this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.proyectoEntidadGestoraService.create(this.proyectoEntidadGestora).pipe(
      tap(result => {
        this.proyectoEntidadGestora = result;
        this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
      }),
      tap(() => this.logger.debug(ProyectoEntidadGestoraFragment.name,
        `createProyectoEntidadGestora()`, 'end'))
    );
  }

  private updateProyectoEntidadGestora(): Observable<IProyectoEntidadGestora> {
    this.logger.debug(ProyectoEntidadGestoraFragment.name,
      `updateProyectoEntidadGestora()`, 'start');
    this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.proyectoEntidadGestoraService.update(
      this.proyectoEntidadGestora.id, this.proyectoEntidadGestora).pipe(
        tap(result => {
          this.proyectoEntidadGestora = result;
          this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
        }),
        tap(() => this.logger.debug(ProyectoEntidadGestoraFragment.name,
          `updateProyectoEntidadGestora()`, 'end'))
      );
  }

  private deleteProyectoEntidadGestora(): Observable<void> {
    this.logger.debug(ProyectoEntidadGestoraFragment.name,
      `deleteProyectoEntidadGestora()`, 'start');
    this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;
    return this.proyectoEntidadGestoraService.deleteById(
      this.proyectoEntidadGestora.id).pipe(
        tap(() => {
          this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
          this.proyectoEntidadGestora.empresaEconomica = {} as IEmpresaEconomica;
        }),
        tap(() => this.logger.debug(ProyectoEntidadGestoraFragment.name,
          `deleteProyectoEntidadGestora()`, 'end'))
      );
  }

}
