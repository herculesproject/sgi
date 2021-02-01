import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolSocioService } from '@core/services/csp/rol-socio.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, Subscription } from 'rxjs';
import { map, startWith, tap } from 'rxjs/operators';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { SolicitudProyectoSocioDatosGeneralesFragment } from './solicitud-proyecto-socio-datos-generales.fragment';


const MSG_ERROR_INIT = marker('csp.solicitud-proyecto-socio.rol.error.cargar');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-datos-generales',
  templateUrl: './solicitud-proyecto-socio-datos-generales.component.html',
  styleUrls: ['./solicitud-proyecto-socio-datos-generales.component.scss']
})
export class SolicitudProyectoSocioDatosGeneralesComponent extends FormFragmentComponent<ISolicitudProyectoSocio>
  implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  private rolSocioFiltered: IRolSocio[] = [];
  rolSocios$: Observable<IRolSocio[]>;

  constructor(
    private readonly logger: NGXLogger,
    protected actionService: SolicitudProyectoSocioActionService,
    private snackBarService: SnackBarService,
    private rolSocioService: RolSocioService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudProyectoSocioDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadRolProyectos();
    this.subscriptions.push(
      merge(
        this.formGroup.get('empresa').valueChanges,
        this.formGroup.get('mesInicio').valueChanges,
        this.formGroup.get('mesFin').valueChanges
      ).pipe(
        tap(() => this.checkRangesMeses())
      ).subscribe()
    );
  }

  private loadRolProyectos(): void {
    const subcription = this.rolSocioService.findAll().pipe(
      map(result => result.items)
    ).subscribe(
      res => {
        this.rolSocioFiltered = res;
        this.rolSocios$ = this.formGroup.get('rolSocio').valueChanges.pipe(
          startWith(''),
          map(value => this.filtroRolProyecto(value))
        );
      },
      error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      }
    );
    this.subscriptions.push(subcription);
  }

  private filtroRolProyecto(value: string): IRolSocio[] {
    const filterValue = value.toString().toLowerCase();
    return this.rolSocioFiltered.filter(
      rolSocio => rolSocio.nombre.toLowerCase().includes(filterValue));
  }

  getNombreRolSocio(rolProyecto?: IRolSocio): string {
    return typeof rolProyecto === 'string' ? rolProyecto : rolProyecto?.nombre;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private checkRangesMeses(): void {
    const empresa = this.formGroup.get('empresa');
    const mesInicioForm = this.formGroup.get('mesInicio');
    const mesFinForm = this.formGroup.get('mesFin');

    const proyectoSocios = this.actionService.getSelectedSolicitudProyectoSocios().filter(
      element => element.empresa.personaRef === empresa.value.personaRef
        && element.id !== this.formPart.solicitudProyectoSocio.id);
    if (proyectoSocios.length > 0) {
      const mesInicio = mesInicioForm.value;
      const mesFin = mesFinForm.value;

      // Comprueba si no se indicaron fechas no haya otros registros con ellas
      if (!mesInicio && !mesFin ||
        proyectoSocios.find(proyectoSocio => !proyectoSocio.mesInicio && !proyectoSocio.mesFin)) {
        empresa.setErrors({ contains: true });
        empresa.markAsTouched({ onlySelf: true });
      } else {
        empresa.setErrors(null);
      }

      // Comprueba solapamiento de mesFin
      if (mesFin) {
        for (const proyectoSocio of proyectoSocios) {
          if (mesFin >= proyectoSocio.mesInicio && mesFin <= proyectoSocio.mesFin) {
            mesFinForm.setErrors({ range: true });
            mesFinForm.markAsTouched({ onlySelf: true });
            break;
          }
        }
      }

      // Comprueba solapamiento de mesInicio
      if (mesInicio) {
        for (const proyectoSocio of proyectoSocios) {
          if (mesInicio >= proyectoSocio.mesInicio && mesInicio <= proyectoSocio.mesFin) {
            mesInicioForm.setErrors({ range: true });
            mesInicioForm.markAsTouched({ onlySelf: true });
            break;
          }
        }
      }
    } else {
      empresa.setErrors(null);
      mesFinForm.setErrors(null);
      mesInicioForm.setErrors(null);
    }
  }
}
