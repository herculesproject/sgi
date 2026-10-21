import { Component, OnDestroy, OnInit } from '@angular/core';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { ConfigService } from '@core/services/eti/config.service';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { ConfigI18nInputTextComponent } from '../config-i18n-input-text/config-i18n-input-text.component';

@Component({
  selector: 'sgi-config-i18n-ckeditor-eti',
  templateUrl: './config-i18n-ckeditor-eti.component.html',
  styleUrls: ['./config-i18n-ckeditor-eti.component.scss']
})
export class ConfigI18nCkeditorEtiComponent extends ConfigI18nInputTextComponent implements OnInit, OnDestroy {

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly configService: ConfigService,
    protected readonly languageService: LanguageService
  ) {
    super(translate, snackBarService, languageService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getValue(key: string): Observable<II18nConfigValue> {
    return this.configService.findI18nById(key);
  }

  protected updateValue(key: string, newValue: II18nConfigValue): Observable<II18nConfigValue> {
    return this.configService.updateI18nValue(key, newValue);
  }

}
