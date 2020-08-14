import {Component, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {TraductorService} from '@core/services/traductor.service';

@Component({
  selector: 'app-cat-root',
  templateUrl: './cat-root.component.html',
  styleUrls: ['./cat-root.component.scss']
})
export class CatRootComponent implements OnInit {
  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(CatRootComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(CatRootComponent.name, 'ngOnInit()', 'end');
  }
}
