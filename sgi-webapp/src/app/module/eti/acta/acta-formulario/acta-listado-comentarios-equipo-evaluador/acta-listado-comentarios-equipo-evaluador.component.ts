import { Component, Input } from '@angular/core';
import { AbstractTableWithoutPaginationComponent } from '@core/component/abstract-table-without-pagination.component';
import { TipoEstadoComentario } from '@core/models/eti/comentario';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';

export interface IEvaluadorWithComentariosAndEnviados extends IEvaluador {
  numComentarios: number;
  comentariosEnviados: boolean;
}

@Component({
  selector: 'sgi-acta-listado-comentarios-equipo-evaluador',
  templateUrl: './acta-listado-comentarios-equipo-evaluador.component.html',
  styleUrls: ['./acta-listado-comentarios-equipo-evaluador.component.scss']
})
export class ActaListadoComentariosEquipoEvaluadorComponent extends AbstractTableWithoutPaginationComponent<IEvaluadorWithComentariosAndEnviados> {
  evaluadores$: Observable<IEvaluadorWithComentariosAndEnviados[]>;

  @Input()
  convocatoriaReunion: IConvocatoriaReunion;
  @Input()
  actaId: number;

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly personaService: PersonaService,
    private readonly evaluadorService: EvaluadorService,
    private readonly evaluacionService: EvaluacionService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService
  ) {
    super();
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluadorWithComentariosAndEnviados>> {
    if (this.convocatoriaReunion?.id) {
      return this.convocatoriaReunionService.findEvaluacionesActivas(this.convocatoriaReunion?.id).pipe(
        map((response) => {
          if (response.items) {
            return response.items[0].memoria?.id;
          }
          else {
            return null;
          }
        }),
        switchMap((idMemoria) => {
          return this.evaluadorService.findAllMemoriasAsignablesConvocatoria(this.convocatoriaReunion?.comite?.id, idMemoria, this.convocatoriaReunion?.fechaEvaluacion).pipe(
            map(response => {
              return response as SgiRestListResult<IEvaluadorWithComentariosAndEnviados>;
            }),
            switchMap(response => from(response.items).pipe(
              mergeMap(evaluador => {
                if (evaluador?.persona?.id) {
                  return this.personaService.findById(evaluador.persona.id).pipe(
                    map(persona => {
                      evaluador.persona = persona;
                      evaluador.numComentarios = 0;
                      evaluador.comentariosEnviados = false;
                      return evaluador;
                    })
                  );
                }
                return of(evaluador);
              }),
              mergeMap(evaluador => {
                if (evaluador?.persona?.id) {
                  return this.evaluacionService.getComentariosPersonaActa(this.actaId, evaluador.persona.id).pipe(
                    map(comentariosEvaluador => {
                      evaluador.numComentarios = comentariosEvaluador.length;
                      evaluador.comentariosEnviados = comentariosEvaluador.some(comentario => comentario.estado === TipoEstadoComentario.CERRADO);
                    })
                  );
                }
                return of(evaluador);
              }),
              toArray(),
              map(() => {
                return response;
              })
            )
            ));
        })
      )
    }
  }

  protected initColumns(): void {
    this.columnas = ['persona.nombre', 'numComentarios', 'comentariosEnviados'];
  }

  protected loadTable(reset?: boolean): void {
    this.evaluadores$ = this.getObservableLoadTable(reset);
  }

  protected createFilters(): SgiRestFilter[] {
    return [];
  }


}
