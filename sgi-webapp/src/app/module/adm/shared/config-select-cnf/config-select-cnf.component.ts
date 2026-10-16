import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { ConfigService } from '@core/services/cnf/config.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ConfigSelectComponent } from '../config-select/config-select.component';

@Component({
  selector: 'sgi-config-select-cnf',
  templateUrl: './../config-select/config-select.component.html',
  styleUrls: ['./../config-select/config-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigSelectCnfComponent extends ConfigSelectComponent {

  @Output()
  readonly valueChange = new EventEmitter<string>();

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    private readonly configService: ConfigService
  ) {
    super(translate, snackBarService);
  }

  protected getValue(key: string): Observable<IConfigValue> {
    return this.configService.findById(key).pipe(
      tap(configValue => this.valueChange.next(configValue?.value))
    );
  }

  protected updateValue(key: string, newValue: string): Observable<IConfigValue> {
    return this.configService.updateValue(key, newValue).pipe(
      tap(configValue => this.valueChange.next(configValue?.value))
    );
  }

}
