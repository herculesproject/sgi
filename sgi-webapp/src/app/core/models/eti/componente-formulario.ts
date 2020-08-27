export class ComponenteFormulario {
  /** Id */
  id: number;

  /** Esquema */
  esquema: string;

  constructor(componenteFormulario?: ComponenteFormulario) {
    this.id = componenteFormulario?.id;
    this.esquema = componenteFormulario?.esquema;
  }
}
