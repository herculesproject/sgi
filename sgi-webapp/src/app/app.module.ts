import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { SharedModule } from '@shared/shared.module';

import { MaterialDesignModule } from '@material/material-design.module';
import { CoreModule } from '@core/core.module';
import { ReactiveFormsModule } from '@angular/forms';
import { LoggerModule } from 'ngx-logger';

import { environment } from '@env';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ComponentsModule } from './components/components.module';


@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    LoggerModule.forRoot({
      serverLoggingUrl: environment.serverLoggingUrl,
      level: environment.logLevel,
      serverLogLevel: environment.serverLogLevel
    }),
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
    MaterialDesignModule.forRoot(),
    ComponentsModule
  ],
  providers: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
