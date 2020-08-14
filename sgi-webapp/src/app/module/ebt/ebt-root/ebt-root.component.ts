import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-ebt-root',
  templateUrl: './ebt-root.component.html',
  styleUrls: ['./ebt-root.component.scss']
})
export class EbtRootComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(EbtRootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(EbtRootComponent.name, 'ngOnInit()', 'end');
  }

}
