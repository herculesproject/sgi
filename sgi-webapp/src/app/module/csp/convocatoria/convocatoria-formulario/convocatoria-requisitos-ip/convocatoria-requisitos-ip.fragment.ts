import { FormFragment } from '@core/services/action-service';
import { IRequisitoIP } from '@core/models/csp/requisito-ip';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NGXLogger } from 'ngx-logger';
import { RequisitoIPService } from '@core/services/csp/requisito-ip.service';

export class ConvocatoriaRequisitosIPFragment extends FormFragment<IRequisitoIP> {

  private requisitoIP: IRequisitoIP;

  constructor(
    private fb: FormBuilder,
    private readonly logger: NGXLogger,
    key: number,
    private requisitoIPService: RequisitoIPService) {
    super(key);
    this.requisitoIP = {} as IRequisitoIP;
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'buildFormGroup()', 'start');
    return this.fb.group({
      numMaximoIP: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      nivelAcademicoRef: ['', Validators.maxLength(50)],
      aniosNivelAcademico: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      sexo: [null],
      vinculacionUniversidad: [null],
      modalidadContratoRef: ['', Validators.maxLength(50)],
      aniosVinculacion: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMinimoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMinimoNoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoNoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      otrosRequisitos: ['']
    });
  }

  protected initializer(key: number): Observable<IRequisitoIP> {
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'initializer(key: number)', 'start');
    if (this.getKey()) {
      return this.requisitoIPService.getRequisitoIPConvocatoria(key).pipe(
        map((requisitoIP) => {
          this.requisitoIP = requisitoIP;
          this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'initializer(key: number)', 'end');
          return requisitoIP;
        })
      );
    }
  }

  buildPatch(value: IRequisitoIP): { [key: string]: any } {
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'buildPatch(value: IRequisitoIP)', 'start');
    return {
      numMaximoIP: value.numMaximoIP,
      nivelAcademicoRef: value.nivelAcademicoRef,
      aniosNivelAcademico: value.aniosNivelAcademico,
      edadMaxima: value.edadMaxima,
      sexo: value.sexo,
      vinculacionUniversidad: value.vinculacionUniversidad,
      modalidadContratoRef: value.modalidadContratoRef,
      aniosVinculacion: value.aniosVinculacion,
      numMinimoCompetitivos: value.numMinimoCompetitivos,
      numMinimoNoCompetitivos: value.numMinimoNoCompetitivos,
      numMaximoCompetitivosActivos: value.numMaximoCompetitivosActivos,
      numMaximoNoCompetitivosActivos: value.numMaximoNoCompetitivosActivos,
      otrosRequisitos: value.otrosRequisitos
    };
  }

  getValue(): IRequisitoIP {
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'getValue()', 'start');
    const form = this.getFormGroup().value;
    this.requisitoIP.numMaximoIP = form.numMaximoIP;
    this.requisitoIP.nivelAcademicoRef = form.nivelAcademicoRef;
    this.requisitoIP.aniosNivelAcademico = form.aniosNivelAcademico;
    this.requisitoIP.edadMaxima = form.edadMaxima;
    this.requisitoIP.sexo = form.sexo;
    this.requisitoIP.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoIP.modalidadContratoRef = form.modalidadContratoRef;
    this.requisitoIP.aniosVinculacion = form.aniosVinculacion;
    this.requisitoIP.numMinimoCompetitivos = form.numMinimoCompetitivos;
    this.requisitoIP.numMinimoNoCompetitivos = form.numMinimoNoCompetitivos;
    this.requisitoIP.numMaximoCompetitivosActivos = form.numMaximoCompetitivosActivos;
    this.requisitoIP.numMaximoNoCompetitivosActivos = form.numMaximoNoCompetitivosActivos;
    this.requisitoIP.otrosRequisitos = form.otrosRequisitos;
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'getValue()', 'end');
    return this.requisitoIP;
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'saveOrUpdate()', 'start');
    const datosRequisitoIP = this.getValue();
    const obs = this.isEdit() ? this.requisitoIPService.update(this.getKey() as number, datosRequisitoIP) :
      this.requisitoIPService.create(datosRequisitoIP);
    return obs.pipe(
      map((value) => {
        this.requisitoIP = value;
        this.logger.debug(ConvocatoriaRequisitosIPFragment.name, 'saveOrUpdate()', 'end');
        return this.requisitoIP.id;
      })
    );
  }

}
