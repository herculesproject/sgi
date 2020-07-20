import {Component, Input, OnInit} from '@angular/core';
import {AbstractTabComponent} from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-generic-tab-label',
  templateUrl: './generic-tab-label.component.html',
  styleUrls: ['./generic-tab-label.component.scss']
})
export class GenericTabLabelComponent implements OnInit {
  @Input() textoLabel: string;
  @Input() abstractTab: AbstractTabComponent<any>;
  @Input() errors: string;

  constructor(private readonly logger: NGXLogger) {
  }

  ngOnInit(): void {
    this.logger.debug(GenericTabLabelComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(GenericTabLabelComponent.name, 'ngOnInit()', 'end');
  }

}
