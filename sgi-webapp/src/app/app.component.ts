import { Component, OnDestroy, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'sgi-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {

  constructor(private logger: NGXLogger) {
  }

  ngOnInit(): void {
    this.logger.info('SGI WEBAPP - HERCULES');
    this.logger.debug(AppComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(AppComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'start');
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'end');
  }
}
