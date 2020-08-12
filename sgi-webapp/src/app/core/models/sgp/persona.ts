export class Persona {

  /** ID */
  personaRef: string;

  /** Nombre */
  nombre: string;

  /** Primer apellido */
  primerApellido: string;

  /** Segundo apellido */
  segundoApellido: string;

  /** Identificador número */
  identificadorNumero: string;

  /** Identificador letra */
  identificadorLetra: string;

  constructor(personaFisica?: Persona) {
    this.personaRef = personaFisica.personaRef;
    this.nombre = personaFisica.nombre;
    this.primerApellido = personaFisica.primerApellido;
    this.segundoApellido = personaFisica.segundoApellido;
    this.identificadorNumero = personaFisica.identificadorNumero;
    this.identificadorLetra = personaFisica.identificadorLetra;
  }
}
