import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FormGroupUtil} from '@shared/config/form-group-util';
import {Registro} from '@core/models/registro';
import {RegistroFilter} from '@core/filters/registro.filter';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {NGXLogger} from 'ngx-logger';
import {TraductorService} from '@core/services/traductor.service';
import {SolicitudService} from '@core/services/solicitud.service';
import {FxFlexProperties} from '@core/models/flexLayout/fx-flex-properties';
import {FxLayoutProperties} from '@core/models/flexLayout/fx-layout-properties';
import {UrlUtils} from '@core/utils/url-utils';
import {SnackBarService} from '@core/services/snack-bar.service';
import {map, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-solicitud-actualizar',
  templateUrl: './solicitud-actualizar.component.html',
  styleUrls: ['./solicitud-actualizar.component.scss'],
})
export class SolicitudActualizarComponent implements OnInit {
  formGroup: FormGroup;
  FormGroupUtil = FormGroupUtil;
  registro: Registro;
  usuarioRef = 'user-1090';

  desactivarAceptar: boolean;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

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
    let id: number;
    // Obtiene el id
    this.activatedRoute.params
      .pipe(
        switchMap((params: Params) => {
          id = Number(params.id);
          if (id) {
            return this.solicitudService.getOne(id);
          } else {
            const registroFilter = new RegistroFilter();
            registroFilter.usuarioRef = usuarioRef;
            // TODO rehacer cuando esté hecho el filtrado
            return this.solicitudService.findAll(registroFilter).pipe(
              map(reg => reg.filter(r => r.usuarioRef === usuarioRef))
            );
          }
        })
      )
      .subscribe(
        // Obtiene el objeto existente
        (registro: Registro) => {
          this.registro = registro[0];
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
        },
        // Si no encuentra
        () => {
          // Añadimos esta comprobación para que no nos eche al crear uno nuevo
          if (id) {
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto('solicitud.actualizar.no-encontrado')
            );
            this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.solicitud}`).then();
          }
          this.logger.debug(
            SolicitudActualizarComponent.name,
            'getSolicitud()',
            'end'
          );
        }
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
    if (FormGroupUtil.validFormGroup(this.formGroup)) {
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
        this.traductor.getTexto('solicitud.actualizar.no-encontrado')
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
    this.solicitudService
      .update(this.registro, this.registro.id)
      .subscribe(
        () => {
          this.snackBarService.mostrarMensajeSuccess(
            this.traductor.getTexto('solicitud.actualizar.normativa.correcto')
          );
          this.router.navigateByUrl(`${UrlUtils.cat}/${UrlUtils.solicitud}`).then();
          this.desactivarAceptar = false;
          this.logger.debug(
            SolicitudActualizarComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        },
        () => {
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('solicitud.actualizar.normativa.error')
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
}
