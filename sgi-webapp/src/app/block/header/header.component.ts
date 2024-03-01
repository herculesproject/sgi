import { AfterViewChecked, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatSelect } from '@angular/material/select';
import { Module } from '@core/module';
import { ConfigPublicService } from '@core/services/cnf/config-public.service';
import { ResourcePublicService } from '@core/services/cnf/resource-public.service';
import { LANGUAGE_MAP, Language, LanguageService } from '@core/services/language.service';
import { LayoutService } from '@core/services/layout.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

interface LanguageSelect {
  value: string;
  viewValue: string;
  image: string;
}

@Component({
  selector: 'sgi-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnDestroy, AfterViewChecked {
  Module = Module;
  anchoPantalla: number;

  module: Module;
  numLogosCabecera: number;
  private subscriptions: Subscription[] = [];
  languages: LanguageSelect[] = [];
  showSelectLanguage = true;
  private readonly ALL_LANGUAGES = [
    { value: Language.ES.code, viewValue: LANGUAGE_MAP.get(Language.ES), image: `${this.getUrlResource(`web-logo-sgi-${Language.ES.code}`)}` },
    { value: Language.EN.code, viewValue: LANGUAGE_MAP.get(Language.EN), image: `${this.getUrlResource(`web-logo-sgi-${Language.EN.code}`)}` },
    { value: Language.EU.code, viewValue: LANGUAGE_MAP.get(Language.EU), image: `${this.getUrlResource(`web-logo-sgi-${Language.EU.code}`)}` },
  ] as LanguageSelect[];

  get selectedLanguage(): LanguageSelect {
    let languageSelected = null;
    if (this.languages.length > 0) {
      languageSelected = this.languages.find(lang => lang.value === this.languageService.getLanguage().code);
      if (!languageSelected) {
        languageSelected = this.languages[0].value;
        this.selectLanguage(this.languages[0].value)
      }
    }
    return languageSelected;
  }

  @ViewChild('select') select?: MatSelect;

  constructor(
    private readonly layout: LayoutService,
    private readonly resourceService: ResourcePublicService,
    private readonly configService: ConfigPublicService,
    private readonly languageService: LanguageService,
    private readonly translateService: TranslateService
  ) {
    this.anchoPantalla = window.innerWidth;
    this.subscriptions.push(this.layout.activeModule$.subscribe((res) => this.module = res));
    this.subscriptions.push(this.configService.getNumeroLogosCabecera().subscribe((num) => this.numLogosCabecera = Number(num)));
    this.subscriptions.push(this.configService.getLenguajesCabecera().subscribe((langs) => {
      if (langs && langs.length > 0) {
        this.showSelectLanguage = true;
        langs.forEach(lang => {
          this.languages.push(this.ALL_LANGUAGES.find(l => l.value === lang));
        })
      } else {
        this.defaultLanguage();
      }
      this.translateService.use(languageService.getLanguage().code);
      this.translateService.setDefaultLang(languageService.getLanguage().code);
    },
      (error) => {
        this.defaultLanguage();
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  ngAfterViewChecked(): void {
    if (this.showSelectLanguage) {
      this.select.value = this.languageService.getLanguage().code;
    }
  }

  getUrlResource(id: string): string {
    return this.resourceService.getUrlResource(id);
  }

  getUrlSetResource(id: string, versiones: string[]): string {
    return versiones.map(version => `${this.getUrlResource(version ? id + version : id)} ${version}`).join(', ');
  }

  selectLanguage(code?: string) {
    this.languageService.switchLanguage(this.languageService.getLanguage(code));
    this.translateService.use(code);
    this.translateService.setDefaultLang(code);
  }

  private defaultLanguage() {
    this.showSelectLanguage = false;
    this.languages.push(this.ALL_LANGUAGES.find(l => l.value === this.languageService.getLanguage().code));
  }

}
