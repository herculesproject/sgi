import { FormControl, FormGroup, Validators } from "@angular/forms";
import { IInvencion } from "@core/models/pii/invencion";
import { FormFragment } from "@core/services/action-service";
import { InvencionService } from "@core/services/pii/invencion/invencion.service";
import { DateTime } from "luxon";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

export class InvencionDatosGeneralesFragment extends FormFragment<IInvencion> {

  private invencion: IInvencion;

  constructor(
    key: number,
    private invencionService: InvencionService,
  ) {
    super(key);
    this.invencion = {} as IInvencion;
    this.invencion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      titulo: new FormControl('', [Validators.maxLength(50)]),
      fechaComunicacion: new FormControl(DateTime.now()),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
      tipoProteccion: new FormControl(null, [Validators.required]),
      subtipoProteccion: new FormControl(null),
      comentarios: new FormControl('', [Validators.maxLength(250)]),
    });
    return fb;
  }

  protected buildPatch(invencion: IInvencion): { [key: string]: any; } {
    const result = {
      titulo: invencion.titulo,
      fechaComunicacion: invencion.fechaComunicacion,
      descripcion: invencion.descripcion,
      tipoProteccion: invencion.tipoProteccion,
      comentarios: invencion.comentarios
    } as IInvencion;
    this.invencion = invencion;

    return result;
  }

  protected initializer(key: string | number): Observable<IInvencion> {
    throw new Error("Method not implemented.");
  }

  getValue(): IInvencion {
    const form = this.getFormGroup().value;
    const invencion = this.invencion;
    invencion.titulo = form.titulo;
    invencion.fechaComunicacion = form.fechaComunicacion;
    invencion.descripcion = form.descripcion;
    invencion.tipoProteccion = form.tipoProteccion;
    invencion.comentarios = form.comentarios;

    return invencion;
  }

  saveOrUpdate(): Observable<string | number | void> {
    const invencion = this.getValue();
    const observable$ = this.create(invencion);

    return observable$.pipe(
      map((result: IInvencion) => {
        return result.id;
      })
    );
  }

  private create(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.create(invencion);
  }
}
