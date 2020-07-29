import { NgModule, Optional, SkipSelf, ErrorHandler } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

import { NGXLogger } from 'ngx-logger';

import { GlobalErrorHandler } from './services/global-error.handler';
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
    {
      provide: NGXLogger,
      useClass: NGXLogger,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    }
  ],
})
export class CoreModule { }
