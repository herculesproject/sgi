import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IGrupo } from '@core/models/csp/grupo';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoRelacion } from '@core/models/csp/proyecto-relacion';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion, TipoEntidad, TIPO_ENTIDAD_HREF_MAP } from '@core/models/rel/relacion';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { BehaviorSubject, EMPTY, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { IProyectoListadoData } from '../../proyecto-listado/proyecto-listado.component';

// Define the extended IGrupoWithTitulo interface
export interface IGrupoWithTitulo extends IGrupo {
  titulo: I18nFieldValue[];
}

type EntidadRelacionada = IConvocatoria | IInvencion | IProyecto | IGrupoWithTitulo;

export interface IProyectoRelacionTableData {
  id: number;
  entidadRelacionada: EntidadRelacionada;
  entidadRelacionadaHref?: string;
  observaciones: I18nFieldValue[];
  tipoEntidadRelacionada: TipoEntidad;
  entidadConvocanteRef: string;
  codigosSge: string;
  tipoRelacion: TipoRelacion;
}

export enum TipoRelacion {
  HIJO = 'HIJO',
  PADRE = 'PADRE',
}

export class ProyectoRelacionFragment extends Fragment {
  private readonly proyectoRelacionesTableData$ = new BehaviorSubject<StatusWrapper<IProyectoRelacionTableData>[]>([]);
  private proyectoRelacionesTableDataToDelete: StatusWrapper<IProyectoRelacionTableData>[] = [];

  miembrosEquipoProyecto: IPersona[] = [];

  private get proyectoId(): number {
    return this.getKey() as number;
  }

  constructor(
    key: number,
    private readonly proyecto: IProyecto,
    private readonly readonly: boolean,
    private readonly relacionService: RelacionService,
    private readonly proyectoService: ProyectoService,
    private readonly sgiAuthService: SgiAuthService,
    private readonly isAccessingAsInvestigador: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    if (this.proyectoId) {
      return this.proyectoService.findRelaciones(this.proyectoId).pipe(
        map(relaciones => relaciones.map(
          relacion => new StatusWrapper(this.createProyectoRelacionTableDataFromProyectoRelacion(relacion)))),
        tap(relacionesWrapped => this.proyectoRelacionesTableData$.next(relacionesWrapped)),
        catchError((error) => {
          this.processError(error);
          return EMPTY;
        })
      );
    }
  }

  private createProyectoRelacionTableDataFromProyectoRelacion(relacion: IProyectoRelacion): IProyectoRelacionTableData {
    return {
      id: relacion.id,
      entidadRelacionada: {
        id: relacion.entidadRelacionada?.id,
        titulo: relacion.entidadRelacionada?.titulo
      } as EntidadRelacionada,
      entidadRelacionadaHref: this.createEntidadRelacionadaHref(relacion.entidadRelacionada?.id, relacion.tipoEntidadRelacionada),
      observaciones: relacion.observaciones,
      tipoEntidadRelacionada: relacion.tipoEntidadRelacionada,
      entidadConvocanteRef: relacion.entidadRelacionada?.codigoExterno ?? '',
      codigosSge: relacion.entidadRelacionada?.codigosSge ?? '',
      tipoRelacion: null
    };
  }

  private createProyectoRelacionTableDataFromRelacion(relacion: IRelacion): IProyectoRelacionTableData {
    const data: IProyectoRelacionTableData = {
      id: relacion.id,
      entidadRelacionada: null,
      observaciones: relacion.observaciones,
      tipoEntidadRelacionada: null,
      entidadConvocanteRef: '',
      codigosSge: '',
      tipoRelacion: null
    };

    if (this.isEntidadOrigenRelatedEntity(relacion, this.proyectoId)) {
      data.entidadRelacionada = relacion.entidadOrigen;
      data.tipoEntidadRelacionada = relacion.tipoEntidadOrigen;
    } else {
      data.entidadRelacionada = relacion.entidadDestino;
      data.tipoEntidadRelacionada = relacion.tipoEntidadDestino;
    }

    return data;
  }

  /**
   * Comprueba si la entidad origen de la relacion es la entidad relacionada con el proyecto actual
   *
   * @param relacion La relacion
   * @param proyectoId Id del proyecto actual
   * @returns True si la entidad origen de la relacion es la entidad relacionada con el proyecto actual,
   * false si la entidad origen es el proyecto actual
   */
  private isEntidadOrigenRelatedEntity(relacion: IRelacion, proyectoId: number): boolean {
    return relacion.entidadOrigen.id !== proyectoId ||
      relacion.entidadOrigen.id === proyectoId && relacion.tipoEntidadOrigen !== TipoEntidad.PROYECTO;
  }

  /**
   * Genera el enlace de navegacion a la entidad relacionada.
   *
   * Devuelve cadena vacia (sin enlace) cuando no se debe permitir navegar a la entidad:
   * - si se esta accediendo como investigador (vista de perfil de investigacion), ya que no
   *   tiene acceso a las pantallas de gestion destino, o
   * - si la entidad es una Invencion y el usuario no tiene permisos de consulta/edicion sobre ella.
   *
   * @param entidadRelacionadaId id de la entidad relacionada
   * @param tipoEntidad tipo de la entidad relacionada
   * @returns el enlace a la entidad relacionada, o cadena vacia si no debe generarse enlace
   */
  private createEntidadRelacionadaHref(entidadRelacionadaId: number, tipoEntidad: TipoEntidad): string {
    const canAccessEntidadDestino = this.isAccessingAsInvestigador
      || (tipoEntidad === TipoEntidad.INVENCION && !this.hasUserInvencionAuth());
    if (canAccessEntidadDestino) {
      return '';
    }

    return `${TIPO_ENTIDAD_HREF_MAP.get(tipoEntidad)}/${entidadRelacionadaId}`;
  }

  private hasUserInvencionAuth(): boolean {
    return this.sgiAuthService.hasAnyAuthority(['PII-INV-V', 'PII-INV-E']);
  }

  hasEditPerm(): boolean {
    return !this.readonly;
  }

  getCurrentProyectoAsSelfRelated(): IProyectoRelacionTableData {
    return {
      entidadRelacionada: this.proyecto,
      tipoEntidadRelacionada: TipoEntidad.PROYECTO
    } as IProyectoRelacionTableData;
  }

  getRelacionesProyectoTableData$(): Observable<StatusWrapper<IProyectoRelacionTableData>[]> {
    return this.proyectoRelacionesTableData$.asObservable();
  }

  addRelacion(relacion: IProyectoRelacionTableData): void {
    const wrapped = new StatusWrapper<IProyectoRelacionTableData>(relacion);
    this.updateWrapperEntidadRelacionadaHref(wrapped);
    if (relacion.tipoEntidadRelacionada === TipoEntidad.PROYECTO) {
      wrapped.value.codigosSge = (relacion.entidadRelacionada as IProyectoListadoData)?.proyectosSGE;
      wrapped.value.entidadConvocanteRef = (relacion.entidadRelacionada as IProyecto).codigoExterno;
    }
    if (relacion.tipoEntidadRelacionada === TipoEntidad.GRUPO) {
      wrapped.value.codigosSge = (relacion.entidadRelacionada as IGrupo)?.proyectoSge?.id;
      wrapped.value.entidadRelacionada = this.fillGrupoWithTitulo(wrapped.value.entidadRelacionada as IGrupo);
    }
    wrapped.setCreated();
    const current = this.proyectoRelacionesTableData$.value;
    current.push(wrapped);
    this.proyectoRelacionesTableData$.next(current);
    this.setChanges(true);
  }

  updateRelacion(index: number): void {
    if (index >= 0) {
      const current = this.proyectoRelacionesTableData$.value;
      const wrapper = current[index];
      this.updateWrapperEntidadRelacionadaHref(wrapper);
      if (!wrapper.created) {
        wrapper.setEdited();
      }
      this.proyectoRelacionesTableData$.next(current);
      this.setChanges(true);
    }
  }

  private updateWrapperEntidadRelacionadaHref(wrapper: StatusWrapper<IProyectoRelacionTableData>): void {
    wrapper.value.entidadRelacionadaHref = this.createEntidadRelacionadaHref(
      wrapper.value.entidadRelacionada.id, wrapper.value.tipoEntidadRelacionada);
  }

  deleteRelacion(wrapper: StatusWrapper<IProyectoRelacionTableData>): void {
    const current = this.proyectoRelacionesTableData$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectoRelacionesTableDataToDelete.push(current[index]);
      }
      this.removeDeletedRelacionFromArray(index, current);
    }
  }

  private removeDeletedRelacionFromArray(
    index: number, currentProyectoRelacionesTableData: StatusWrapper<IProyectoRelacionTableData>[]): void {
    currentProyectoRelacionesTableData.splice(index, 1);
    this.proyectoRelacionesTableData$.next(currentProyectoRelacionesTableData);
    this.setChanges(this.hasFragmentChangesPending());
  }

  private hasFragmentChangesPending(): boolean {
    return this.proyectoRelacionesTableDataToDelete.length > 0 ||
      this.proyectoRelacionesTableData$.value.some((value) => value.created || value.edited);
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.deleteRelaciones(),
      this.updateRelaciones(),
      this.createRelaciones()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private deleteRelaciones(): Observable<void> {
    if (this.proyectoRelacionesTableDataToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.proyectoRelacionesTableDataToDelete).pipe(
      mergeMap(wrapped =>
        this.deleteInformePatentabilidadById(wrapped)
      )
    );
  }

  private deleteInformePatentabilidadById(wrapped: StatusWrapper<IProyectoRelacionTableData>): Observable<void> {
    return this.relacionService.delete(wrapped.value.id).pipe(
      tap(() =>
        this.proyectoRelacionesTableDataToDelete = this.proyectoRelacionesTableDataToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        )
      )
    );
  }

  private updateRelaciones(): Observable<void> {
    const current = this.proyectoRelacionesTableData$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.relacionService.update(wrapper.value.id, this.createRelacionFromProyectoRelacion(wrapper.value)).pipe(
          map((relacionResponse) => this.refreshProyectoRelacionesTableData(relacionResponse, wrapper, current)),
        );
      }))
    );
  }

  private createRelaciones(): Observable<void> {
    const current = this.proyectoRelacionesTableData$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.relacionService.create(this.createRelacionFromProyectoRelacion(wrapper.value)).pipe(
          map((relacionResponse) => this.refreshProyectoRelacionesTableData(relacionResponse, wrapper, current)),
        );
      }))
    );
  }

  private createRelacionFromProyectoRelacion(proyectoRelacion: IProyectoRelacionTableData): IRelacion {
    return {
      id: proyectoRelacion.id,
      entidadOrigen: proyectoRelacion.tipoEntidadRelacionada !== TipoEntidad.PROYECTO ?
        this.proyecto : this.getEntidadOrigenProyecto(proyectoRelacion),
      entidadDestino: proyectoRelacion.tipoEntidadRelacionada !== TipoEntidad.PROYECTO ?
        proyectoRelacion.entidadRelacionada : this.getEntidadDestinoProyecto(proyectoRelacion),
      tipoEntidadOrigen: TipoEntidad.PROYECTO,
      tipoEntidadDestino: proyectoRelacion.tipoEntidadRelacionada,
      observaciones: proyectoRelacion.observaciones
    };
  }

  private getEntidadOrigenProyecto(proyectoRelacion: IProyectoRelacionTableData): EntidadRelacionada {
    return proyectoRelacion.tipoRelacion === TipoRelacion.PADRE ? this.proyecto : proyectoRelacion.entidadRelacionada;
  }

  private getEntidadDestinoProyecto(proyectoRelacion: IProyectoRelacionTableData): EntidadRelacionada {
    return proyectoRelacion.tipoRelacion === TipoRelacion.PADRE ? proyectoRelacion.entidadRelacionada : this.proyecto;
  }

  private refreshProyectoRelacionesTableData(
    relacionResponse: IRelacion,
    wrapper: StatusWrapper<IProyectoRelacionTableData>,
    current: StatusWrapper<IProyectoRelacionTableData>[]
  ): void {
    const proyectoRelacion = this.createProyectoRelacionTableDataFromRelacion(relacionResponse);
    this.copyRelatedAttributes(wrapper.value, proyectoRelacion);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoRelacionTableData>(proyectoRelacion);
    this.proyectoRelacionesTableData$.next(current);
  }

  private copyRelatedAttributes(
    source: IProyectoRelacionTableData,
    target: IProyectoRelacionTableData
  ): void {
    target.entidadRelacionada = source.entidadRelacionada;
    target.entidadRelacionadaHref = source.entidadRelacionadaHref;
    target.codigosSge = source.codigosSge;
  }

  private fillGrupoWithTitulo(grupo: IGrupo): IGrupoWithTitulo {
    return {
      ...grupo,
      titulo: grupo.nombre
    };
  }

}
