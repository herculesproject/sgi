import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export class Language {

  private constructor(public readonly code: string, public readonly translateKey: string, public readonly icon) {

  }

  public static readonly ES = new Language('es', marker('language.es'), 'lang-es');
  public static readonly EN = new Language('en', marker('language.en'), 'lang-en');
  public static readonly EU = new Language('eu', marker('language.eu'), 'lang-eu');

  public static values(): Language[] {
    return [this.ES, this.EN, this.EU];
  }

  public static fromCode(code: string): Language {
    for (const value of this.values()) {
      if (code?.toLocaleLowerCase() === value.code) {
        return value;
      }
    }

    return null;
  }
}