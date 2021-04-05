import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { FormFragment } from '@core/services/action-service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { Observable, of } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoEntidadGestoraFragment extends FormFragment<IProyectoEntidadGestora> {

  private proyectoEntidadGestora: IProyectoEntidadGestora;

  ocultarSubEntidad: boolean;

  constructor(
    private fb: FormBuilder,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEntidadGestoraService: ProyectoEntidadGestoraService,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
    super(key, true);
    this.setComplete(true);
    this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
  }

  protected initializer(key: number): Observable<IProyectoEntidadGestora> {
    if (this.getKey()) {
      return this.proyectoService.findEntidadGestora(key).pipe(
        switchMap((entidadesGestoras) => {
          const proyectoEntidadesGestoras = entidadesGestoras.items;
          if (proyectoEntidadesGestoras.length > 0) {
            const entidadGestora = proyectoEntidadesGestoras[0];
            return this.empresaEconomicaService.findById(entidadGestora.empresaEconomica.personaRef).pipe(
              map((empresaEconomica) => {
                entidadGestora.empresaEconomica = empresaEconomica;
                return entidadGestora;
              })
            );
          }
          return of(this.proyectoEntidadGestora);
        })
      );
    }
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      entidadGestora: new FormControl({
        value: null,
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

    return form;
  }

  buildPatch(entidadGestora: IProyectoEntidadGestora): { [key: string]: any } {
    this.proyectoEntidadGestora = entidadGestora;
    return {
      entidadGestora: entidadGestora.empresaEconomica
    };
  }

  getValue(): IProyectoEntidadGestora {
    this.proyectoEntidadGestora.empresaEconomica = this.getFormGroup().controls.entidadGestora.value;

    return this.proyectoEntidadGestora;
  }

  /**
   * Setea la entidad gestora seleccionada en el formulario
   * @param entidadGestora empresa
   */
  private onEntidadGestoraChange(entidadGestora: IEmpresaEconomica): void {
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
  }

  saveOrUpdate(): Observable<void> {
    let observable$: Observable<any>;
    const proyectoEntidadGestora = this.getValue();
    if (!proyectoEntidadGestora.empresaEconomica) {
      observable$ = this.deleteProyectoEntidadGestora(proyectoEntidadGestora);
    } else {
      observable$ = proyectoEntidadGestora.id ?
        this.updateProyectoEntidadGestora(proyectoEntidadGestora) : this.createProyectoEntidadGestora(proyectoEntidadGestora);
    }
    return observable$.pipe(
      takeLast(1)
    );
  }

  private createProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    proyectoEntidadGestora.proyectoId = this.getKey() as number;
    return this.proyectoEntidadGestoraService.create(proyectoEntidadGestora).pipe(
      map(result => {
        this.proyectoEntidadGestora = Object.assign(this.proyectoEntidadGestora, result);
      })
    );
  }

  private updateProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.update(
      proyectoEntidadGestora.id, proyectoEntidadGestora).pipe(
        map(result => {
          this.proyectoEntidadGestora = Object.assign(this.proyectoEntidadGestora, result);
        })
      );
  }

  private deleteProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.deleteById(
      proyectoEntidadGestora.id).pipe(
        tap(() => {
          this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
          this.proyectoEntidadGestora.empresaEconomica = {} as IEmpresaEconomica;
        })
      );
  }

}
