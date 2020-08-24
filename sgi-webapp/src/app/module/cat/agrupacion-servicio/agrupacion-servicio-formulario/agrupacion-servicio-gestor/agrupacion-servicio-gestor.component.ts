import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NGXLogger } from 'ngx-logger';

import { Usuario } from '@core/models/cat/usuario';
import { Subject, ReplaySubject } from 'rxjs';
import { Supervision } from '@core/models/cat/supervision';
import { SnackBarService } from '@core/services/snack-bar.service';

const MSG_ERROR_GESTOR_EXISTE = marker('cat.servicio.gestor.existe');

@Component({
  selector: 'sgi-agrupacion-servicio-gestor',
  templateUrl: './agrupacion-servicio-gestor.component.html',
  styleUrls: ['./agrupacion-servicio-gestor.component.scss']
})
export class AgrupacionServicioGestorComponent implements OnInit {

  gestorFormGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  usuarios: Usuario[] = [];

  displayedColumns: string[] = [];
  gestores$: Subject<Usuario[]>;
  gestores: Usuario[] = [];

  gestoresAniadidos: Usuario[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private snackBarService: SnackBarService) {

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'ngOnInit()', 'start');

    this.displayedColumns = ['nombre', 'apellidos', 'dni', 'acciones'];

    this.gestores$ = new ReplaySubject();

    this.getUsuarios();

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Crea el formulario de datos generales.
   * @returns formulario.
   */
  createGroup(): FormGroup {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'createGroup()', 'start');

    this.gestorFormGroup = new FormGroup({
      gestor: new FormControl('', [
        Validators.required
      ])
    });

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'createGroup()', 'end');

    return this.gestorFormGroup;
  }

  /**
   * Setea los datos de supervisión en el formulario.
   * @param supervisiones supervisiones del servicio.
   */
  setDatosForm(supervisiones: Supervision[]) {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'setDatosForm()', 'start');

    this.gestores = this.usuarios.filter(usuario => supervisiones.some(supervisor => supervisor.usuarioRef === usuario.usuarioRef));

    this.gestores$.next(this.gestores);

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'createGroup()', 'end');
  }


  /**
   * Mock de usuarios.
   */
  getUsuarios(): void {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'getUsuarios()', 'start');

    let usuario = new Usuario(1, 'user-254', 'usuario1', 'apellidos usuario1', '78945589', 'L');
    this.usuarios.push(usuario);

    usuario = new Usuario(2, 'user-454', 'usuario2', 'apellidos usuario2', '98125589', 'P');
    this.usuarios.push(usuario);

    usuario = new Usuario(3, 'user-10', 'usuario10', 'apellidos usuario10', '56778941', 'N');
    this.usuarios.push(usuario);

    usuario = new Usuario(4, 'user-20', 'usuario20', 'apellidos usuario20', '95667841', 'W');
    this.usuarios.push(usuario);

    usuario = new Usuario(5, 'user-22', 'usuario22', 'apellidos usuario22', '85667841', 'Q');
    this.usuarios.push(usuario);

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'getUsuarios()', 'end');

  }

  /**
   * Añade el gestor seleccionado en el combo de gestores a la tabla.
   */
  aniadirGestor() {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'aniadirGestor()', 'start');

    const gestorSeleccionado = this.gestorFormGroup.controls.gestor.value;

    const gestorAsociado = this.gestores.filter(gestor => gestor.usuarioRef === gestorSeleccionado.usuarioRef);

    if (gestorAsociado.length > 0) {
      this.snackBarService.showError(MSG_ERROR_GESTOR_EXISTE);
    } else {
      this.gestores.push(gestorSeleccionado);
      this.gestoresAniadidos.push(gestorSeleccionado);
      this.gestores$.next(this.gestores);
    }

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'aniadirGestor()', 'end');
  }

}
