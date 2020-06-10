import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-root-component',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.scss']
})
export class RootComponent implements OnInit {

  constructor(
    private logger: NGXLogger,
  ) { }

  ngOnInit(): void {
    this.logger.debug(RootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(RootComponent.name, 'ngOnInit()', 'end');
  }

}
