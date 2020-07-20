import {Component, Input, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {UrlUtils} from '@core/utils/url-utils';

@Component({
  selector: 'app-footer-crear',
  templateUrl: './footer-crear.component.html',
  styleUrls: ['./footer-crear.component.scss']
})
export class FooterCrearComponent implements OnInit {
  UrlUtils = UrlUtils;
  @Input() texto: string;

  constructor(
    private readonly logger: NGXLogger,
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(FooterCrearComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(FooterCrearComponent.name, 'ngOnInit()', 'end');
  }

}
