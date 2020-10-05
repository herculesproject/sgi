import { Component, OnDestroy, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IMemoria } from '@core/models/eti/memoria';
import { Comite } from '@core/models/eti/comite';
import { startWith, map } from 'rxjs/operators';
import { Observable, Subscription } from 'rxjs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MemoriaDatosGeneralesFragment } from './memoria-datos-generales.fragment';
import { MemoriaActionService } from '../../memoria.action.service';
import { TipoMemoria } from '@core/models/eti/tipo-memoria';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IPersona } from '@core/models/sgp/persona';



const MSG_ERROR_INIT_ = marker('eti.memoria.datosGenerales.error.init');
const TEXT_USER_TITLE = marker('eti.memoria.datosGenerales.buscador.solicitante');
const TEXT_USER_BUTTON = marker('eti.memoria.datosGenerales.buscador.buscar.solicitante');

@Component({
  selector: 'sgi-memoria-datos-generales',
  templateUrl: './memoria-datos-generales.component.html',
  styleUrls: ['./memoria-datos-generales.component.scss']
})
export class MemoriaDatosGeneralesComponent extends FormFragmentComponent<IMemoria> implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  comites: Comite[] = [];
  tiposMemoria: TipoMemoria[] = [];

  filteredComites: Observable<Comite[]>;
  filteredTipoMemorias: Observable<TipoEstadoMemoria[]>;

  datosGeneralesFragment: MemoriaDatosGeneralesFragment;
  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;

  private subscriptions: Subscription[] = [];

  constructor(
    protected readonly logger: NGXLogger,
    private readonly comiteService: ComiteService,
    private readonly snackBarService: SnackBarService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.datosGeneralesFragment = this.fragment as MemoriaDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.logger.debug(MemoriaDatosGeneralesComponent.name, 'ngOnInit()', 'start');

    this.loadComites();
    this.loadTipoMemorias();

    this.logger.debug(MemoriaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  loadTipoMemorias() {
    this.subscriptions.push(this.tipoEstadoMemoriaService.findAll().subscribe(
      (res) => {
        this.tiposMemoria = res.items;
        this.logger.debug(MemoriaDatosGeneralesComponent.name, 'loadTipoMemorias()', 'start');
        this.filteredTipoMemorias = this.formGroup.controls.tipoEstadoMemoria.valueChanges
          .pipe(
            startWith(''),
            map(value => {
              return this.filterTipoMemorias(value);
            })
          );
      },
      () => {
        this.snackBarService.showError(MSG_ERROR_INIT_);
        this.logger.debug(MemoriaDatosGeneralesComponent.name, 'loadTipoMemorias()', 'end');
      }
    ));
  }

  loadComites() {
    this.subscriptions.push(this.comiteService.findAll().subscribe(
      (res) => {
        this.comites = res.items;
        this.logger.debug(MemoriaDatosGeneralesComponent.name, 'loadComites()', 'start');
        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      },
      () => {
        this.snackBarService.showError(MSG_ERROR_INIT_);
        this.logger.debug(MemoriaDatosGeneralesComponent.name, 'loadComites()', 'end');
      }
    ));
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | Comite): Comite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comites.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }


  private filterTipoMemorias(value: string | TipoEstadoMemoria): TipoEstadoMemoria[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tiposMemoria.filter
      (tipoEstadoMemoria => tipoEstadoMemoria.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: Comite): string {
    return comite?.comite;
  }

  getTipoEstadoMemoria(tipoEstadoMemoria: TipoEstadoMemoria): string {
    return tipoEstadoMemoria?.nombre;
  }

  getPersonaRef(persona: IPersona): string {
    return persona.personaRef;
  }

  getTitulo(titulo: string): string {
    return titulo;
  }

}

