import { HttpClient } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { Language } from '@core/i18n/language';
import { IConfigValue } from '@core/models/cnf/config-value';
import { environment } from '@env';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  private language: Language = Language.ES;
  private availableLanguages: Language[] = [Language.ES];
  private availableLanguages$: BehaviorSubject<Language[]> = new BehaviorSubject<Language[]>(this.availableLanguages);
  private cookieService: CookieService;
  private translateService: TranslateService;

  readonly languageChange$: BehaviorSubject<Language> = new BehaviorSubject<Language>(Language.ES);

  constructor(private injector: Injector, private http: HttpClient) {

  }

  async _bootstrap() {
    this.loadAvailableLanguages().subscribe(
      (languages) => {
        this.availableLanguages = languages;
        this.availableLanguages$.next(this.availableLanguages);
        this.initialize();
      },
      (error) => {
        console.log("Error obteniendo los idiomas disponibles. Se inicia con un idioma por defecto: " + error);
        this.initialize();
      }
    );
    this.cookieService = this.injector.get<CookieService>(CookieService);
    this.translateService = this.injector.get<TranslateService>(TranslateService);
  }

  public getAvailableLanguages(): Language[] {
    return this.availableLanguages;
  }

  public onAvailableLanguagesChange(): Observable<Language[]> {
    return this.availableLanguages$.asObservable();
  }

  public getLanguage(): Language {
    return this.language;
  }

  public switchLanguage(language: Language, updateCookie = true): void {
    this.language = language;
    this.translateService.use(language.code);
    this.languageChange$.next(this.language);
    if (updateCookie) {
      this.setCookieLanguage(language);
    }
  }

  public refresh(): void {
    this._bootstrap().then();
  }

  public getFieldValue(field: I18nFieldValue[]): string {
    const f = this.getField(field);
    if (f) {
      return f.value;
    }
    return '';
  }

  public getField<T extends I18nFieldValue>(field: T[]): T {
    if (!Array.isArray(field)) {
      return null;
    }
    let fieldValue = field.filter(f => f.lang === this.language);
    if (fieldValue.length) {
      return fieldValue[0];
    }
    else {
      fieldValue = field.filter(f => f.value?.length);
      if (fieldValue.length) {
        return fieldValue[0];
      }
      else {
        return null;
      }
    }
  }

  private loadAvailableLanguages(): Observable<Language[]> {
    return this.http.get<IConfigValue>(`${environment.serviceServers.cnf}/public/config/web-languages-header`).pipe(
      map(response => response.value.split(',').map(m => Language.fromCode(m)))
    );
  }

  private initialize(): void {
    const cookieLanguage = Language.fromCode(this.cookieService.get('sgi-locale'));
    const browserLanguage = Language.fromCode(this.translateService.getBrowserLang());
    const defaultLanguage = Language.fromCode(this.translateService.getDefaultLang())
    if (this.availableLanguages.includes(cookieLanguage)) {
      this.switchLanguage(cookieLanguage, false)
    }
    else if (this.availableLanguages.includes(browserLanguage)) {
      this.switchLanguage(browserLanguage, true);
    }
    else if (this.availableLanguages.includes(defaultLanguage)) {
      this.switchLanguage(defaultLanguage, true)
    }
    else {
      this.switchLanguage(this.availableLanguages[0], true);
    }
  }

  private setCookieLanguage(language: Language): void {
    this.cookieService.set('sgi-locale', language.code, { path: '/', sameSite: 'Strict' });
  }
}