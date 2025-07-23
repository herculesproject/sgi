import { I18nFieldValue } from "@core/i18n/i18n-field";

interface TipoConfiguracion {
  id: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}

interface TipoConfiguracionI18n {
  id: number;
  nombre: I18nFieldValue[];
  descripcion?: I18nFieldValue[];
  activo: boolean;
}


// tslint:disable: no-empty-interface
export interface IModeloEjecucion extends TipoConfiguracion {
  externo: boolean;
  contrato: boolean;
  solicitudSinConvocatoria: boolean;
}

export interface ITipoDocumento extends TipoConfiguracion {
}

export interface ITipoHito extends TipoConfiguracion {
}

export interface ITipoFinalidad extends TipoConfiguracionI18n {
}

export interface ITipoFase extends TipoConfiguracionI18n {
}

export interface ITipoEnlace extends TipoConfiguracion {
}

export interface ITipoFinanciacion extends TipoConfiguracionI18n {
}

export interface ITipoUnidadGestion extends TipoConfiguracion {
}

export interface ITipoAmbitoGeografico extends TipoConfiguracionI18n {
}

export interface ITipoRegimenConcurrencia extends TipoConfiguracion {
}

export interface ITipoOrigenFuenteFinanciacion extends TipoConfiguracionI18n {
}
