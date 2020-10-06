import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { NGXLogger } from 'ngx-logger';
import { MatDialog } from '@angular/material/dialog';
import { MemoriaActionService } from '../../memoria.action.service';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { BehaviorSubject, Subscription } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { MemoriaDocumentacionFragment } from './memoria-documentacion.fragment';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MemoriaDocumentacionMemoriaModalComponent } from '../../modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IFormulario } from '@core/models/eti/formulario';
import { MemoriaDocumentacionSeguimientosModalComponent } from '../../modals/memoria-documentacion-seguimientos-modal/memoria-documentacion-seguimientos-modal.component';

const MSG_CONFIRM_DELETE = marker('eti.memoria.documentacion.listado.eliminar');

@Component({
  selector: 'sgi-memoria-documentacion',
  templateUrl: './memoria-documentacion.component.html',
  styleUrls: ['./memoria-documentacion.component.scss']
})
export class MemoriaDocumentacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: MemoriaDocumentacionFragment;

  private subscriptions: Subscription[] = [];

  dataSourceDocumentoMemoria: MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>;
  dataSourceSeguimientoAnual: MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>;
  dataSourceSeguimientoFinal: MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>;
  dataSourceRetrospectiva: MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>;

  @ViewChild('paginatorDocumentacionMemoria', { static: true }) paginatorDocumentacionMemoria: MatPaginator;
  @ViewChild('paginatorSeguimientoAnual', { static: true }) paginatorSeguimientoAnual: MatPaginator;
  @ViewChild('paginatorSeguimientoFinal', { static: true }) paginatorSeguimientoFinal: MatPaginator;
  @ViewChild('paginatorRetrospectiva', { static: true }) paginatorRetrospectiva: MatPaginator;

  @ViewChild('sortDocumentacionMemoria', { static: true }) sortDocumentacionMemoria: MatSort;
  @ViewChild('sortSeguimientoAnual', { static: true }) sortSeguimientoAnual: MatSort;
  @ViewChild('sortSeguimientoFinal', { static: true }) sortSeguimientoFinal: MatSort;
  @ViewChild('sortRetrospectiva', { static: true }) sortRetrospectiva: MatSort;


  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  totalElementosDocumentoMemoria: number = 0;
  displayedColumnsDocumentoMemoria: string[] = ['documentoRef', 'tipoDocumento.nombre', 'aportado', 'acciones'];
  elementosPaginaDocumentoMemoria: number[] = [5, 10, 25, 100];

  totalElementosSeguimientoAnual: number = 0;
  displayedColumnsSeguimientoAnual: string[] = ['documentoRef', 'acciones'];
  elementosPaginaSeguimientoAnual: number[] = [5, 10, 25, 100];

  totalElementosSeguimientoFinal: number = 0;
  displayedColumnsSeguimientoFinal: string[] = ['documentoRef', 'acciones'];
  elementosPaginaSeguimientoFinal: number[] = [5, 10, 25, 100];

  totalElementosRetrospectiva: number = 0;
  displayedColumnsRetrospectiva: string[] = ['documentoRef', 'acciones'];
  elementosPaginaRetrospectiva: number[] = [5, 10, 25, 100];

  documentacionesMemoria$: BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>;
  documentacionesSeguimientoAnual$: BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>;
  documentacionesSeguimientoFinal$: BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>;
  documentacionesRetrospectiva$: BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>;


  estadoMemoria: TipoEstadoMemoria;
  formulario: IFormulario;


  constructor(
    protected readonly dialogService: DialogService, protected readonly logger: NGXLogger,
    protected matDialog: MatDialog,
    actionService: MemoriaActionService) {

    super(actionService.FRAGMENT.DOCUMENTACION, actionService);

    this.formPart = this.fragment as MemoriaDocumentacionFragment;

    this.documentacionesMemoria$ = (this.fragment as MemoriaDocumentacionFragment).documentacionesMemoria$;
    this.documentacionesSeguimientoAnual$ = (this.fragment as MemoriaDocumentacionFragment).documentacionesSeguimientoAnual$;
    this.documentacionesSeguimientoFinal$ = (this.fragment as MemoriaDocumentacionFragment).documentacionesSeguimientoFinal$;
    this.documentacionesRetrospectiva$ = (this.fragment as MemoriaDocumentacionFragment).documentacionesRetrospectiva$;


    this.estadoMemoria = actionService.estadoActualMemoria;
  }

  ngOnInit(): void {

    this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();


    this.dataSourceDocumentoMemoria = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
    this.dataSourceDocumentoMemoria.paginator = this.paginatorDocumentacionMemoria;
    this.dataSourceDocumentoMemoria.sort = this.sortDocumentacionMemoria;
    this.formPart.documentacionesMemoria$.subscribe(elements => {
      this.dataSourceDocumentoMemoria.data = elements;
      this.totalElementosDocumentoMemoria = elements.length;


      this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'end');
    });


    this.dataSourceSeguimientoAnual = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
    this.dataSourceSeguimientoAnual.paginator = this.paginatorSeguimientoAnual;
    this.dataSourceSeguimientoAnual.sort = this.sortSeguimientoAnual;
    // this.formPart.documentacionesSeguimientoAnual$.subscribe(elements => {
    // this.dataSourceSeguimientoAnual.data = elements;
    //this.totalElementosSeguimientoAnual = elements.length;
    //this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'end');
    //});

    this.dataSourceSeguimientoFinal = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
    this.dataSourceSeguimientoFinal.paginator = this.paginatorSeguimientoFinal;
    this.dataSourceSeguimientoFinal.sort = this.sortSeguimientoFinal;
    // this.formPart.documentacionesSeguimientoFinal$.subscribe(elements => {
    //   this.dataSourceSeguimientoFinal.data = elements;
    //   this.totalElementosSeguimientoFinal = elements.length;
    //   this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'end');
    // });

    this.dataSourceRetrospectiva = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
    this.dataSourceRetrospectiva.paginator = this.paginatorRetrospectiva;
    this.dataSourceRetrospectiva.sort = this.sortRetrospectiva;
    // this.formPart.documentacionesRetrospectiva$.subscribe(elements => {
    //   this.dataSourceRetrospectiva.data = elements;
    //   this.totalElementosRetrospectiva = elements.length;
    //   this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'end');
    // });



    this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnInit()', 'end');

  }



  /**
   * Apertura de modal de añadir documentación memoria
   */
  openModalDocumentacionMemoria(): void {
    this.logger.debug(MemoriaDocumentacionComponent.name, 'openModalDocumentacionMemoria()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: this.dataSourceDocumentoMemoria.data,
      autoFocus: false
    };
    this.matDialog.open(MemoriaDocumentacionMemoriaModalComponent, config);

    this.logger.debug(MemoriaDocumentacionComponent.name, 'openModalDocumentacionMemoria()', 'end');

  }



  openModalDocumentacionSeguimiento(tipoSeguimiento: string, documentacion?: StatusWrapper<IDocumentacionMemoria>): void {
    this.logger.debug(MemoriaDocumentacionComponent.name, 'openModalDocumentacionSeguimiento()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: documentacion ? documentacion.value : {} as IDocumentacionMemoria,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(MemoriaDocumentacionSeguimientosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (documentacionSeguimiento: IDocumentacionMemoria) => {
        if (documentacionSeguimiento) {
          if (documentacion) {
            if (!documentacion.created) {
              documentacion.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            // TODO coger estos datos del back

            this.formPart.addDocumentacionSeguimiento(documentacionSeguimiento, tipoSeguimiento);
          }
        }
        this.logger.debug(MemoriaDocumentacionComponent.name, 'openModalDocumentacionSeguimiento()', 'end');
      }
    );


    this.logger.debug(MemoriaDocumentacionComponent.name, 'openModalDocumentacionSeguimiento()', 'end');

  }



  /**
   * Elimina la documentación.
   *
   * @param wrappedDocumentacion documentación a eliminar.
   */
  deleteDocumentacionInicial(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    this.logger.debug(MemoriaDocumentacionComponent.name,
      'delete(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>) - start');

    const dialogSubscription = this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe((aceptado) => {
      if (aceptado) {
        wrappedDocumentacion.value.aportado = false;
        wrappedDocumentacion.setEdited();
      }
    });

    this.subscriptions.push(dialogSubscription);

    this.logger.debug(MemoriaDocumentacionComponent.name,
      'delete(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>) - end');
  }


  /**
   * Elimina la documentación.
   *
   * @param wrappedDocumentacion documentación a eliminar.
   */
  deleteDocumentacionSeguimiento(tipoSeguimiento: string, wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    this.logger.debug(MemoriaDocumentacionComponent.name,
      'delete(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>) - start');

    const dialogSubscription = this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe((aceptado) => {
      if (aceptado) {
        wrappedDocumentacion.value.aportado = false;
        wrappedDocumentacion.setEdited();
      }
    });

    this.formPart.deletedDocumentacionSeguimiento(tipoSeguimiento, wrappedDocumentacion);

    this.subscriptions.push(dialogSubscription);

    this.logger.debug(MemoriaDocumentacionComponent.name,
      'delete(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(MemoriaDocumentacionComponent.name, 'ngOnDestroy()', 'end');
  }


  /**
   * Devuelve si una memoria se encuentra en el estado de añadir documentación inicial.
   * EN ELABORACION, COMPLETADA, FAVORABLE PENDIENTE DE MODIFICACIONES MINIMAS,
   * PENDIENTE DE CORRECCIONES, NO PROCEDE EVALUAR
   *
   */
  hasPermisoUpdateDocumentacionInicial(): boolean {
    return this.estadoMemoria.id === 1 || this.estadoMemoria.id === 2
      || this.estadoMemoria.id === 6 || this.estadoMemoria.id === 7
      || this.estadoMemoria.id === 8;
  }
}
