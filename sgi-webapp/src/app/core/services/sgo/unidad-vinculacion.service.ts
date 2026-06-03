import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { environment } from '@env';
import {
  FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById,
  RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult
} from '@herculesproject/framework/http';
import { Observable, of } from 'rxjs';

// tslint:disable-next-line: variable-name
const _UnidadVinculacionServiceMixinBase:
  FindByIdCtor<string, IUnidadVinculacion, IUnidadVinculacion> &
  FindAllCtor<IUnidadVinculacion, IUnidadVinculacion> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class UnidadVinculacionService extends _UnidadVinculacionServiceMixinBase {
  private static readonly MAPPING = '/unidades-vinculacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${UnidadVinculacionService.MAPPING}`,
      http
    );
  }

  /**
   * Busca todas las unidades de vinculación de primer nivel (tipos de unidad).
   *
   * @returns la lista de unidades de vinculación raíz.
   */
  findAllPadres(onlyActive = true): Observable<SgiRestListResult<IUnidadVinculacion>> {
    let filter: SgiRestFilter = new RSQLSgiRestFilter('predecesorId', SgiRestFilterOperator.IS_NULL, '');
    if (onlyActive) {
      filter = filter.and('activo', SgiRestFilterOperator.EQUALS, 'true');
    }

    return this.findAll({ filter });
  }

  /**
   * Busca todas las unidades de vinculación que tengan como predecesor el id indicado.
   *
   * @param id identificador de la unidad de vinculación predecesora.
   * @param onlyActive si es `true` (por defecto) devuelve solo las unidades de vinculación activas.
   * @returns la lista de la unidades de vinculación hijas.
   */
  findAllHijos(id: string, onlyActive = true): Observable<SgiRestListResult<IUnidadVinculacion>> {
    let filter: SgiRestFilter = new RSQLSgiRestFilter('predecesorId', SgiRestFilterOperator.EQUALS, id);
    if (onlyActive) {
      filter = filter.and('activo', SgiRestFilterOperator.EQUALS, 'true');
    }

    return this.findAll({ filter });
  }

  /**
   * Busca las unidades de vinculación cuyos ids estén en la lista indicada.
   *
   * @param ids lista de identificadores únicos de unidades de vinculación.
   * @param onlyActive si es `true` (por defecto) devuelve solo las unidades de vinculación activas.
   * @returns la lista de unidades de vinculación.
   */
  findAllByIdIn(ids: string[], onlyActive = true): Observable<SgiRestListResult<IUnidadVinculacion>> {
    if (!ids?.length) {
      return of({ items: [], page: { index: 0, size: 0, total: 0, count: 0 } } as SgiRestListResult<IUnidadVinculacion>);
    }

    let filter: SgiRestFilter = new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids);
    if (onlyActive) {
      filter = filter.and('activo', SgiRestFilterOperator.EQUALS, 'true');
    }

    return this.findAll({ filter });
  }

}
