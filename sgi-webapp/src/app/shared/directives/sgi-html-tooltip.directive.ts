import { AriaDescriber, FocusMonitor } from '@angular/cdk/a11y';
import { Directionality } from '@angular/cdk/bidi';
import { Overlay, ScrollDispatcher } from '@angular/cdk/overlay';
import { Platform } from '@angular/cdk/platform';
import {
  Directive, ElementRef, Inject, Input, NgZone, Optional, Renderer2, SecurityContext, ViewContainerRef
} from '@angular/core';
import {
  MatTooltip,
  MatTooltipDefaultOptions, MAT_TOOLTIP_DEFAULT_OPTIONS,
  MAT_TOOLTIP_SCROLL_STRATEGY
} from '@angular/material/tooltip';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * Tooltip de Angular Material que muestra contenido HTML sanitizado. Se abre y se cierra igual que matTooltip.
 */
@Directive({
  selector: '[sgiHtmlTooltip]',
})
export class SgiHtmlTooltipDirective extends MatTooltip {

  private html = '';

  /** Contenido HTML del tooltip */
  @Input()
  get sgiHtmlTooltip(): string {
    return this.html;
  }
  set sgiHtmlTooltip(value: string) {
    this.html = this.sanitizer.sanitize(SecurityContext.HTML, value ?? '') ?? '';
    this.message = this.toPlainText(this.html);
    this.renderHtml();
  }

  constructor(
    overlay: Overlay,
    elementRef: ElementRef,
    scrollDispatcher: ScrollDispatcher,
    viewContainerRef: ViewContainerRef,
    ngZone: NgZone,
    platform: Platform,
    ariaDescriber: AriaDescriber,
    focusMonitor: FocusMonitor,
    @Inject(MAT_TOOLTIP_SCROLL_STRATEGY) scrollStrategy: any,
    @Optional() dir: Directionality,
    @Optional() @Inject(MAT_TOOLTIP_DEFAULT_OPTIONS) defaultOptions: MatTooltipDefaultOptions,
    private readonly sanitizer: DomSanitizer,
    private readonly renderer: Renderer2
  ) {
    super(
      overlay,
      elementRef,
      scrollDispatcher,
      viewContainerRef,
      ngZone,
      platform,
      ariaDescriber,
      focusMonitor,
      scrollStrategy,
      dir,
      defaultOptions,
      null
    );
  }

  /**
   * Muestra el tooltip sustituyendo su texto plano por el contenido HTML sanitizado.
   *
   * @param delay retardo en milisegundos antes de mostrar el tooltip.
   */
  show(delay?: number): void {
    super.show(delay);
    this.renderHtml();
  }

  /**
   * Vuelca el contenido HTML sanitizado en el tooltip, si está creado.
   */
  private renderHtml(): void {
    const tooltipElement: HTMLElement = this._tooltipInstance
      ? this._overlayRef?.overlayElement?.querySelector('.mat-tooltip')
      : null;
    if (tooltipElement) {
      this.renderer.setProperty(tooltipElement, 'innerHTML', this.html);
    }
  }

  /**
   * Obtiene el texto plano del HTML indicado, usado como mensaje accesible del tooltip.
   *
   * @param html contenido HTML sanitizado.
   * @returns el texto sin etiquetas.
   */
  private toPlainText(html: string): string {
    if (!html) {
      return '';
    }
    const body = new DOMParser().parseFromString(html, 'text/html').body;
    body.querySelectorAll('p, li, br').forEach(element => element.after(' '));
    return body.textContent?.replace(/\s+/g, ' ').trim() ?? '';
  }

}
