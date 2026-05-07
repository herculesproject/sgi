import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoGrupo } from '@core/models/csp/tipo-grupo';
import { TipoGrupoService } from '@core/services/csp/tipo-grupo/tipo-grupo.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoGrupoModalComponent } from '../../tipo-grupo/tipo-grupo-modal/tipo-grupo-modal.component';

@Component({
  selector: 'sgi-select-tipo-grupo',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoGrupoComponent
    }
  ]
})
export class SelectTipoGrupoComponent extends SelectServiceExtendedComponent<ITipoGrupo> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoGrupoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoGrupoModalComponent;
    this.sortWith = (o1: SelectValue<ITipoGrupo>, o2: SelectValue<ITipoGrupo>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<ITipoGrupo[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TGIN-C');
  }

}
