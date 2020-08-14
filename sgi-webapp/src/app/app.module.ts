import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule, LOCALE_ID } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { environment } from '@env';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TraductorPaginatorService } from '@core/services/traductor-paginator.service';
import { LoggerModule } from 'ngx-logger';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SgiAuthModule, SGI_AUTH_CONFIG, SgiAuthMode } from '@sgi/framework/auth';

import { BlockModule } from './block/block.module';
import { SelectorModuloComponent } from './block/selector-modulo/selector-modulo.component';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { CoreModule } from '@angular/flex-layout';

// Load supported locales
registerLocaleData(localeEs);

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    LoggerModule.forRoot(environment.loggerConfig),
    MaterialDesignModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (http: HttpClient) => {
          return new TranslateHttpLoader(http);
        },
        deps: [HttpClient]
      },
      defaultLanguage: 'es',
    }),
    BlockModule,
    HttpClientModule,
    SgiAuthModule.forRoot()
  ],
  providers: [
    {
      provide: MatPaginatorIntl,
      useClass: TraductorPaginatorService
    },
    {
      provide: SGI_AUTH_CONFIG,
      useValue: environment.authConfig
    },
    {
      provide: SgiAuthMode,
      useValue: environment.authConfig.mode
    },
    {
      provide: LOCALE_ID,
      useValue: 'es'
    },
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: { useUtc: true }
    },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    }
  ],
  entryComponents: [
    SelectorModuloComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
