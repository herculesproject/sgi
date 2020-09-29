import { Fragment } from '@core/services/action-service';
import { map } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';

import { IHito } from '@core/models/csp/hito';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { BehaviorSubject, Observable, of } from 'rxjs';

export class ConvocatoriaHitosFragment extends Fragment {

  hitos$: BehaviorSubject<StatusWrapper<IHito>[]> = new BehaviorSubject<StatusWrapper<IHito>[]>([]);

  private selectedIdConvocatoria: number;

  constructor(
    key: number,
    private service: ConvocatoriaService,
  ) {
    super(key);
    this.selectedIdConvocatoria = key;
  }


  onInitialize(): void {
    if (this.getKey()) {
      this.loadHitos(this.getKey() as number);
    }
  }



  loadHitos(idConvocatoria: number): void {
    if (!this.isInitialized() || this.selectedIdConvocatoria !== idConvocatoria) {
      this.selectedIdConvocatoria = idConvocatoria;
      this.service.findHitosConvocatoria(idConvocatoria).pipe(
        map((response) => {
          if (response.items) {

            return response.items.map((hito) => new StatusWrapper<IHito>(hito));
          }
          else {
            return [];
          }
        })
      ).subscribe((hitos) => {
        if (this.isEdit() && this.selectedIdConvocatoria !== this.getKey()) {
          this.setChanges(true);
          this.setComplete(this.selectedIdConvocatoria ? true : false);
        }
        else if (this.isEdit() && this.selectedIdConvocatoria === this.getKey()) {
          this.setChanges(false);
          this.setComplete(false);
        }
        else {
          this.setChanges(this.selectedIdConvocatoria ? true : false);
          this.setComplete(this.selectedIdConvocatoria ? true : false);
        }
        this.hitos$.next(hitos);
      });
    }
  }


  saveOrUpdate(): Observable<void> {

    return of(void 0);
  }
}