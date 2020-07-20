import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {NGXLogger} from 'ngx-logger';
import {Observable, Subscription} from 'rxjs';
import {FormGroupUtil} from '@core/services/form-group-util';

@Component({
  selector: 'app-abstract-tab',
  templateUrl: './abstract-tab.component.html',
  styleUrls: ['./abstract-tab.component.scss']
})
export abstract class AbstractTabComponent<T> implements OnInit, OnDestroy {
  warning: boolean;
  error: boolean;
  datosIniciales: T;

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
    this.formGroup = new FormGroup({});
    this.primeraComprobacion = true;
    this.subscripciones = [];
    this.warning = false;
    this.error = false;
    this.datosIniciales = this.getDatosIniciales();
    this.logger.debug(AbstractTabComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractTabComponent.name, 'ngOnDestroy()', 'start');
    this.subscripciones.forEach(x => x.unsubscribe());
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
    const result = JSON.stringify(this.datosIniciales) === JSON.stringify(this.formGroup.value);
    this.logger.debug(AbstractTabComponent.name, 'equals()', 'end');
    return result;
  }

  /**
   * Ejecuta la llamada al servidor
   */
  mandarPeticion(): Observable<any> {
    return this.crearObservable();
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
  }

  /**
   * Crea la petición de la tab que se ejecutará cuando se guarde la
   * información en el servidor
   */
  abstract crearObservable(): Observable<any>;

  /**
   * Carga los datos por defecto para la pestaña
   *
   * @param valor Parámetro para crear los datos si es necesario
   */
  abstract getDatosIniciales(valor?: any): T;

}
