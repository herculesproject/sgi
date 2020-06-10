import { NgModule, Optional, SkipSelf, ErrorHandler } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

import { NGXLogger } from 'ngx-logger';

import { throwIfAlreadyLoaded } from './guards/module-import.guard';
import { AuthGuard } from './guards/auth.guard';
import { GlobalErrorHandler } from './services/global-error.handler';
import { SERVICES_IMPORTS } from './services/exports/services-export';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [],
  exports: [
    TranslateModule
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    TranslateModule
  ],
  providers: [
    SERVICES_IMPORTS,
    {
      provide: NGXLogger,
      useClass: NGXLogger,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
    AuthGuard,
  ],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }
}
