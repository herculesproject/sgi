import { Component, OnChanges, Input, Output, SimpleChanges, EventEmitter } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { BuscarConvocatoriaDialogoComponent } from './dialogo/buscar-convocatoria-dialogo.component';

const TEXT_TITLE = marker('csp.buscadorConvocatoria.titulo');
const TEXT_BUTTON = marker('botones.buscar');

@Component({
  selector: 'sgi-buscar-convocatoria',
  templateUrl: './buscar-convocatoria.component.html',
  styleUrls: ['./buscar-convocatoria.component.scss']
})
export class BuscarConvocatoriaComponent implements OnChanges {

  datosConvocatoria = '';
  convocatoria = {} as IConvocatoria;

  @Input() required = false;
  @Input() disabled = false;
  @Input() textoLabel = TEXT_TITLE;
  @Input() textoInput = TEXT_TITLE;
  @Input() textoButton = TEXT_BUTTON;
  @Input() datosConvocatoriaTexto: string;

  @Output() convocatoriaSeleccionada = new EventEmitter<IConvocatoria>();

  constructor(public dialog: MatDialog, private readonly logger: NGXLogger) { }

  ngOnChanges(changes: SimpleChanges) {
    this.logger.debug(BuscarConvocatoriaComponent.name, 'ngOnChanges(changes: SimpleChanges)', 'start');
    if (changes.datosConvocatoriaTexto) {
      this.datosConvocatoria = this.datosConvocatoriaTexto;
    }
    this.logger.debug(BuscarConvocatoriaComponent.name, 'ngOnChanges(changes: SimpleChanges)', 'end');
  }

  formularioBuscarConvocatoria(): void {
    this.logger.debug(BuscarConvocatoriaComponent.name, `formularioBuscarConvocatoria()`, 'start');
    const dialogRef = this.dialog.open(BuscarConvocatoriaDialogoComponent, {
      width: '1000px',
      data: this.convocatoria
    });
    dialogRef.afterClosed().subscribe((convocatoria: IConvocatoria) => {
      if (convocatoria) {
        this.datosConvocatoria = convocatoria.titulo;
        this.selectConvocatoria(convocatoria);
      }
    });
    this.logger.debug(BuscarConvocatoriaComponent.name, `formularioBuscarConvocatoria()`, 'end');
  }

  selectConvocatoria(convocatoria: IConvocatoria) {
    this.logger.debug(BuscarConvocatoriaComponent.name, `selectConvocatoria(${convocatoria})`, 'start');
    this.convocatoriaSeleccionada.emit(convocatoria);
    this.logger.debug(BuscarConvocatoriaComponent.name, `selectConvocatoria(${convocatoria})`, 'end');
  }

}
