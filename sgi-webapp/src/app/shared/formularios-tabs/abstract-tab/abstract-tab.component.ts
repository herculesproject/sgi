import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormGroupUtil } from '@core/services/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-abstract-tab',
  templateUrl: './abstract-tab.component.html',
  styleUrls: ['./abstract-tab.component.scss']
})
export abstract class AbstractTabComponent<T> implements OnInit, OnDestroy {
  warning: boolean;
  error: boolean;
  datosIniciales: T;
  datosFormulario: T;

  formGroup: FormGroup;
  @Output() eventEmitter: EventEmitter<string>;
  primeraComprobacion: boolean;
  subscripciones: Subscription[];

  protected constructor(
    protected logger: NGXLogger
  ) {
    this.eventEmitter = new EventEmitter<string>();
  }

  ngOnInit(): void {
    this.logger.debug(AbstractTabComponent.name, 'ngOnInit()', 'start');
    this.primeraComprobacion = true;
    this.subscripciones = [];
    this.warning = false;
    this.error = false;

    this.logger.debug(AbstractTabComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractTabComponent.name, 'ngOnDestroy()', 'start');
    this.subscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(AbstractTabComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Comprueba los errores del formulario
   */
  comprobarErrores(): string {
    this.logger.debug(AbstractTabComponent.name, 'comprobarErrores()', 'start');
    this.error = false;
    const errors = FormGroupUtil.getNumErrors(this.formGroup);
    this.crearSubcripcionesCambiosValor();
    this.logger.debug(AbstractTabComponent.name, 'comprobarErrores()', 'end');
    return errors === 0 ? undefined : String(errors);
  }

  /**
   * Crea subcripciones para saber cuando cambia cualquier valor del formGroup
   * y actualizar el número de fallos de la tab
   */
  private crearSubcripcionesCambiosValor(): void {
    this.logger.debug(AbstractTabComponent.name, 'crearSubcripcionesCambiosValor()', 'start');
    if (this.primeraComprobacion) {
      this.primeraComprobacion = false;
      this.subscripciones = FormGroupUtil.subscribeValues(
        this.formGroup, () => {
          this.eventEmitter.emit(this.comprobarErrores());
        });
    }
    this.logger.debug(AbstractTabComponent.name, 'crearSubcripcionesCambiosValor()', 'end');
  }

  /**
   * Comprueba si se debe mostrar el indicador de que hay datos modificados sin guardar
   */
  checkWarning() {
    this.logger.debug(AbstractTabComponent.name, 'checkWarning()', 'start');
    this.warning = !this.equals();
    this.logger.debug(AbstractTabComponent.name, 'checkWarning()', 'end');
  }

  /**
   * Comprueba si los datos de formularios son iguales a los datos iniciales
   */
  protected equals(): boolean {
    this.logger.debug(AbstractTabComponent.name, 'equals()', 'start');
    const datosIniciales = JSON.stringify(this.datosIniciales);
    const datosFormulario = JSON.stringify(this.getDatosFormulario());
    const result = datosIniciales === datosFormulario;
    this.logger.debug(AbstractTabComponent.name, 'equals()', 'end');
    return result;
  }


  /**
   * Muestra que ha ocurrido un error al cargar los datos al servidor
   */
  mostrarError() {
    this.error = true;
    this.warning = false;
  }

  /**
   * Actualiza los datos iniciales con la respuesta del servidor
   *
   * @param res Respuesta del servidor
   */
  actualizarDatos(res: T) {
    this.warning = false;
    this.datosIniciales = res;
    this.datosFormulario = res;
  }



  /**
   * Crea el formGroup que usará la pestaña
   */
  abstract crearFormGroup(): FormGroup;


  /**
   * Carga los datos del formulario a la entidad correspodiente
   */
  abstract getDatosFormulario(): T;
}
