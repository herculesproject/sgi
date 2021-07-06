import { FormControl, FormGroup, Validators } from "@angular/forms";
import { IInvencion } from "@core/models/pii/invencion";
import { FormFragment } from "@core/services/action-service";
import { InvencionService } from "@core/services/pii/invencion/invencion.service";
import { DateTime } from "luxon";
import { NGXLogger } from "ngx-logger";
import { EMPTY, Observable } from "rxjs";
import { catchError, map, tap } from "rxjs/operators";

export class InvencionDatosGeneralesFragment extends FormFragment<IInvencion> {

  private invencion: IInvencion;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private invencionService: InvencionService,
    private hasEditPerm: boolean
  ) {
    super(key);
    this.invencion = {} as IInvencion;
    this.invencion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      titulo: new FormControl('', [Validators.maxLength(50)]),
      fechaComunicacion: new FormControl(DateTime.now()),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
      tipoProteccion: new FormControl(null, [Validators.required]),
      subtipoProteccion: new FormControl(null),
      proyecto: new FormControl(null),
      comentarios: new FormControl('', [Validators.maxLength(250)]),
    });

    if (!this.hasEditPerm) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(invencion: IInvencion): { [key: string]: any; } {
    const result = {
      id: invencion.id,
      titulo: invencion.titulo,
      fechaComunicacion: invencion.fechaComunicacion,
      descripcion: invencion.descripcion,
      tipoProteccion: invencion.tipoProteccion,
      proyecto: invencion.proyecto,
      comentarios: invencion.comentarios
    } as IInvencion;
    this.invencion = invencion;

    return result;
  }

  protected initializer(key: number): Observable<IInvencion> {
    return this.invencionService.findById(key).pipe(
      catchError((err) => {
        this.logger.error(err);
        return EMPTY;
      })
    );
  }

  getValue(): IInvencion {
    const form = this.getFormGroup().value;
    const invencion = this.invencion;
    invencion.titulo = form.titulo;
    invencion.fechaComunicacion = form.fechaComunicacion;
    invencion.descripcion = form.descripcion;
    invencion.tipoProteccion = form.tipoProteccion;
    invencion.proyecto = form.proyecto;
    invencion.comentarios = form.comentarios;

    return invencion;
  }

  saveOrUpdate(): Observable<string | number | void> {
    const invencion = this.getValue();
    const observable$ = this.isEdit() ? this.update(invencion) : this.create(invencion);

    return observable$.pipe(
      map((result: IInvencion) => {
        return result.id;
      })
    );
  }

  private create(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.create(invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
      })
    );
  }

  private update(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.update(invencion.id, invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
      })
    );
  }
}
