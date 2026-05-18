import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { Fragment } from '@core/services/action-service';
import { GrupoDescriptorService } from '@core/services/csp/grupo-descriptor/grupo-descriptor.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class GrupoDescriptorFragment extends Fragment {
  descriptores$ = new BehaviorSubject<StatusWrapper<IGrupoDescriptor>[]>([]);
  private gruposDescriptorEliminados: StatusWrapper<IGrupoDescriptor>[] = [];

  constructor(
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoDescriptorService: GrupoDescriptorService,
    private readonly tipoGrupoDescriptorService: TipoDescriptorGrupoService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;

      this.subscriptions.push(
        this.grupoService.findDescriptores(id).pipe(
          map((response) => response.items),
          switchMap((descriptores) => this.populateTiposDescriptores(descriptores))
        ).subscribe((descriptores) => {
          this.descriptores$.next(
            descriptores.map((descriptor) => {
              descriptor.grupoId = this.getKey() as number;
              return new StatusWrapper<IGrupoDescriptor>(descriptor);
            })
          );
        })
      );
    }
  }

  addGrupoDescriptor(element: IGrupoDescriptor): void {
    const wrapper = new StatusWrapper<IGrupoDescriptor>(element);
    wrapper.setCreated();
    const current = this.descriptores$.value;
    current.push(wrapper);
    this.descriptores$.next(current);
    this.setChanges(true);
  }

  updateGrupoDescriptor(wrapper: StatusWrapper<IGrupoDescriptor>): void {
    const current = this.descriptores$.value;
    const index = current.findIndex((value) => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.descriptores$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoDescriptor(wrapper: StatusWrapper<IGrupoDescriptor>) {
    const current = this.descriptores$.value;
    const index = current.findIndex((value) => value === wrapper);

    if (index >= 0) {
      if (!wrapper.created) {
        this.gruposDescriptorEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.descriptores$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteGrupoDescriptores(),
      this.updateGrupoDescriptores(),
      this.createGrupoDescriptores()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteGrupoDescriptores(): Observable<void> {
    if (this.gruposDescriptorEliminados.length === 0) {
      return of(void 0);
    }

    return from(this.gruposDescriptorEliminados).pipe(
      mergeMap((wrapped) => {
        return this.grupoDescriptorService
          .deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.gruposDescriptorEliminados = this.gruposDescriptorEliminados
                .filter((deleted) => deleted.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateGrupoDescriptores(): Observable<void> {
    const descriptores = this.descriptores$.value.filter((value) => value.edited);
    if (descriptores.length === 0) {
      return of(void 0);
    }

    return from(descriptores).pipe(
      mergeMap((data) =>
        this.grupoDescriptorService
          .update(data.value.id, data.value)
          .pipe(
            map((updated) => {
              updated.tipoDescriptorGrupo = data.value.tipoDescriptorGrupo;
              const index = this.descriptores$.value.findIndex((current) => current === data);
              this.descriptores$.value[index] = new StatusWrapper<IGrupoDescriptor>(updated);
              this.descriptores$.next(this.descriptores$.value);
            })
          )
      )
    );
  }

  private createGrupoDescriptores(): Observable<void> {
    const descriptores = this.descriptores$.value.filter((value) => value.created);
    if (descriptores.length === 0) {
      return of(void 0);
    }

    descriptores.forEach(
      (wrapper) => wrapper.value.grupoId = this.getKey() as number
    );

    return from(descriptores).pipe(
      mergeMap((data) =>
        this.grupoDescriptorService
          .create(data.value)
          .pipe(
            map((created) => {
              created.tipoDescriptorGrupo = data.value.tipoDescriptorGrupo;
              const index = this.descriptores$.value.findIndex((current) => current === data);
              this.descriptores$.value[index] = new StatusWrapper<IGrupoDescriptor>(created);
              this.descriptores$.next(this.descriptores$.value);
            })
          )
      )
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.descriptores$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private populateTiposDescriptores(descriptores: IGrupoDescriptor[]): Observable<IGrupoDescriptor[]> {
    const ids = [...new Set(descriptores.map((e) => e.tipoDescriptorGrupo?.id).filter((id) => !!id))];
    if (!ids.length) {
      return of(descriptores);
    }

    return this.tipoGrupoDescriptorService.findAllByIdIn(ids).pipe(
      map((response) => {
        const tiposEnlaceById = new Map<number, ITipoDescriptorGrupo>(response.items.map((t) => [t.id, t]));
        descriptores.forEach((descriptor) => {
          if (descriptor.tipoDescriptorGrupo?.id) {
            descriptor.tipoDescriptorGrupo = tiposEnlaceById.get(descriptor.tipoDescriptorGrupo.id) ?? descriptor.tipoDescriptorGrupo;
          }
        });

        return descriptores;
      })
    );
  }

}
