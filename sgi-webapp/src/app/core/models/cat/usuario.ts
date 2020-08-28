export class Persona {

  /** ID */
  id: number;
  /** personaRef */
  personaRef: string;
  /** nombre */
  nombre: string;
  /** apellidos */
  apellidos: string;
  /** dni */
  dni: string;
  /** dni letra */
  dniLetra: string;

  constructor(
    id: number, personaRef: string,
    nombre: string, apellidos: string, dni: string, dniLetra: string) {
    this.id = id;
    this.personaRef = personaRef;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.dni = dni;
    this.dniLetra = dniLetra;
  }

}
