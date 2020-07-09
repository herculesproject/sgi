export class Gestor {

  /** Usuario ref. */
  usuarioRef: string;

  /** Nombre. */
  nombre: string;

  /** Apellidos. */
  apellidos: string;

  /** Dni. */
  dni: string;

  /** Letra dni. */
  dniLetra: string;

  constructor(usuarioRef: string, nombre: string, apellidos: string, dni: string, dniLetra: string) {
    this.usuarioRef = usuarioRef;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.dni = dni;
    this.dniLetra = dniLetra;
  }

}
