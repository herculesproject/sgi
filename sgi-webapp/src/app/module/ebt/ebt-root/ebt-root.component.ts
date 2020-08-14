import { Component, OnInit } from '@angular/core';
import { TraductorService } from '@core/services/traductor.service';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-ebt-root',
  templateUrl: './ebt-root.component.html',
  styleUrls: ['./ebt-root.component.scss']
})
export class EbtRootComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(EbtRootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(EbtRootComponent.name, 'ngOnInit()', 'end');
  }

}
