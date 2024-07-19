import { NgModule } from '@angular/core';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';


/**
 *
 */
@NgModule()
export class IconModule {
  private basePath = `/assets/icons`;

  /**
   *
   * @param domSanitizer
   * @param matIconRegistry
   */
  constructor(
    private readonly domSanitizer: DomSanitizer,
    private readonly matIconRegistry: MatIconRegistry
  ) {
    this.matIconRegistry
      .addSvgIcon('lang-en', this.getPath('lang-en.svg'))
      .addSvgIcon('lang-es', this.getPath('lang-es.svg'))
      .addSvgIcon('lang-eu', this.getPath('lang-eu.svg'));
  }

  /**
   * @param url
   */
  private getPath(url: string): SafeResourceUrl {
    return this.domSanitizer.bypassSecurityTrustResourceUrl(`${this.basePath}/${url}`);
  }
}
