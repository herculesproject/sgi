import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';

@Component({
  selector: 'app-evaluacion-etica-listado',
  templateUrl: './evaluacion-etica-listado.component.html',
  styleUrls: ['./evaluacion-etica-listado.component.scss']
})
export class EvaluacionEticaListadoComponent implements OnInit {
  textoCrear: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly traductor: TraductorService,
  ) {
    this.textoCrear = this.traductor.getTexto('footer.eti.evaluacionEtica.crear');
  }

  ngOnInit(): void {
    this.logger.debug(EvaluacionEticaListadoComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(EvaluacionEticaListadoComponent.name, 'ngOnInit()', 'end');
  }
}
