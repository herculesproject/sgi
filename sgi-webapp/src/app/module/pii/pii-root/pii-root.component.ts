import { Component, OnInit } from '@angular/core';
import { TraductorService } from '@core/services/traductor.service';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-pii-root',
  templateUrl: './pii-root.component.html',
  styleUrls: ['./pii-root.component.scss']
})
export class PiiRootComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(PiiRootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(PiiRootComponent.name, 'ngOnInit()', 'end');
  }

}
