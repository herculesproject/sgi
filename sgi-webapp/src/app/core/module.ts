
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export class Module {

  private constructor(
    public readonly title: string,
    public readonly path: string
  ) { }

  public static readonly ETI = new Module(marker('cabecera.modulo.eti'), 'eti');

  public static fromPath(path: string): Module {
    switch (path) {
      case Module.ETI.path:
        return Module.ETI;
      default:
        return undefined;
    }
  }

  public toString(): string {
    return this.path;
  }
}


