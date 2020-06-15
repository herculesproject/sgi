import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { UrlUtils } from '@core/utils/url-utils';

@Component({
  selector: 'app-menu-principal',
  templateUrl: './menu-principal.component.html',
  styleUrls: ['./menu-principal.component.scss'],
})
export class MenuPrincipalComponent implements OnInit {
  UrlUtils = UrlUtils;

  constructor(private logger: NGXLogger) {}

  ngOnInit(): void {
    this.logger.debug(MenuPrincipalComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(MenuPrincipalComponent.name, 'ngOnInit()', 'end');
  }
}
