import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-evaluacion-etica-listado',
  templateUrl: './evaluacion-etica-listado.component.html',
  styleUrls: ['./evaluacion-etica-listado.component.scss']
})
export class EvaluacionEticaListadoComponent implements OnInit {
  textoCrear = 'footer.eti.evaluacionEtica.crear';

  constructor(
    private readonly logger: NGXLogger,
  ) { }

  ngOnInit(): void {
    this.logger.debug(EvaluacionEticaListadoComponent.name, 'ngOnInit()', 'start');
    this.logger.debug(EvaluacionEticaListadoComponent.name, 'ngOnInit()', 'end');
  }
}
