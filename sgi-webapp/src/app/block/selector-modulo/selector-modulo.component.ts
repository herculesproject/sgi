import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { APP_ROUTE_NAMES } from 'src/app/app-route-names';

const MSG_MODULO_ETI = marker('selector-modulo.etica');

export interface Modulo {
  url: string;
  nombre: string;
}

@Component({
  templateUrl: './selector-modulo.component.html',
  styleUrls: ['./selector-modulo.component.scss']
})
export class SelectorModuloComponent implements OnInit {
  modulos: Modulo[];

  constructor(
    private logger: NGXLogger,
    public dialogRef: MatDialogRef<SelectorModuloComponent>,
    private router: Router
  ) {
    this.modulos = [
      {
        url: APP_ROUTE_NAMES.ETI,
        nombre: MSG_MODULO_ETI
      }
    ];
  }

  ngOnInit(): void {
    this.logger.debug(SelectorModuloComponent.name, 'ngOnInit()', 'start');
    // this.dialogRef.updateSize('420px', '360px');
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
