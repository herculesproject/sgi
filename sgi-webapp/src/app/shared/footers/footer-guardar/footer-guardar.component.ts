import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-footer-guardar',
  templateUrl: './footer-guardar.component.html',
  styleUrls: ['./footer-guardar.component.scss']
})
export class FooterGuardarComponent implements OnInit {
  @Input() texto: string;
  @Output() emitter: EventEmitter<boolean>;

  constructor(
    private readonly logger: NGXLogger,
  ) {
    this.emitter = new EventEmitter<boolean>();
  }

  ngOnInit(): void {
    this.logger.debug(FooterGuardarComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(FooterGuardarComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Emite un evento true
   */
  emitir() {
    this.logger.debug(FooterGuardarComponent.name, 'emitir()', 'start');
    this.emitter.emit(true);
    this.logger.debug(FooterGuardarComponent.name, 'emitir()', 'end');
  }
}
