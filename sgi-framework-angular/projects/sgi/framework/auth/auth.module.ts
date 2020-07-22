import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HasAuthorityForAnyUODirective } from './directives/has-authority-for-any-uo.directive';
import { IfAuthenticatedDirective } from './directives/if-authenticated.directive';
import { HasAnyAuthorityForAnyUODirective } from './directives/has-any-authority-for-any-uo.directive';
import { HasAuthorityDirective } from './directives/has-authority.directive';
import { HasAnyAuthorityDirective } from './directives/has-any-authority.directive';
import { SgiAuthService } from './auth.service';
import { authInitializer } from './auth.initializer';
import { authFactory } from './auth.factory';
import { SgiAuthMode } from './auth.enum';
import { NGXLogger } from 'ngx-logger';
import { Router } from '@angular/router';
import { PlatformLocation } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { SgiAuthHttpInterceptor } from './auth-http-interceptor';
import { SGI_AUTH_CONFIG } from './auth.config';


@NgModule({
  declarations: [
    HasAuthorityForAnyUODirective,
    IfAuthenticatedDirective,
    HasAnyAuthorityForAnyUODirective,
    HasAuthorityDirective,
    HasAnyAuthorityDirective,
  ],
  providers: [
    {
      provide: SgiAuthService,
      useFactory: authFactory,
      deps: [SgiAuthMode, NGXLogger, Router, PlatformLocation],
    },
    {
      provide: APP_INITIALIZER,
      useFactory: authInitializer,
      multi: true,
      deps: [SgiAuthService, SGI_AUTH_CONFIG],
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SgiAuthHttpInterceptor,
      multi: true
    }
  ],
  exports: [
    HasAuthorityForAnyUODirective,
    IfAuthenticatedDirective,
    HasAnyAuthorityForAnyUODirective,
    HasAuthorityDirective,
    HasAnyAuthorityDirective
  ]
})
export class SgiAuthModule { }
