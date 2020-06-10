import { Component, OnInit, ChangeDetectionStrategy, AfterContentChecked } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, AfterContentChecked {
  formGroup: FormGroup;
  // width pantalla resoluciones pequenas
  screenWidth: number;
  // toogle
  toggleActive: boolean;
  select: string;

  constructor(
    private logger: NGXLogger,
    private sidenav: LayoutService,
    public traductorServicio: TraductorService
  ) {
    this.screenWidth = window.innerWidth;
  }

  ngOnInit(): void {
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'start');
    this.toggleActive = false;
    this.select = 'modulo2';
    this.formGroup = new FormGroup({
      module: new FormControl(this.select, []),
    });
    this.logger.debug(HeaderComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterContentChecked(): void {
    this.formGroup.get('module').setValue(this.select);
  }

  /**
   * Cambiar el estado del menú acordeón
   */
  toggleSidenav(): void {
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'start');
    this.toggleActive = !this.toggleActive;
    this.sidenav.setToogleSidenav(this.toggleActive);
    this.logger.debug(HeaderComponent.name, 'toggleSidenav()', 'end');
  }

  /**
   * Redirecciona al valor seleccionado en el select
   */
  navigateTo(value: string): void {
    this.logger.debug(HeaderComponent.name, 'navigateTo(value)', 'start');
    this.select = value;
    this.logger.debug(HeaderComponent.name, 'navigateTo(value)', 'end');
  }

}
