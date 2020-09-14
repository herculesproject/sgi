import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { BuscarPersonaDialogoComponent } from './dialogo/buscar-persona-dialogo.component';
import { IPersonaDialogo } from '@core/models/eti/persona-dialogo';
import { Persona } from '@core/models/sgp/persona';

@Component({
  selector: 'sgi-buscar-persona',
  templateUrl: './buscar-persona.component.html',
  styleUrls: ['./buscar-persona.component.scss']
})
export class BuscarPersonaComponent implements OnChanges {

  datosUsuario: string;
  usuarioDialogo: IPersonaDialogo;
  persona: Persona;

  @Input() textoLabel: string;
  @Input() textoInput: string;
  @Input() textoButton: string;
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
      personaRef: ''
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
