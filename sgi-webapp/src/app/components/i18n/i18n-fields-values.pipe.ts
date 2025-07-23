import { ChangeDetectorRef, Injectable, OnDestroy, Pipe, PipeTransform } from "@angular/core";
import { I18nFieldValue } from "@core/i18n/i18n-field";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { Language } from "@core/i18n/language";
import { LanguageService } from "@core/services/language.service";
import { Subscription } from "rxjs";

function isI18nFieldValue(field: any[]): field is I18nFieldValue[] {
  if (Array.isArray(field) && field.length) {
    return field.every(f => isI18nValue(f));
  }
  return false;
}

function isI18nValue(field: I18nFieldValue | I18nFieldValueResponse): field is I18nFieldValue {
  if (typeof field.lang === "string") {
    return false;
  }
  return true;
}

@Injectable()
@Pipe({
  name: 'i18nFieldsValues',
  pure: false // required to update the value when the promise is resolved
})
export class I18nFieldsValuesPipe implements PipeTransform, OnDestroy {
  value: string = '';
  lastLang: Language;
  onLangChange: Subscription;

  constructor(private languageService: LanguageService, private _ref: ChangeDetectorRef) {
  }

  private getFieldsValues(fields: I18nFieldValue[][] | I18nFieldValueResponse[][], lang: Language): string {
    let values = [];

    fields?.forEach((field: I18nFieldValue[] | I18nFieldValueResponse[]) => {
      if (field.length) {
        let request = isI18nFieldValue(field) ? field.filter(f => f.lang === lang) : field.filter(f => f.lang === lang.code);
        if (request.length) {
          values.push(this.getDisplayValue(request[0], lang));
        } else {
          // Get other value
          request = isI18nFieldValue(field) ? field.filter(f => f.value?.length) : field.filter(f => f.value?.length);
          if (request.length) {
            values.push(this.getDisplayValue(request[0], lang));
          }
        }
      }
    });

    return values.join(', ');
  }

  private getDisplayValue(field: I18nFieldValue | I18nFieldValueResponse, lang: Language): string {
    const fieldLanguage = typeof field.lang === 'string' ? Language.fromCode(field.lang) : field.lang;
    return (fieldLanguage === lang ? '' : `[${fieldLanguage?.codeExtended}] `) + field.value;
  }

  private updateValue(fields: I18nFieldValue[][] | I18nFieldValueResponse[][], lang: Language): void {
    this.value = this.getFieldsValues(fields, lang);
    this.lastLang = lang;
    this._ref.markForCheck();
  }

  transform(query: I18nFieldValue[][] | I18nFieldValueResponse[][] | string, ...args: any[]): any {
    if (typeof query === 'string') {
      return query;
    }

    if (!Array.isArray(query) || !query.length) {
      return '';
    }

    const requestLang = this.languageService.getLanguage();

    // if we ask another time for the same key, return the last value
    if (this.lastLang === requestLang && this.value === this.getFieldsValues(query, requestLang)) {
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