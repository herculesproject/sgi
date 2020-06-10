import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-menu-principal',
  templateUrl: './menu-principal.component.html',
  styleUrls: ['./menu-principal.component.scss']
})
export class MenuPrincipalComponent implements OnInit {

  constructor(
    private logger: NGXLogger
  ) { }

  ngOnInit(): void {
    this.logger.debug(MenuPrincipalComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(MenuPrincipalComponent.name, 'ngOnInit()', 'end');
  }

}
