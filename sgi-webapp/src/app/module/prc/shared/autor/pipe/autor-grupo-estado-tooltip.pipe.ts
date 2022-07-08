import { Pipe, PipeTransform } from '@angular/core';
import { IAutorGrupo } from '@core/models/prc/autor-grupo';
import { TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'autorGrupoEstadoTooltip'
})
export class AutorGrupoEstadoTooltipPipe implements PipeTransform {

  constructor(private readonly translateService: TranslateService) { }

  transform(grupos: IAutorGrupo[]): string {
    if (!grupos) {
      return '';
    }
    return grupos.map(autorGrupo =>
      this.translateService.instant(TIPO_ESTADO_PRODUCCION_MAP.get(autorGrupo.estado)) + ' (' + autorGrupo.grupo.codigo + ')'
    ).join(', ');
  }
}
