import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoProrrogaDatosGeneralesFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-datos-generales/proyecto-prorroga-datos-generales.fragment';
import { ProyectoProrrogaDocumentosFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-documentos/proyecto-prorroga-documentos.fragment';
import { ProyectoProrrogaDocumentoService } from '@core/services/csp/proyecto-prorroga-documento.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoProrroga, TipoProrrogaEnum } from '@core/models/csp/proyecto-prorroga';

const MSG_IMPORTE = marker('csp.proyecto-prorroga.saveOrUpdate.importe.msg');

@Injectable()
export class ProyectoProrrogaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: ProyectoProrrogaDatosGeneralesFragment;
  private documentos: ProyectoProrrogaDocumentosFragment;

  dialogService: DialogService

  constructor(
    private logger: NGXLogger,
    route: ActivatedRoute,
    proyectoProrrogaService: ProyectoProrrogaService,
    periodoSeguimientoDocumentoService: ProyectoProrrogaDocumentoService,
    documentoService: DocumentoService,
    dialogService: DialogService
  ) {
    super();

    this.logger = logger;
    this.dialogService = dialogService;

    if (history.state?.proyectoProrroga?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoProrrogaDatosGeneralesFragment(logger, history.state?.proyectoProrroga?.id, proyectoProrrogaService, history.state?.proyecto, history.state?.selectedProyectoProrrogas, history.state?.proyectoProrroga, history.state?.readonly);
    this.documentos = new ProyectoProrrogaDocumentosFragment(logger, history.state?.proyectoProrroga?.id, proyectoProrrogaService, periodoSeguimientoDocumentoService, documentoService, history.state?.proyecto, history.state?.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }


  saveOrUpdate(): Observable<void> {
    if (this.mostrarAvisoImporte()) {
      return this.dialogService.showConfirmation(MSG_IMPORTE).pipe(
        switchMap(aceptado => {
          if (aceptado) {
            return this.doSaveOrUpdate();
          }
        })
      )
    } else {
      return this.doSaveOrUpdate();
    }
  }

  private doSaveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoProrrogaActionService.name, 'saveOrUpdate()', 'start');
    this.performChecks(true);
    if (this.hasErrors()) {
      this.logger.error(ProyectoProrrogaActionService.name, 'saveOrUpdate()', 'error');
      return throwError('Errores');
    }
    if (this.isEdit()) {
      return this.datosGenerales.saveOrUpdate().pipe(
        switchMap(() => {
          this.datosGenerales.refreshInitialState(true);
          return this.documentos.saveOrUpdate();
        })
      );
    } else {
      return this.datosGenerales.saveOrUpdate().pipe(
        switchMap(() => {
          return this.documentos.saveOrUpdate();
        }),
        tap(() => this.logger.debug(ProyectoProrrogaActionService.name,
          'saveOrUpdate()', 'end'))
      );
    }
  }

  private mostrarAvisoImporte(): boolean {
    const proyectoProrroga = this.datosGenerales.isInitialized() ? this.datosGenerales.getValue() : {} as IProyectoProrroga;
    let mostrarAvisoImporte = false;
    if (proyectoProrroga.tipoProrroga === TipoProrrogaEnum.IMPORTE || proyectoProrroga.tipoProrroga === TipoProrrogaEnum.TIEMPO_IMPORTE) {
      if (history.state?.proyectoProrroga && history.state?.proyectoProrroga.id) {
        if (proyectoProrroga.importe !== history.state?.proyectoProrroga.importe) {
          mostrarAvisoImporte = true;
        }
      } else {
        if (proyectoProrroga.importe !== null) {
          mostrarAvisoImporte = true;
        }
      }
    }
    return mostrarAvisoImporte;
  }

}
