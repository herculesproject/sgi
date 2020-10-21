import { Component, Input, Output, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { BuscarEmpresaEconomicaDialogoComponent } from './dialogo/buscar-empresa-economica-dialogo.component';

const TEXT_USER_TITLE = marker('sgp.buscadorEmpresaEconomica.titulo');
const TEXT_USER_BUTTON = marker('botones.buscar');

@Component({
  selector: 'sgi-buscar-empresa-economica',
  templateUrl: './buscar-empresa-economica.component.html',
  styleUrls: ['./buscar-empresa-economica.component.scss']
})
export class BuscarEmpresaEconomicaComponent implements OnChanges {

  datosEmpresaEconomica: string;
  empresaEconomica: IEmpresaEconomica = {} as IEmpresaEconomica;

  @Input() textoLabel = TEXT_USER_TITLE;
  @Input() textoInput = TEXT_USER_TITLE;
  @Input() textoButton = TEXT_USER_BUTTON;
  @Input() datosEmpresaEconomicaTexto: string;

  @Output()
  empresaEconomicaSeleccionada: EventEmitter<IEmpresaEconomica> = new EventEmitter();

  constructor(public dialog: MatDialog, private readonly logger: NGXLogger) { }


  ngOnChanges(changes: SimpleChanges) {
    if (changes.datosEmpresaEconomicaTexto) {
      this.datosEmpresaEconomica = this.datosEmpresaEconomicaTexto;
    }
  }

  formularioBuscarEmpresaEconomica(): void {
    const dialogRef = this.dialog.open(BuscarEmpresaEconomicaDialogoComponent, {
      width: '1000px',
      data: this.empresaEconomica
    });

    dialogRef.afterClosed().subscribe((empresaEconomica: IEmpresaEconomica) => {
      if (empresaEconomica) {
        this.datosEmpresaEconomica = empresaEconomica.razonSocial;
        this.selectEmpresaEconomica(empresaEconomica);
      }
    });
  }

  selectEmpresaEconomica(empresaEconomica: IEmpresaEconomica) {
    this.logger.debug(BuscarEmpresaEconomicaComponent.name, 'selectEmpresaEconomica(empresaEconomica: IEmpresaEconomica)', 'start');
    this.empresaEconomicaSeleccionada.emit(empresaEconomica);
    this.logger.debug(BuscarEmpresaEconomicaComponent.name, 'selectEmpresaEconomica(empresaEconomica: IEmpresaEconomica)', 'end');
  }

}
