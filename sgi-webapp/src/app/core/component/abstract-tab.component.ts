import { EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

export abstract class AbstractTabComponent<T> implements OnInit, OnDestroy {
  warning: boolean;
  error: boolean;
  datosIniciales: T;
  datosFormulario: T;

  formGroup: FormGroup;
  @Output() eventEmitter: EventEmitter<string>;
  primeraComprobacion: boolean;
  suscripciones: Subscription[];

  protected constructor(
    protected logger: NGXLogger
  ) {
    this.eventEmitter = new EventEmitter<string>();
  }
  ngOnInit(): void {
    this.logger.debug(AbstractTabComponent.name, 'ngOnInit()', 'start');
    this.primeraComprobacion = true;
    this.suscripciones = [];
    this.warning = false;
    this.error = false;
    this.formGroup = this.createFormGroup();
    this.logger.debug(AbstractTabComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractTabComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x?.unsubscribe());
    this.logger.debug(AbstractTabComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Comprueba los errores del formulario
   */
  checkErrores(): string {
    this.logger.debug(AbstractTabComponent.name, 'comprobarErrores()', 'start');
    this.error = false;
    const errors = FormGroupUtil.getNumErrors(this.formGroup);
    this.createSuscripcionesCambiosValor();
    this.logger.debug(AbstractTabComponent.name, 'comprobarErrores()', 'end');
    return errors === 0 ? undefined : String(errors);
  }

  /**
   * Crea suscripciones para saber cuando cambia cualquier valor del formGroup
   * y actualizar el número de fallos de la tab
   */
  private createSuscripcionesCambiosValor(): void {
    this.logger.debug(AbstractTabComponent.name, 'crearSubcripcionesCambiosValor()', 'start');
    if (this.primeraComprobacion) {
      this.primeraComprobacion = false;
      this.suscripciones = FormGroupUtil.subscribeValues(
        this.formGroup, () => {
          this.eventEmitter.emit(this.checkErrores());
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
    const datosIniciales = this.datosIniciales ? JSON.stringify(this.datosIniciales) : null;
    this.datosFormulario = this.getDatosFormulario();
    const datosFormulario = this.datosFormulario ? JSON.stringify(this.datosFormulario) : null;
    const result = datosIniciales === datosFormulario;
    this.logger.debug(AbstractTabComponent.name, 'equals()', 'end');
    return result;
  }


  /**
   * Muestra que ha ocurrido un error al cargar los datos al servidor
   */
  showError() {
    this.error = true;
    this.warning = false;
  }

  /**
   * Actualiza los datos iniciales con la respuesta del servidor
   *
   * @param res Respuesta del servidor
   */
  updateDatos(res: T) {
    this.warning = false;
    this.datosIniciales = res;
    this.datosFormulario = res;
  }

  /**
   * Crea el formGroup que usará la pestaña
   */
  abstract createFormGroup(): FormGroup;

  /**
   * Carga los datos del formulario a la entidad correspodiente
   */
  abstract getDatosFormulario(): T;
}
