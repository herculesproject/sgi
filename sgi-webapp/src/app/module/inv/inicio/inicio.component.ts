import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'sgi-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.scss']
})
export class InicioComponent implements OnInit {

  constructor(private logger: NGXLogger) {
  }

  ngOnInit(): void {
    this.logger.debug(InicioComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(InicioComponent.name, 'ngOnInit()', 'end');
  }

}
