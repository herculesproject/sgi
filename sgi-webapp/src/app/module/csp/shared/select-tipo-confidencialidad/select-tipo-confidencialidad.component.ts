import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { TipoConfidencialidadService } from '@core/services/csp/tipo-confidencialidad/tipo-confidencialidad.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoConfidencialidadModalComponent } from '../../tipo-confidencialidad/tipo-confidencialidad-modal/tipo-confidencialidad-modal.component';

@Component({
  selector: 'sgi-select-tipo-confidencialidad',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoConfidencialidadComponent
    }
  ]
})
export class SelectTipoConfidencialidadComponent extends SelectServiceExtendedComponent<ITipoConfidencialidad> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoConfidencialidadService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoConfidencialidadModalComponent;
    this.sortWith = (
      o1: SelectValue<ITipoConfidencialidad>,
      o2: SelectValue<ITipoConfidencialidad>
    ) => o1?.displayText.localeCompare(o2?.displayText);
  }

  protected loadServiceOptions(): Observable<ITipoConfidencialidad[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TCONF-C');
  }

}
