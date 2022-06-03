
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { Fragment } from '@core/services/action-service';
import { EmpresaEquipoEmprendedorService } from '@core/services/eer/empresa-equipo-emprendedor/empresa-equipo-emprendedor.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface IEmpresaEquipoEmprendedorListado extends IEmpresaEquipoEmprendedor {
  categoriaProfesional: ICategoriaProfesional;
}

export class EmpresaEquipoEmprendedorFragment extends Fragment {
  equipos$ = new BehaviorSubject<StatusWrapper<IEmpresaEquipoEmprendedorListado>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private readonly empresaEquipoEmprendedorService: EmpresaEquipoEmprendedorService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService,
    private readonly empresaService: EmpresaService,
    private sgiAuthService: SgiAuthService,
    private readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.empresaExplotacionResultadosService.findMiembrosEquipoEmprendedor(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.personaService.findById(element.miembroEquipo.id).pipe(
                  map(persona => {
                    element.miembroEquipo = persona;
                    if (persona.id === this.sgiAuthService.authStatus$?.getValue()?.userRefId) {
                      // Es miembro del equipo
                      this.readonly = false;
                    }
                    return element as IEmpresaEquipoEmprendedorListado;
                  })
                );
              }),
              map(() => result)
            );
          }),
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                if (element.miembroEquipo.entidad?.id) {
                  return this.empresaService.findById(element.miembroEquipo.entidad?.id).pipe(
                    map(entidad => {
                      element.miembroEquipo.entidad = entidad;
                      return element as IEmpresaEquipoEmprendedorListado;
                    }),
                    switchMap(empresaEquipoEmprendedor => this.getVinculacionPersona(empresaEquipoEmprendedor))
                  );
                } else {
                  return of(element);
                }
              }),
              map(() => result)
            );
          }),
          map(miembrosEquipo => {
            return miembrosEquipo.items.map(miembroEquipo => {
              miembroEquipo.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
              return new StatusWrapper<IEmpresaEquipoEmprendedorListado>(miembroEquipo as IEmpresaEquipoEmprendedorListado);
            });
          })
        ).subscribe(
          result => {
            this.equipos$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addEmpresaEquipoEmprendedor(element: IEmpresaEquipoEmprendedorListado) {
    this.getVinculacionPersona(element).subscribe(equipo => {
      const wrapper = new StatusWrapper<IEmpresaEquipoEmprendedorListado>(equipo);
      wrapper.setCreated();
      const current = this.equipos$.value;
      current.push(wrapper);
      this.equipos$.next(current);
      this.setChanges(true);
    });
  }

  updateEmpresaEquipoEmprendedor(wrapper: StatusWrapper<IEmpresaEquipoEmprendedorListado>): void {
    const current = this.equipos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.getVinculacionPersona(this.equipos$.value[index].value).subscribe(() => {
        this.equipos$.value[index] = wrapper;
        this.setChanges(true);
      });

    }
  }

  deleteEmpresaEquipoEmprendedor(wrapper: StatusWrapper<IEmpresaEquipoEmprendedorListado>) {
    const current = this.equipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.equipos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.equipos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.empresaEquipoEmprendedorService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IEmpresaEquipoEmprendedorListado) => {
              const empresaEquipoEmprendedor = values.find(
                equipo => equipo.miembroEquipo.id === value.miembroEquipo.id
              );
              value.miembroEquipo = empresaEquipoEmprendedor.miembroEquipo;
              value.categoriaProfesional = empresaEquipoEmprendedor.categoriaProfesional;
              return value;
            });
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.map(miembroEquipo => {
            miembroEquipo.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
            return new StatusWrapper<IEmpresaEquipoEmprendedorListado>(miembroEquipo as IEmpresaEquipoEmprendedorListado);
          });
        }),
        takeLast(1),
        map((results) => {
          this.equipos$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.equipos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private getVinculacionPersona(element: IEmpresaEquipoEmprendedorListado): Observable<IEmpresaEquipoEmprendedorListado> {
    const filter = new RSQLSgiRestFilter(
      'fechaObtencion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(DateTime.now())
    );
    const options: SgiRestFindOptions = {
      filter
    };
    return this.vinculacionService.findVinculacionesCategoriasProfesionalesByPersonaId(element.miembroEquipo.id, options)
      .pipe(map(vinculacionCategoria => {
        element.categoriaProfesional = vinculacionCategoria?.categoriaProfesional;
        return element;
      }));
  }

}
