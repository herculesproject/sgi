import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AfterViewInit, OnDestroy, OnInit } from '@angular/core';
import { AbstractTabComponent } from './abstract-tab.component';
import { Subscription } from 'rxjs';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';

const MSG_ERROR_FORM = marker('form-group.error');

export abstract class AbstractFormularioComponent implements OnInit, OnDestroy, AfterViewInit {
  tabOrigen: number;
  errorTabs: string[];

  tabs: Map<number, AbstractTabComponent<any>>;
  suscripciones: Subscription[];

  protected constructor(
    protected logger: NGXLogger,
    protected readonly snackBarService: SnackBarService
  ) { }

  ngOnInit(): void {
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnInit()', 'start');
    this.tabOrigen = 0;
    this.errorTabs = [];
    this.tabs = new Map<number, AbstractTabComponent<any>>();
    this.suscripciones = [];
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.initTabs();
    this.tabs?.forEach(() => this.errorTabs.push(undefined));
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(AbstractFormularioComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Calcula y muestra los errores del formulario de la tab que inicia el
   * cambia de tab
   *
   * @param tabChangeEvent Tab origen desde la cual se inicia el cambio de tab
   */
  cambioTab(tabChangeEvent: MatTabChangeEvent): void {
    this.logger.debug(AbstractFormularioComponent.name, `cambioTab(${tabChangeEvent})`, 'start');
    this.tabs.get(this.tabOrigen).checkWarning();
    this.setErroresTab(this.tabOrigen, this.tabs.get(this.tabOrigen).checkErrores());
    this.tabOrigen = tabChangeEvent.index;
    this.logger.debug(AbstractFormularioComponent.name, `cambioTab(${tabChangeEvent})`, 'end');
  }

  /**
   * Muestra en el selector el número de errores que tiene la tab en su formGroup
   *
   * @param numTab Posición de la tab
   * @param numErrores Número de errores detectados
   */
  setErroresTab(numTab: number, numErrores: string): void {
    this.logger.debug(AbstractFormularioComponent.name, `setErroresTab(${numTab}, ${numErrores})`, 'start');
    this.errorTabs[numTab] = numErrores !== '0' ? numErrores : undefined;
    this.logger.debug(AbstractFormularioComponent.name, `setErroresTab(${numTab}, ${numErrores})`, 'end');
  }

  /**
   * Comprueba y envia los datos al servidor para guardarlos
   */
  saveData(): void {
    if (this.checkFormGroup()) {
      this.sendData();
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
  }


  /**
   * Comprueba que todos los formGroup no tengan errores
   */
  private checkFormGroup(): boolean {
    this.logger.debug(AbstractFormularioComponent.name, `checkFormGroup`, 'start');
    let result = true;
    for (let i = 0; i < this.tabs.size; i++) {
      const errors = this.tabs.get(i).checkErrores();
      if (errors) {
        this.setErroresTab(i, errors);
        result = false;
      }
    }
    this.logger.debug(AbstractFormularioComponent.name, `checkFormGroup`, 'end');
    return result;
  }

  /**
   * Crea las referencias a las tabs del formulario
   */
  protected abstract initTabs(): void;

  /**
   * Envia los datos al servidor
   */
  protected abstract sendData(): void;
}
