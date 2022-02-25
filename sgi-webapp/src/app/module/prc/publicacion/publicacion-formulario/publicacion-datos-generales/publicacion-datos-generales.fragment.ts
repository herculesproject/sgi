import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IAutorWithGrupos } from '@core/models/prc/autor';
import { ICampoProduccionCientifica, ICampoProduccionCientificaWithConfiguracion } from '@core/models/prc/campo-produccion-cientifica';
import { IEstadoProduccionCientifica } from '@core/models/prc/estado-produccion-cientifica';
import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { Fragment } from '@core/services/action-service';
import { ProyectoResumenService } from '@core/services/csp/proyecto-resumen/proyecto-resumen.service';
import { AutorService } from '@core/services/prc/autor/autor.service';
import { CampoProduccionCientificaService } from '@core/services/prc/campo-produccion-cientifica/campo-produccion-cientifica.service';
import { ConfiguracionCampoService } from '@core/services/prc/configuracion-campo/configuracion-campo.service';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { ValorCampoService } from '@core/services/prc/valor-campo/valor-campo.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, concatMap, map, mergeMap, tap, toArray } from 'rxjs/operators';

export class PublicacionDatosGeneralesFragment extends Fragment {
  private camposProduccionCientificaWithConfiguracionMap$ =
    new BehaviorSubject<Map<string, ICampoProduccionCientificaWithConfiguracion>>(new Map());
  private indicesImpacto$ = new BehaviorSubject<IIndiceImpacto[]>([]);
  private autores$ = new BehaviorSubject<IAutorWithGrupos[]>([]);
  private proyectos$ = new BehaviorSubject<IProyectoPrc[]>([]);
  private acreditaciones$ = new BehaviorSubject<IAcreditacion[]>([]);
  private valoresCampoMap = new Map<string, IValorCampo[]>();
  private produccionCientifica: IProduccionCientifica;

  get estadoProduccionCientifica(): IEstadoProduccionCientifica {
    return this.produccionCientifica?.estado;
  }

  constructor(
    private id: number,
    private produccionCientifica$: Observable<IProduccionCientifica>,
    private campoProduccionCientificaService: CampoProduccionCientificaService,
    private configuracionCampo: ConfiguracionCampoService,
    private valorCampoService: ValorCampoService,
    private produccionCientificaService: ProduccionCientificaService,
    private personaService: PersonaService,
    private proyectoResumenService: ProyectoResumenService,
    private documentoService: DocumentoService,
    private autorService: AutorService,
  ) {
    super(id);
    this.subscriptions.push(this.produccionCientifica$
      .subscribe(produccionCientifica => this.produccionCientifica = produccionCientifica)
    );
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    return forkJoin({
      camposProduccionCientificaMap: this.initializeCamposProduccionCientifica(),
      indicesImpacto: this.initializeIndicesImpacto(),
      autores: this.initializeAutores(),
      proyectos: this.initializeProyectos(),
      acreditaciones: this.initializeAcreditaciones()
    }).pipe(
      tap(({ camposProduccionCientificaMap }) => this.camposProduccionCientificaWithConfiguracionMap$.next(camposProduccionCientificaMap)),
      tap(({ indicesImpacto }) => this.indicesImpacto$.next(indicesImpacto)),
      tap(({ autores }) => this.autores$.next(autores)),
      tap(({ proyectos }) => this.proyectos$.next(proyectos)),
      tap(({ acreditaciones }) => this.acreditaciones$.next(acreditaciones)),
    );
  }

  private initializeCamposProduccionCientifica(): Observable<Map<string, ICampoProduccionCientificaWithConfiguracion>> {
    return this.campoProduccionCientificaService
      .findAllCamposProduccionCientifca(this.produccionCientifica)
      .pipe(
        map(camposProduccionCientifica =>
          camposProduccionCientifica.map(
            campo => {
              campo.produccionCientifica = this.produccionCientifica;
              return campo;
            })),
        mergeMap(camposProduccionCientifica =>
          from(camposProduccionCientifica).pipe(
            mergeMap(campo =>
              this.configuracionCampo.findConfiguracionCampo(campo)
                .pipe(
                  map(configuracion => (
                    {
                      campoProduccionCientifica: campo,
                      configuracionCampo: configuracion
                    } as ICampoProduccionCientificaWithConfiguracion)
                  )
                )
            ),
            toArray()
          )
        ),
        map(camposProduccionCientifica =>
          new Map(camposProduccionCientifica.map(campo => [campo.campoProduccionCientifica.codigo, campo]))
        )
      );
  }

