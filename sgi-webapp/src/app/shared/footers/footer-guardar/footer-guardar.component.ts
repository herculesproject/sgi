import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { IActionService } from '@core/services/action-service';

@Component({
  selector: 'sgi-footer-guardar',
  templateUrl: './footer-guardar.component.html',
  styleUrls: ['./footer-guardar.component.scss']
})
export class FooterGuardarComponent {
  @Input() texto: string;
  @Input() actionService: IActionService;
  @Output() emitter: EventEmitter<boolean>;


  constructor(
    private readonly logger: NGXLogger
  ) {
    this.emitter = new EventEmitter<boolean>();
  }

  save() {
    this.logger.debug(FooterGuardarComponent.name, 'emitir()', 'start');
    this.emitter.emit(true);
    this.logger.debug(FooterGuardarComponent.name, 'emitir()', 'end');
  }
}
