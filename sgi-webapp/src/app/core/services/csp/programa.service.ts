import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { Observable, of } from 'rxjs';
import { IObjectTree } from '@core/models/csp/object-tree';
import { IPrograma } from '@core/models/csp/programa';



const programasTree: IObjectTree[] = [
  {
    nombre: 'Plan nacional 2020-2023',
    children: [
      {
        nombre: 'Programa 1',
        children: [
          { nombre: 'Modalidad K' }
        ]
      },
      { nombre: 'Programa 2' },
    ]
  }, {
    nombre: 'Plan Propio',
    children: [
      {
        nombre: 'Programa ayudas propias',
        children: [
          { nombre: 'Predoctorales' }
        ]
      }

    ]
  },
];

@Injectable({
  providedIn: 'root'
})
export class ProgramaService extends SgiRestService<number, IPrograma> {

  private static readonly MAPPING = '/programa';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProgramaService.name,
      logger,
      `${environment.serviceServers.eti}${ProgramaService.MAPPING}`,
      http
    );
  }


  findProgramasTree(): Observable<SgiRestListResult<IObjectTree>> {
    this.logger.debug(ProgramaService.name, `findProgramasTree()`, '-', 'START');
    return of({
      page: null,
      total: programasTree.length,
      items: programasTree
    } as SgiRestListResult<IObjectTree>);
  }
}
