import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreModule } from '@core/core.module';
import { environment } from '@env';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TraductorPaginatorService } from '@core/services/traductor-paginator.service';
import { SharedModule } from '@shared/shared.module';
import { LoggerModule } from 'ngx-logger';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ComponentsModule } from './components/components.module';
import { SelectorModuloComponent } from '@shared/componentes-layout/selector-modulo/selector-modulo.component';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { SgiAuthModule, SGI_AUTH_CONFIG, SgiAuthMode } from '@sgi/framework/auth';

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
    SharedModule,
    MaterialDesignModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (http: HttpClient) => {
          return new TranslateHttpLoader(http);
        },
        deps: [HttpClient]
      }
    }),
    ReactiveFormsModule,
    ComponentsModule,
    HttpClientModule,
    PerfectScrollbarModule,
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
    }
  ],
  entryComponents: [
    SelectorModuloComponent
  ],
  exports: [
    AppComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
