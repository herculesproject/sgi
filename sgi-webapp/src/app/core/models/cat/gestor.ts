export class Gestor {

  /** Persona ref. */
  personaRef: string;

  /** Nombre. */
  nombre: string;

  /** Apellidos. */
  apellidos: string;

  /** Dni. */
  dni: string;

  /** Letra dni. */
  dniLetra: string;

  constructor(personaRef: string, nombre: string, apellidos: string, dni: string, dniLetra: string) {
    this.personaRef = personaRef;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.dni = dni;
    this.dniLetra = dniLetra;
  }

}
