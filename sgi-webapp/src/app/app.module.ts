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

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CoreModule,
    LoggerModule.forRoot({
      serverLoggingUrl: environment.serverLoggingUrl,
      level: environment.logLevel,
      serverLogLevel: environment.serverLogLevel
    }),
    SharedModule,
    ReactiveFormsModule,
    MaterialDesignModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
