import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { IActionService } from '@core/services/action-service';
import { Subject } from 'rxjs';

@Component({
  selector: 'sgi-action-footer',
  templateUrl: './action-footer.component.html',
  styleUrls: ['./action-footer.component.scss']
})
export class ActionFooterComponent {
  @Input() texto: string;
  @Input() actionService: IActionService;
  readonly event$ = new Subject<boolean>();


  constructor(
    private readonly logger: NGXLogger
  ) { }

  save() {
    this.logger.debug(ActionFooterComponent.name, 'emitir()', 'start');
    this.event$.next(true);
    this.logger.debug(ActionFooterComponent.name, 'emitir()', 'end');
  }

  cancel() {
    this.event$.next(false);
  }
}
