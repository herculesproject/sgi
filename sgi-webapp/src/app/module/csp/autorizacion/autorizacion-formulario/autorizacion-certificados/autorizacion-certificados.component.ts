import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AutorizacionActionService } from '../../autorizacion.action.service';
import { AutorizacionCertificadoModalComponent, ICertificadoAutorizacionModalData } from '../autorizacion-certificado-modal/autorizacion-certificado-modal.component';
import { AutorizacionCertificadosFragment } from './autorizacion-certificados.fragment';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_ERROR_LOAD = marker('error.load');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const AUTORIZACION_KEY = marker('csp.autorizacion');
const CERTIFICADO_KEY = marker('csp.certificado-autorizacion');

@Component({
  selector: 'sgi-autorizacion-certificados',
  templateUrl: './autorizacion-certificados.component.html',
  styleUrls: ['./autorizacion-certificados.component.scss']
})
export class AutorizacionCertificadosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: AutorizacionCertificadosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['nombre', 'publico', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<ICertificadoAutorizacion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;
  textoCrear: string;

  constructor(
    protected actionService: AutorizacionActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private documentoService: DocumentoService,
    private snackBar: SnackBarService
  ) {
    super(actionService.FRAGMENT.CERTIFICADOS, actionService);
    this.formPart = this.fragment as AutorizacionCertificadosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ICertificadoAutorizacion>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.nombre;
          case 'publico':
            return wrapper.value.visible;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.certificadosAutorizacion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CERTIFICADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CERTIFICADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

  }

  /**
   * Apertura de modal de Cetificados Autorizacion
   */
  openModal(value?: StatusWrapper<ICertificadoAutorizacion>): void {
    const data = {
      id: value?.value?.id,
      autorizacion: value?.value?.autorizacion,
      nombre: value?.value?.nombre,
      documento: value?.value?.documento,
      visible: value?.value?.visible,
      hasSomeOtherCertificadoAutorizacionVisible: this.formPart.certificadosAutorizacion$.value.some(certificado =>
        certificado.value.visible && !value?.value?.visible),
    } as ICertificadoAutorizacionModalData;
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(AutorizacionCertificadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (certificado: ICertificadoAutorizacion) => {
        if (certificado) {
          if (value) {
            this.formPart.updateCertificado(new StatusWrapper<ICertificadoAutorizacion>(certificado));
          } else {
            this.formPart.addCertificado(certificado);
          }
        }
      }
    );
  }

  downloadFile(value: ICertificadoAutorizacion): void {
    this.subscriptions.push(this.documentoService.downloadFichero(value.documento.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, value.documento.nombre);
      },
      () => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

  deleteCertificado(wrapper: StatusWrapper<ICertificadoAutorizacion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteCertificado(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
