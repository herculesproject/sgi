import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoDescriptorGrupoModalComponent } from '../../tipo-descriptor-grupo/tipo-descriptor-grupo-modal/tipo-descriptor-grupo-modal.component';

@Component({
  selector: 'sgi-select-tipo-descriptor-grupo',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoDescriptorGrupoComponent
    }
  ]
})
export class SelectTipoDescriptorGrupoComponent extends SelectServiceExtendedComponent<ITipoDescriptorGrupo> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoDescriptorGrupoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoDescriptorGrupoModalComponent;
    this.sortWith = (
      o1: SelectValue<ITipoDescriptorGrupo>,
      o2: SelectValue<ITipoDescriptorGrupo>
    ) => o1?.displayText.localeCompare(o2?.displayText);
  }

  protected loadServiceOptions(): Observable<ITipoDescriptorGrupo[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TDESG-C');
  }

}
