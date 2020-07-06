import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {SelectorModuloComponent} from '../selector-modulo/selector-modulo.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-menu-secundario',
  templateUrl: './menu-secundario.component.html',
  styleUrls: ['./menu-secundario.component.scss']
})
export class MenuSecundarioComponent implements OnInit {
  constructor(
    private logger: NGXLogger,
    public matDialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.logger.debug(MenuSecundarioComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(MenuSecundarioComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Abre ventana modal para cambiar de módulo de la aplicación
   */
  selectorModulo() {
    this.logger.debug(MenuSecundarioComponent.name, 'selectorModulo()', 'start');
    const config = {
      maxWidth: '500px',
      maxHeight: '500px',
    };
    this.matDialog.open(SelectorModuloComponent, config);
    this.logger.debug(MenuSecundarioComponent.name, 'selectorModulo()', 'end');
  }
}
