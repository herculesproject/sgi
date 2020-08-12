import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Registro } from '@core/models/cat/registro';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { SolicitudService } from '@core/services/cat/solicitud.service';
import { FormGroupUtil } from '@core/services/form-group-util';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { SgiRestFilterType, SgiRestFindOptions } from '@sgi/framework/http';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-solicitud-actualizar',
  templateUrl: './solicitud-actualizar.component.html',
  styleUrls: ['./solicitud-actualizar.component.scss'],
})
export class SolicitudActualizarComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  registro: Registro;
  usuarioRef = 'user-1090';

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  solicitudServiceGetSubscription: Subscription;
  solicitudServiceUpdateSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly solicitudService: SolicitudService,
    private readonly traductor: TraductorService,
    private snackBarService: SnackBarService
  ) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'ngOnInit()',
      'start'
    );
    this.desactivarAceptar = false;
    this.registro = new Registro();
    this.getSolicitud(this.usuarioRef);
    this.formGroup = new FormGroup({
      aceptaCondiciones: new FormControl(this.registro.aceptaCondiciones),
      servicio: new FormControl(this.registro.servicio),
    });
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'ngOnInit()',
      'end'
    );
  }

  /**
   * Obtiene los datos del registro a actualizar si existe
   */
  getSolicitud(usuarioRef: string): void {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'getSolicitud()',
      'start'
    );
    const findOptions: SgiRestFindOptions = {
      filters: [
        {
          field: 'usuarioRef',
          type: SgiRestFilterType.EQUALS,
          value: usuarioRef,
        }
      ]
    };
    this.solicitudServiceGetSubscription = this.solicitudService.findAll(findOptions).subscribe((response) => {
      this.registro = response.items[0];
      this.solicitudService.registro = this.registro;
      // Actualiza el formGroup
      FormGroupUtil.setValue(
        this.formGroup,
        'aceptaCondiciones',
        this.registro.aceptaCondiciones
      );
      FormGroupUtil.setValue(
        this.formGroup,
        'servicio',
        this.registro.servicio
      );
      FormGroupUtil.setValue(
        this.formGroup,
        'registro',
        this.registro
      );
      this.logger.debug(
        SolicitudActualizarComponent.name,
        'getSolicitud()',
        'start'
      );
      // Return the values
      return this.registro;
    },
      catchError(() => {
        // On error reset pagination values
        // Añadimos esta comprobación para que no nos eche al crear uno nuevo
        this.snackBarService.mostrarMensajeSuccess(
          this.traductor.getTexto('cat.solicitud.actualizar.no-encontrado')
        );
        this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.solicitud}`).then();

        this.logger.debug(
          SolicitudActualizarComponent.name,
          'getSolicitud()',
          'end'
        );
        return of([]);
      })
    );
  }

  /**
   * Comprueba el formulario enviado por el usuario.
   * Si todos los datos son correctos, envia la información al back.
   * En caso contrario, avisa al usuario que campos son los incorrectos.
   */
  enviarForm(): void {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'sendForm()',
      'start'
    );
    if (FormGroupUtil.valid(this.formGroup)) {
      this.enviarApi();
    } else {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('form-group.error')
      );
    }
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'sendForm()',
      'end'
    );
  }

  /**
   * Envia los datos al back para crear o actualizar un registro
   */
  private enviarApi(): void {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'sendApi()',
      'start'
    );
    this.getDatosForm();
    this.desactivarAceptar = true;

    // Si no tiene id, mensaje de no encontrado
    if (this.registro.id === null) {
      this.snackBarService.mostrarMensajeError(
        this.traductor.getTexto('cat.solicitud.actualizar.no-encontrado')
      );
    }
    // Si tiene id, lo actualizamos
    else {
      this.actualizarSolicitud();
    }
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'sendApi()',
      'end'
    );
  }

  /**
   * Actualiza un registro existente en el back
   */
  private actualizarSolicitud() {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'actualizarSolicitud()',
      'start'
    );
    this.registro.servicio = this.solicitudService.registro.servicio;
    this.solicitudServiceGetSubscription = this.solicitudService
      .update(this.registro.id, this.registro)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('cat.solicitud.actualizar.normativa.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat.root}/${UrlUtils.cat.solicitud}`).then();
          this.desactivarAceptar = false;
          this.logger.debug(
            SolicitudActualizarComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('cat.solicitud.actualizar.normativa.error')
          );
          this.desactivarAceptar = false;
          this.logger.debug(
            SolicitudActualizarComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        }
      );
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): void {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'createData()',
      'start'
    );
    this.registro.aceptaCondiciones = FormGroupUtil.getValue(
      this.formGroup,
      'aceptaCondiciones'
    );
    this.registro.servicio = this.solicitudService.registro.servicio;
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'createData()',
      'end'
    );
  }


  ngOnDestroy(): void {
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'ngOnDestroy()',
      'start'
    );
    this.solicitudServiceGetSubscription?.unsubscribe();
    this.solicitudServiceUpdateSubscription?.unsubscribe();
    this.logger.debug(
      SolicitudActualizarComponent.name,
      'ngOnDestroy()',
      'end'
    );
  }
}
