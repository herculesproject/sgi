import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Fragment } from '@core/services/action-service';
import { GrupoRelacionInstitucionalService } from '@core/services/csp/grupo-relacion-institucional/grupo-relacion-institucional.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class GrupoRelacionInstitucionalFragment extends Fragment {
  relacionesInstitucionales$ = new BehaviorSubject<StatusWrapper<IGrupoRelacionInstitucional>[]>([]);
  private relacionesInstitucionalesEliminadas: StatusWrapper<IGrupoRelacionInstitucional>[] = [];

  constructor(
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoRelacionInstitucionalService: GrupoRelacionInstitucionalService,
    private readonly empresaService: EmpresaService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;

      this.subscriptions.push(
        this.grupoService.findRelacionesInstitucionales(id).pipe(
          map((response) => response.items),
          mergeMap((relaciones) => this.populateEntidades(relaciones))
        ).subscribe((relaciones) => {
          this.relacionesInstitucionales$.next(
            relaciones.map((relacion) => {
              relacion.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoRelacionInstitucional>(relacion);
            })
          );
        })
      );
    }
  }

  addGrupoRelacionInstitucional(element: IGrupoRelacionInstitucional): void {
    const wrapper = new StatusWrapper<IGrupoRelacionInstitucional>(element);
    wrapper.setCreated();
    const current = this.relacionesInstitucionales$.value;
    current.push(wrapper);
    this.relacionesInstitucionales$.next(current);
    this.setChanges(true);
  }

  updateGrupoRelacionInstitucional(wrapper: StatusWrapper<IGrupoRelacionInstitucional>): void {
    const current = this.relacionesInstitucionales$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);

    if (index >= 0) {
      wrapper.setEdited();
      this.relacionesInstitucionales$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoRelacionInstitucional(wrapper: StatusWrapper<IGrupoRelacionInstitucional>): void {
    const current = this.relacionesInstitucionales$.value;
    const index = current.findIndex((value) => value === wrapper);

    if (index >= 0) {
      if (!wrapper.created) {
        this.relacionesInstitucionalesEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.relacionesInstitucionales$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteRelaciones(),
      this.updateRelaciones(),
      this.createRelaciones()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteRelaciones(): Observable<void> {
    if (this.relacionesInstitucionalesEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.relacionesInstitucionalesEliminadas).pipe(
      mergeMap((wrapper) => {
        return this.grupoRelacionInstitucionalService.deleteById(wrapper.value.id).pipe(
          tap(() => {
            this.relacionesInstitucionalesEliminadas = this.relacionesInstitucionalesEliminadas
              .filter((deleted) => deleted.value.id !== wrapper.value.id);
          })
        );
      })
    );
  }

  private updateRelaciones(): Observable<void> {
    const relaciones = this.relacionesInstitucionales$.value.filter((value) => value.edited);
    if (relaciones.length === 0) {
      return of(void 0);
    }

    return from(relaciones).pipe(
      mergeMap((wrapper) => {
        return this.grupoRelacionInstitucionalService
          .update(wrapper.value.id, wrapper.value)
          .pipe(
            map((updated) => {
              updated.entidad = wrapper.value.entidad;
              const index = this.relacionesInstitucionales$.value.findIndex((current) => current === wrapper);
              this.relacionesInstitucionales$.value[index] = new StatusWrapper<IGrupoRelacionInstitucional>(updated);
              this.relacionesInstitucionales$.next(this.relacionesInstitucionales$.value);
            })
          );
      })
    );
  }

  private createRelaciones(): Observable<void> {
    const relaciones = this.relacionesInstitucionales$.value.filter((value) => value.created);
    if (relaciones.length === 0) {
      return of(void 0);
    }

    relaciones.forEach(
      (wrapper) => wrapper.value.grupo.id = this.getKey() as number
    );

    return from(relaciones).pipe(
      mergeMap((wrapper) => {
        return this.grupoRelacionInstitucionalService
          .create(wrapper.value)
          .pipe(
            map((created) => {
              created.entidad = wrapper.value.entidad;
              const index = this.relacionesInstitucionales$.value.findIndex((current) => current === wrapper);
              this.relacionesInstitucionales$.value[index] = new StatusWrapper<IGrupoRelacionInstitucional>(created);
              this.relacionesInstitucionales$.next(this.relacionesInstitucionales$.value);
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.relacionesInstitucionales$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private populateEntidades(relaciones: IGrupoRelacionInstitucional[]): Observable<IGrupoRelacionInstitucional[]> {
    const ids = [...new Set(relaciones.map(r => r.entidad?.id).filter(id => !!id))];
    if (!ids.length) {
      return of(relaciones);
    }

    return this.empresaService.findAllByIdIn(ids).pipe(
      map((response) => {
        const empresasById = new Map<string, IEmpresa>(response.items.map((e) => [e.id, e]));
        relaciones.forEach((relacion) => {
          if (relacion.entidad?.id) {
            relacion.entidad = empresasById.get(relacion.entidad.id) ?? relacion.entidad;
          }
        });

        return relaciones;
      })
    );
  }

}
