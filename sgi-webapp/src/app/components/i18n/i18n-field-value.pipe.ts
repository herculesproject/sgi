import { ChangeDetectorRef, Injectable, OnDestroy, Pipe, PipeTransform } from "@angular/core";
import { I18nFieldValue } from "@core/i18n/i18n-field";
import { Language } from "@core/i18n/language";
import { LanguageService } from "@core/services/language.service";
import { Subscription } from "rxjs";

@Injectable()
@Pipe({
  name: 'i18nFieldValue',
  pure: false // required to update the value when the promise is resolved
})
export class I18nFieldValuePipe implements PipeTransform, OnDestroy {
  value: string = '';
  lastLang: Language;
  onLangChange: Subscription;

  constructor(private languageService: LanguageService, private _ref: ChangeDetectorRef) {
  }

  updateValue(field: I18nFieldValue[], lang: Language): void {
    if (field.length) {
      let request = field.filter(f => f.lang === lang);
      if (request.length) {
        this.value = request[0].value;
      }
      else {
        // Get other value
        request = field.filter(f => f.value?.length);
        if (request.length) {
          this.value = request[0].value;
        }
        else {
          this.value = '';
        }
      }
    }
    else {
      this.value = '';
    }
    this.lastLang = lang;
    this._ref.markForCheck();
  }

  transform(query: I18nFieldValue[], ...args: any[]): any {
    if (!Array.isArray(query) || !query.length) {
      return '';
    }

    const requestLang = this.languageService.getLanguage();

    // if we ask another time for the same key, return the last value
    if (this.lastLang === requestLang) {
      return this.value;
    }

    // set the value
    this.updateValue(query, requestLang);

    // if there is a subscription to onLangChange, clean it
    this._dispose();

    // subscribe to onLangChange event, in case the language changes
    if (!this.onLangChange) {
      this.onLangChange = this.languageService.languageChange$.subscribe((language: Language) => {
        if (this.lastLang) {
          this.lastLang = null; // we want to make sure it doesn't return the same value until it's been updated
          this.updateValue(query, language);
        }
      });
    }

    return this.value;
  }

  /**
   * Clean any existing subscription to change events
   */
  private _dispose(): void {
    if (typeof this.onLangChange !== 'undefined') {
      this.onLangChange.unsubscribe();
      this.onLangChange = undefined;
    }
  }

  ngOnDestroy(): void {
    this._dispose();
  }
}