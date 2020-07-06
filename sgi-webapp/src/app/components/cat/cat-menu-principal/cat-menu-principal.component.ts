import {Component, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {UrlUtils} from '@core/utils/url-utils';

@Component({
  selector: 'app-cat-menu-principal',
  templateUrl: './cat-menu-principal.component.html',
  styleUrls: ['./cat-menu-principal.component.scss'],
})
export class CatMenuPrincipalComponent implements OnInit {
  UrlUtils = UrlUtils;

  constructor(private logger: NGXLogger) {
  }

  ngOnInit(): void {
    this.logger.debug(CatMenuPrincipalComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(CatMenuPrincipalComponent.name, 'ngOnInit()', 'end');
  }
}
