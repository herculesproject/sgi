import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AfterViewInit, OnDestroy, OnInit } from '@angular/core';
import { AbstractTabComponent } from './abstract-tab.component';
import { Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { SnackBarService } from '../services/snack-bar.service';

const MSG_ERROR_FORM = marker('form-group.error');

export abstract class AbstractFormularioComponent implements OnInit, OnDestroy, AfterViewInit {
  tabOrigen: number;
  errorTabs: string[];

  tabs: Map<number, AbstractTabComponent<any>>;
  subscripciones: Subscription[];

  protected constructor(
    protected logger: NGXLogger,
    protected readonly snackBarService: SnackBarService
  ) { }

  ngOnInit(): void {
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnInit()', 'start');
    this.tabOrigen = 0;
    this.errorTabs = [];
    this.tabs = new Map<number, AbstractTabComponent<any>>();
    this.subscripciones = [];
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.inicializarTabs();
    this.tabs?.forEach(() => this.errorTabs.push(undefined));
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnDestroy()', 'start');
    this.subscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Calcula y muestra los errores del formulario de la tab que inicia el
   * cambia de tab
   *
   * @param tabChangeEvent Tab origen desde la cual se inicia el cambio de tab
   */
  cambioTab(tabChangeEvent: MatTabChangeEvent): void {
    this.logger.debug(AbstractFormularioComponent.name, 'cambioTab(tabChangeEvent: MatTabChangeEvent)', 'start');
    this.tabs.get(this.tabOrigen).checkWarning();
    this.setErroresTab(this.tabOrigen, this.tabs.get(this.tabOrigen).comprobarErrores());
    this.tabOrigen = tabChangeEvent.index;
    this.logger.debug(AbstractFormularioComponent.name, 'cambioTab(tabChangeEvent: MatTabChangeEvent)', 'end');
  }

  /**
   * Muestra en el selector el número de errores que tiene la tab en su formGroup
   *
   * @param numTab Posición de la tab
   * @param numErrores Número de errores detectados
   */
  setErroresTab(numTab: number, numErrores: string): void {
    this.logger.debug(AbstractFormularioComponent.name, 'setErroresTab(numTab: number, numErrores: string)', 'start');
    this.errorTabs[numTab] = numErrores !== '0' ? numErrores : undefined;
    this.logger.debug(AbstractFormularioComponent.name, 'setErroresTab(numTab: number, numErrores: string)', 'end');
  }

  /**
   * Comprueba y envia los datos al servidor para guardarlos
   */
  guardarDatos(): void {
    if (this.comprobarFormGroup()) {
      this.enviarDatos();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
  }


  /**
   * Comprueba que todos los formGroup no tengan errores
   */
  private comprobarFormGroup(): boolean {
    let result = true;
    for (let i = 0; i < this.tabs.size; i++) {
      const errors = this.tabs.get(i).comprobarErrores();
      if (errors) {
        this.setErroresTab(i, errors);
        result = false;
      }
    }
    return result;
  }

  /**
   * Crea las referencias a las tabs del formulario
   */
  protected abstract inicializarTabs(): void;

  /**
   * Envia los datos al servidor
   */
  protected abstract enviarDatos();
}
