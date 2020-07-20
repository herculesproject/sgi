import {Injectable} from '@angular/core';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {TraductorService} from '@core/services/traductor.service';

@Injectable({
  providedIn: 'root',
})
export class TraductorPaginatorService extends MatPaginatorIntl {
  constructor(private readonly traductor: TraductorService) {
    super();
  }

  changeWords(): void {
    this.itemsPerPageLabel = this.traductor.getTexto(
      'paginator.itemsPerPageLabel'
    );
    this.nextPageLabel = this.traductor.getTexto(
      'paginator.nextPageLabel'
    );
    this.previousPageLabel = this.traductor.getTexto(
      'paginator.previousPageLabel'
    );
    this.lastPageLabel = this.traductor.getTexto(
      'paginator.lastPageLabel'
    );
  }

  getRangeLabel = function(page: number, pageSize: number, length: number) {
    this.changeWords();
    if (length === 0 || pageSize === 0) {
      return `0 ${this.traductor.getTexto('paginator.of')} ${length}`;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex =
      startIndex < length
        ? Math.min(startIndex + pageSize, length)
        : startIndex + pageSize;
    return (
      `${startIndex + 1} - ${endIndex} ` +
      `${this.traductor.getTexto('paginator.of')} ${length}`
    );
  };
}
