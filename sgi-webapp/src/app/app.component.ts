import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { LayoutService } from '@core/services/layout.service';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'sgi-webapp';

  // width pantalla resoluciones pequenas
  screenWidth: number;

  selected = 'modulo2';

  menuToogle: boolean;

  // Subscription
  subscriptionMenuToogle: Subscription;

  constructor(private logger: NGXLogger, private layoutService: LayoutService) {
    this.screenWidth = window.innerWidth;
  }

  ngOnInit(): void {
    this.logger.info('SGI WEBAPP - HERCULES');
    this.logger.debug(AppComponent.name, 'ngOnInit()', 'start');

    this.subscriptionMenuToogle = this.layoutService.getToogleSidenav().subscribe(menuToogle => {
      this.menuToogle = menuToogle;
    });

    this.logger.debug(AppComponent.name, 'ngOnInit()', 'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptionMenuToogle.unsubscribe();
    this.logger.debug(AppComponent.name, 'ngOnDestroy()', 'end');
  }


}
