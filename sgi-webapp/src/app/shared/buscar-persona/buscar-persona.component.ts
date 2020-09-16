import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { BuscarPersonaDialogoComponent } from './dialogo/buscar-persona-dialogo.component';
import { IPersonaDialogo } from '@core/models/eti/persona-dialogo';
import { Persona } from '@core/models/sgp/persona';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const TEXT_USER_TITLE = marker('eti.buscarUsuario.titulo');
const TEXT_USER_BUTTON = marker('eti.buscarUsuario.boton.buscar');

@Component({
  selector: 'sgi-buscar-persona',
  templateUrl: './buscar-persona.component.html',
  styleUrls: ['./buscar-persona.component.scss']
})
export class BuscarPersonaComponent implements OnChanges {

  datosUsuario: string;
  usuarioDialogo: IPersonaDialogo;
  persona: Persona;

  @Input() textoLabel = TEXT_USER_TITLE;
  @Input() textoInput = TEXT_USER_TITLE;
  @Input() textoButton = TEXT_USER_BUTTON;
  @Input() datosUsuarioTexto: string;

  @Output()
  usuarioSeleccionado: EventEmitter<Persona> = new EventEmitter();

  constructor(public dialog: MatDialog, private readonly logger: NGXLogger) {
    this.usuarioDialogo = {
      nombre: '',
      apellidos: '',
      numIdentificadorPersonal: ''
    };
    this.persona = {
      nombre: '',
      primerApellido: '',
      segundoApellido: '',
      identificadorLetra: '',
      identificadorNumero: '',
      personaRef: '',
      nivelAcademico: '',
      vinculacion: ''
    };
    this.datosUsuario = this.datosUsuarioTexto;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.datosUsuarioTexto) {
      this.datosUsuario = this.datosUsuarioTexto;
    }
  }

  formularioBuscarUsuario(): void {
    const dialogRef = this.dialog.open(BuscarPersonaDialogoComponent, {
      width: '600px',
      data: this.persona
    });

    dialogRef.afterClosed().subscribe(persona => {
      console.log('formularioBuscarUsuario - closed');
      if (persona) {
        this.datosUsuario = persona.nombre + ' ' + persona.primerApellido + ' ' +
          persona.segundoApellido + '(' + persona.identificadorNumero + persona.identificadorLetra + ')';
        this.selectUsuario(persona);
      }
    });
  }

  selectUsuario(persona: Persona) {
    this.logger.debug(BuscarPersonaComponent.name, 'selectUsuario(persona: Persona)', 'start');
    this.usuarioSeleccionado.emit(persona);
    this.logger.debug(BuscarPersonaComponent.name, 'selectUsuario(persona: Persona)', 'end');
  }

}
