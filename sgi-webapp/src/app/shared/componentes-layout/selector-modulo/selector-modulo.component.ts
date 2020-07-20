import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {UrlUtils} from '@core/utils/url-utils';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-selector-modulo',
  templateUrl: './selector-modulo.component.html',
  styleUrls: ['./selector-modulo.component.scss']
})
export class SelectorModuloComponent implements OnInit {
  modulos: string[];

  constructor(
    private logger: NGXLogger,
    public dialogRef: MatDialogRef<SelectorModuloComponent>,
    private router: Router
  ) {
    this.modulos = [UrlUtils.cat, UrlUtils.ebt, UrlUtils.pii, UrlUtils.eti];
  }

  ngOnInit(): void {
    this.logger.debug(SelectorModuloComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(SelectorModuloComponent.name, 'ngOnInit()', 'end');
  }

  cerrarModal(): void {
    this.dialogRef.close();
  }

  abrirModulo(modulo: string) {
    this.router.navigateByUrl(modulo).then(
      () => {
        this.cerrarModal();
      }
    );
  }
}
