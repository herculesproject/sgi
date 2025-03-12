import { IResultadoInformePatentibilidad } from "@core/models/pii/resultado-informe-patentabilidad";
import { IResultadoInformePatentibilidadResponse } from "../resultado-informe-patentabilidad/resultado-informe-patentabilidad-response";

export interface IInformePatentabilidadResponse {
  id: number;
  invencionId: number;
  fecha: string;
  nombre: string;
  documentoRef: string;
  resultadoInformePatentabilidad: IResultadoInformePatentibilidadResponse;
  entidadCreadoraRef: string;
  contactoEntidadCreadora: string;
  contactoExaminador: string;
  comentarios: string;
}