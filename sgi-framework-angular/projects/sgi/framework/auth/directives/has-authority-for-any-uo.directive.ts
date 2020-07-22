import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { SgiAuthService } from '../auth.service';
import { Subscription } from 'rxjs';
import { hasAuthorityForAnyUO } from '../auth.authority';

/**
 * Structural directive to hide elements if the user NOT have the established authority.
 *
 * The authority must not contain an UO (Unidad Organizativa) prefix, because the validation ignore it.
 *
 * To check authority against UO use {@link HasAuthority}
 */
@Directive({
  selector: '[sgiHasAuthorityForAnyUO]'
})
export class HasAuthorityForAnyUODirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasAuthorityForAnyUO')
  private authority: string;
  private userAuthorities: string[];
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    if (!this.authority || typeof this.authority !== 'string' || this.authority.trim() === '') {
      throw Error('Must provide an authority');
    }
    if (this.authority.indexOf('_') >= 0) {
      throw Error('Authority cannot contain an underscore');
    }
    this.subscription = this.authService.authStatus$.subscribe((status) => {
      this.userAuthorities = status.authorities;
      this.updateView();
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private updateView(): void {
    if (this.checkAuthority() && !this.rendered) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.rendered = true;
    }
    else if (!this.checkAuthority() && this.rendered) {
      this.viewContainer.clear();
      this.rendered = false;
    }
  }

  private checkAuthority(): boolean {
    return hasAuthorityForAnyUO(this.userAuthorities, this.authority);
  }
}
