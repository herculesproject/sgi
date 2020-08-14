import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-pii-root',
  templateUrl: './pii-root.component.html',
  styleUrls: ['./pii-root.component.scss']
})
export class PiiRootComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(PiiRootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(PiiRootComponent.name, 'ngOnInit()', 'end');
  }

}
