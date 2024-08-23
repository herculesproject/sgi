import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IPrograma } from '@core/models/csp/programa';
import { ProgramaService } from '@core/services/csp/programa.service';
import { LanguageService } from '@core/services/language.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-programa',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProgramaComponent
    }
  ]
})
export class SelectProgramaComponent extends SelectServiceComponent<IPrograma> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private service: ProgramaService,
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
  }

  protected loadServiceOptions(): Observable<IPrograma[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAllPlan(findOptions).pipe(map(response => response.items));
  }

}
