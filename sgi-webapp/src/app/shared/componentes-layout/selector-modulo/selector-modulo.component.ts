import { Component, OnInit } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';

export interface Modulo {
  url: string;
  nombre: string;
}

@Component({
  selector: 'app-selector-modulo',
  templateUrl: './selector-modulo.component.html',
  styleUrls: ['./selector-modulo.component.scss']
})
export class SelectorModuloComponent implements OnInit {
  modulos: Modulo[];

  constructor(
    private logger: NGXLogger,
    public dialogRef: MatDialogRef<SelectorModuloComponent>,
    private router: Router,
    private readonly traductor: TraductorService
  ) {
    this.modulos = [{
      url: UrlUtils.cat.root,
      nombre: this.traductor.getTexto('selector-modulo.catalogo')
    },
    {
      url: UrlUtils.eti.root,
      nombre: this.traductor.getTexto('selector-modulo.etica')
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
