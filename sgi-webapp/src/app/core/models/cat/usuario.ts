export class Usuario {

  /** ID */
  id: number;
  /** usuarioRef */
  usuarioRef: string;
  /** nombre */
  nombre: string;
  /** apellidos */
  apellidos: string;
  /** dni */
  dni: string;
  /** dni letra */
  dniLetra: string;

  constructor(
    id: number, usuarioRef: string,
    nombre: string, apellidos: string, dni: string, dniLetra: string) {
    this.id = id;
    this.usuarioRef = usuarioRef;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.dni = dni;
    this.dniLetra = dniLetra;
  }

}
