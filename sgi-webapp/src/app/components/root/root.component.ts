import { Component, OnDestroy, OnInit } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

import { AppComponent } from '../../app.component';

@Component({
  selector: 'app-root',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.scss']
})
export class RootComponent implements OnInit, OnDestroy {
  // width pantalla resoluciones pequenas
  screenWidth: number;
  menuToogle: boolean;
  // Subscription
  subscriptionMenuToogle: Subscription;

  constructor(private logger: NGXLogger, private layoutService: LayoutService) {
    this.screenWidth = window.innerWidth;
  }

  ngOnInit(): void {
    this.logger.info('SGI WEBAPP - HERCULES');
    this.logger.debug(AppComponent.name, 'ngOnInit()', 'start');

    this.subscriptionMenuToogle = this.layoutService
      .getToogleSidenav()
      .subscribe((menuToogle) => {
        this.menuToogle = menuToogle;
      });
    this.logger.debug(AppComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'start');
    if (this.subscriptionMenuToogle) {
      this.subscriptionMenuToogle.unsubscribe();
    }
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'end');
  }

}