  private initializeIndicesImpacto(): Observable<IIndiceImpacto[]> {
    return this.produccionCientificaService.findIndicesImpacto(this.produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(indiceImpacto => {
          indiceImpacto.produccionCientifica = this.produccionCientifica;
          return indiceImpacto;
        })),
        catchError(() => of([]))
      );
  }

  private initializeAutores(): Observable<IAutorWithGrupos[]> {
    return this.produccionCientificaService.findAutores(this.produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(autor => {
          autor.produccionCientifica = this.produccionCientifica;
          return autor;
        })),
        concatMap(autores => from(autores)
          .pipe(
            mergeMap(autor => {
              if (autor.persona?.id) {
                return this.personaService.findById(autor.persona.id)
                  .pipe(
                    map(persona => {
                      autor.persona = persona;
                      return autor;
                    }),
                    catchError(() => of(autor))
                  );
              } else {
                return of(autor);
              }
            }),
            mergeMap(autor => this.autorService.findGrupos(autor.id)
              .pipe(
                map(({ items }) => ({
                  autor,
                  grupos: items
                } as IAutorWithGrupos)),
                catchError(() => of({ autor, grupos: [] }))
              )
            ),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }

  private initializeProyectos(): Observable<IProyectoPrc[]> {
    return this.produccionCientificaService.findProyectos(this.produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(proyecto => {
          proyecto.produccionCientifica = this.produccionCientifica;
          return proyecto;
        })),
        concatMap(proyectos => from(proyectos)
          .pipe(
            // TODO obtener sólo los datos necesarios del proyecto para PRC
            mergeMap(proyecto => this.proyectoResumenService.findById(proyecto.id)
              .pipe(
                map(proyectoResumen => {
                  proyecto.proyecto = proyectoResumen;
                  return proyecto;
                }),
                catchError(() => of(proyecto)),
              )
            ),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }

  private initializeAcreditaciones(): Observable<IAcreditacion[]> {
    return this.produccionCientificaService.findAcreditaciones(this.produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(acreditacion => {
          acreditacion.produccionCientifica = this.produccionCientifica;
          return acreditacion;
        })),
        concatMap(acreditaciones => from(acreditaciones)
          .pipe(
            mergeMap(acreditacion => {
              if (acreditacion.documento?.documentoRef) {
                return this.documentoService.getInfoFichero(acreditacion.documento.documentoRef)
                  .pipe(
                    map(documento => {
                      acreditacion.documento = documento;
                      return acreditacion;
                    }),
                    catchError(() => of(acreditacion))
                  );
              } else {
                return of(acreditacion);
              }
            }),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }

  getCamposProduccionCientificaWithConfiguracionMap$(): Observable<Map<string, ICampoProduccionCientificaWithConfiguracion>> {
    return this.camposProduccionCientificaWithConfiguracionMap$.asObservable();
  }

  getIndicesImpacto$(): Observable<IIndiceImpacto[]> {
    return this.indicesImpacto$.asObservable();
  }

  getAutores$(): Observable<IAutorWithGrupos[]> {
    return this.autores$.asObservable();
  }

  getProyectos$(): Observable<IProyectoPrc[]> {
    return this.proyectos$.asObservable();
  }

  getAcreditaciones$(): Observable<IAcreditacion[]> {
    return this.acreditaciones$.asObservable();
  }

  getValoresCampo$ = (campoProduccionCientifica: ICampoProduccionCientifica) => {
    const valoresCampo = this.valoresCampoMap.get(campoProduccionCientifica.codigo);
    if (!valoresCampo) {
      return this.valorCampoService.findAllValorCampo(campoProduccionCientifica).pipe(
        tap(valoresCampoFetched => this.valoresCampoMap.set(campoProduccionCientifica.codigo, valoresCampoFetched))
      );
    } else {
      return of(valoresCampo);
    }
  }
}
