import { Injectable, OnDestroy } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { marker } from "@biesbjerg/ngx-translate-extract-marker";
import { ActionService } from "@core/services/action-service";
import { AutorizacionService } from "@core/services/csp/autorizacion/autorizacion.service";
import { ConvocatoriaService } from "@core/services/csp/convocatoria.service";
import { EstadoAutorizacionService } from "@core/services/csp/estado-autorizacion/estado-autorizacion.service";
import { DialogService } from "@core/services/dialog.service";
import { EmpresaService } from "@core/services/sgemp/empresa.service";
import { PersonaService } from "@core/services/sgp/persona.service";
import { NGXLogger } from "ngx-logger";
import { Observable, of, throwError } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { AutorizacionDatosGeneralesFragment } from "./autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.fragment";
import { AUTORIZACION_ROUTE_PARAMS } from "./autorizacion-route-params";

const MSG_REGISTRAR = marker('msg.csp.autorizacion.presentar');

@Injectable()
export class AutorizacionActionService extends
  ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };

  private datosGenerales: AutorizacionDatosGeneralesFragment;

  private dialogService: DialogService;


  public readonly id: number;


  constructor(
    fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaService: ConvocatoriaService,
    private autorizacionService: AutorizacionService,
    personaService: PersonaService,
    empresaService: EmpresaService,
    estadoAutorizacionService: EstadoAutorizacionService,
    dialogService: DialogService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(AUTORIZACION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.enableEdit();
    }

    this.dialogService = dialogService;
    this.datosGenerales = new AutorizacionDatosGeneralesFragment(logger, this.id, autorizacionService, personaService, empresaService, estadoAutorizacionService, convocatoriaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.datosGenerales.initialize();
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(tap(() => this.datosGenerales.refreshInitialState(true))))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    } else {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(
            tap((key) => {
              this.datosGenerales.refreshInitialState(true);
              if (typeof key === 'string' || typeof key === 'number') {
                this.onKeyChange(key);
              }
            })
          ))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    }
  }


  /**
   * Acción de presentacion de una autorizacion
   */
  presentar(): Observable<void> {
    return this.dialogService.showConfirmation(MSG_REGISTRAR).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.autorizacionService.presentar(this.id);
        }
      })
    );
  }
}
