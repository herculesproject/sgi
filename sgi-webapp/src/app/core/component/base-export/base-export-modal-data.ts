import { SgiRestFindOptions } from "@herculesproject/framework/http";

export interface IBaseExportModalData {
  findOptions?: SgiRestFindOptions;
  totalRegistrosExportacionExcel?: number;
  limiteRegistrosExportacionExcel?: number;
}
