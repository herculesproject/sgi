import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-menu-secundario',
  templateUrl: './menu-secundario.component.html',
  styleUrls: ['./menu-secundario.component.scss']
})
export class MenuSecundarioComponent implements OnInit {

  constructor(
    private logger: NGXLogger
    ) { }

  ngOnInit(): void {
    this.logger.debug(MenuSecundarioComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(MenuSecundarioComponent.name, 'ngOnInit()', 'end');
  }

}
