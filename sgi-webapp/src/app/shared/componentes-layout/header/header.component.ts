import { Component, OnInit } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { NGXLogger } from 'ngx-logger';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  // width pantalla resoluciones pequenas
  screenWidth: number;

  selected = 'modulo2';

  // toogle
  toggleActive: boolean = false;

  constructor(private logger: NGXLogger, private router: Router, private sidenav: LayoutService) {
    this.screenWidth = window.innerWidth;
  }

  ngOnInit(): void {
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Cambiar el estado del menu acordeon
   */
  toggleSidenav(): void {
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'start');
    this.toggleActive = !this.toggleActive;
    this.sidenav.setToogleSidenav(this.toggleActive);
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'end');
  }

  /**
   * Redirecciona al valor seleccionado en el select
   * @param value identificador modulo
   */
  navigateTo(value) {
    this.logger.debug(HeaderComponent.name, 'navigateTo(value)', 'start');
    console.log(value);
    //this.router.navigate(['../', value]);
    this.logger.debug(HeaderComponent.name, 'navigateTo(value)', 'end');

  }

}
