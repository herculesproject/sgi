import {Component, OnInit} from '@angular/core';
import {UrlUtils} from '@core/utils/url-utils';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-eti-menu-principal',
  templateUrl: './eti-menu-principal.component.html',
  styleUrls: ['./eti-menu-principal.component.scss']
})
export class EtiMenuPrincipalComponent implements OnInit {
  UrlUtils = UrlUtils;

  constructor(private logger: NGXLogger) {
  }

  ngOnInit(): void {
    this.logger.debug(EtiMenuPrincipalComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(EtiMenuPrincipalComponent.name, 'ngOnInit()', 'end');
  }
}
