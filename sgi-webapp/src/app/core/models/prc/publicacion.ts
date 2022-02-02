import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IPublicacion extends IProduccionCientifica {
  tituloPublicacion: string;
  tipoProduccion: TipoProduccion;
  fechaPublicacion: DateTime;
}


export enum TipoProduccion {
  CAPITULO_LIBRO = '004',
  INFORME_CIENTIFICO = '018',
  ARTICULO_CIENTIFICO = '020',
  LIBRO_CIENTIFICO = '032',
  ARTICULOS_PRENSA = '075',
  DICCIONARIO_COMUN = '106',
  REVISTAS_DIFUSION_GENERAL = '173',
  ARTICULO_ENCICLOPEDIA = '202',
  ARTICULO_DIVULGACION = '203',
  TRADUCCION = '204',
  RESENA = '205',
  REVISION_BIBLIOGRAFICA = '206',
  LIBRO_DIVULGACION = '207',
  EDICION_CIENTIFICA = '208',
  DICCIONARIO_CIENTIFICO = '209',
  OTROS = 'OTHERS',
  COMENTARIO_SISTEMATICO_NORMAS = 'COMENTARIO_SISTEMATICO_NORMAS'
}

export const TIPO_PRODUCCION_MAP: Map<TipoProduccion, string> = new Map([
  [TipoProduccion.CAPITULO_LIBRO, marker('prc.publicacion.tipo-produccion.CAPITULO_LIBRO')],
  [TipoProduccion.INFORME_CIENTIFICO, marker('prc.publicacion.tipo-produccion.INFORME_CIENTIFICO')],
  [TipoProduccion.ARTICULO_CIENTIFICO, marker('prc.publicacion.tipo-produccion.ARTICULO_CIENTIFICO')],
  [TipoProduccion.LIBRO_CIENTIFICO, marker('prc.publicacion.tipo-produccion.LIBRO_CIENTIFICO')],
  [TipoProduccion.ARTICULOS_PRENSA, marker('prc.publicacion.tipo-produccion.ARTICULOS_PRENSA')],
  [TipoProduccion.DICCIONARIO_COMUN, marker('prc.publicacion.tipo-produccion.DICCIONARIO_COMUN')],
  [TipoProduccion.REVISTAS_DIFUSION_GENERAL, marker('prc.publicacion.tipo-produccion.REVISTAS_DIFUSION_GENERAL')],
  [TipoProduccion.ARTICULO_ENCICLOPEDIA, marker('prc.publicacion.tipo-produccion.ARTICULO_ENCICLOPEDIA')],
  [TipoProduccion.ARTICULO_DIVULGACION, marker('prc.publicacion.tipo-produccion.ARTICULO_DIVULGACION')],
  [TipoProduccion.TRADUCCION, marker('prc.publicacion.tipo-produccion.TRADUCCION')],
  [TipoProduccion.RESENA, marker('prc.publicacion.tipo-produccion.RESENA')],
  [TipoProduccion.REVISION_BIBLIOGRAFICA, marker('prc.publicacion.tipo-produccion.REVISION_BIBLIOGRAFICA')],
  [TipoProduccion.LIBRO_DIVULGACION, marker('prc.publicacion.tipo-produccion.LIBRO_DIVULGACION')],
  [TipoProduccion.EDICION_CIENTIFICA, marker('prc.publicacion.tipo-produccion.EDICION_CIENTIFICA')],
  [TipoProduccion.DICCIONARIO_CIENTIFICO, marker('prc.publicacion.tipo-produccion.DICCIONARIO_CIENTIFICO')],
  [TipoProduccion.OTROS, marker('prc.publicacion.tipo-produccion.OTROS')],
  [TipoProduccion.COMENTARIO_SISTEMATICO_NORMAS, marker('prc.publicacion.tipo-produccion.COMENTARIO_SISTEMATICO_NORMAS')]
]);
