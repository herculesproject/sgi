import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { MatTableDataSource, MatTable } from '@angular/material/table';

import { NGXLogger } from 'ngx-logger';

import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';

import { Gestor } from '@core/models/gestor';
import { Supervision } from '@core/models/supervision';



@Component({
  selector: 'app-agrupacion-servicio-gestor',
  templateUrl: './agrupacion-servicio-gestor.component.html',
  styleUrls: ['./agrupacion-servicio-gestor.component.scss']
})
export class AgrupacionServicioGestorComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  @ViewChild(MatTable, { static: true }) table: MatTable<any>;
  displayedColumns: string[] = ['nombre', 'apellidos', 'dni', 'acciones'];
  dataSource: MatTableDataSource<Gestor>;

  gestorListado: Gestor[];

  gestorFromGroup: FormGroup;

  constructor(private readonly logger: NGXLogger) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  ngOnInit(): void {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'ngOnInit()', 'start');

    this.dataSource = new MatTableDataSource<Gestor>([]);

    // Mock de gestores
    this.mockGestoresList();

    this.getGestoresServicio();

    this.gestorFromGroup = new FormGroup({
      gestor: new FormControl('', [
        Validators.required
      ])
    });

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Método para mockear listado de gestores.
   * Deberá realizarse llamada a api para recuperar los usuarios con 
   * perfil ACT-CAT-001-Gestor sobre la unidad de gestión vinculada al módulo de CAT (SAI, ACTI,... ).
   */
  mockGestoresList() {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'mockGestoresList()', 'start');

    const gestor1 = new Gestor('UsuarioRef1', 'Nombre gestor1', 'Apellidos gestor1', '55599977', 'A');
    const gestor2 = new Gestor('UsuarioRef2', 'Nombre gestor2', 'Apellidos gestor2', '88475598', 'G');
    const gestor3 = new Gestor('UsuarioRef5', 'Nombre gestor5', 'Apellidos gestor5', '65876545', 'W');
    const gestor4 = new Gestor('UsuarioRef10', 'Nombre gestor10', 'Apellidos gestor10', '25648879', 'P');
    const gestor5 = new Gestor('UsuarioRef98', 'Nombre gestor98', 'Apellidos gestor98', '48787913', 'R');

    this.gestorListado = [];

    this.gestorListado.push(gestor1);
    this.gestorListado.push(gestor2);
    this.gestorListado.push(gestor3);
    this.gestorListado.push(gestor4);
    this.gestorListado.push(gestor5);

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'mockGestoresList()', 'start');
  }


  /**
   * Se añade el gestor seleccionado en la tabla de gestores del servicio.
   */
  aniadirGestor() {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'aniadirGestor()', 'start');

    this.dataSource.data.push(this.gestorFromGroup.controls.gestor.value);
    this.table.renderRows();

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'aniadirGestor()', 'end');


  }

  /**
   * En caso de estar actualizando un servicio se recuperarán los gestores de este,
   * en caso contrario se inicializará la tabla a vacío.
   */
  getGestoresServicio() {
    this.logger.debug(AgrupacionServicioGestorComponent.name, 'getGestoresServicio()', 'start');

    // Creación de servicio
    this.dataSource.data = [];

    this.logger.debug(AgrupacionServicioGestorComponent.name, 'getGestoresServicio()', 'end');


  }



}
