
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { CookieService } from 'ngx-cookie-service';
import { Subject } from 'rxjs';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';

export class Language {

  private constructor(public readonly code: string) {

  }

  public static readonly ES = new Language('es');
  public static readonly EN = new Language('en');
  public static readonly EU = new Language('eu');
}

export const LANGUAGE_MAP: Map<Language, string> = new Map([
  [Language.ES, marker('language.es')],
  [Language.EN, marker('language.en')],
  [Language.EU, marker('language.eu')]
]);

export class LocaleId extends String {
  get onChange$(): Subject<string> {
    return this.languageService.languageChange$;
  }

  constructor(private languageService: LanguageService) {
    super();
  }

  toString(): string {
    return this.languageService.getLanguageCode();
  }

  valueOf(): string {
    return this.toString();
  }
}


@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  private language: Language = Language.ES;
  private readonly COOKIE_PATH: string = '/';
  readonly languageChange$: BehaviorSubject<string> = new BehaviorSubject<string>(Language.ES.code);

  constructor(
    private readonly cookieService: CookieService
  ) {
    if (!cookieService.check('sgi-locale')) {
      cookieService.set('sgi-locale', this.language.code, { path: this.COOKIE_PATH, sameSite: 'Strict' });
    }
    this.switchLanguage(this.getLanguage(cookieService.get('sgi-locale')));
  }

  public getLanguage(code?: string): Language {
    if (code) {
      let lang = this.language;
      switch (code) {
        case Language.ES.code: {
          lang = Language.ES;
          break;
        }
        case Language.EN.code: {
          lang = Language.EN;
          break;
        }
        case Language.EU.code: {
          lang = Language.EU;
          break;
        }
      }
      return lang;
    }
    return this.language;
  }

  public getLocale(): string {
    if (this.language?.code === Language.EU.code) {
      return 'es-EU';
    }
    return this.language.code;
  }

  public getLanguageCode(): string {
    return this.language.code;
  }

  public switchLanguage(language: Language): void {
    this.cookieService.set('sgi-locale', language.code, { path: this.COOKIE_PATH, sameSite: 'Strict' });
    this.language = language;
    this.languageChange$.next(this.language.code);
  }

}