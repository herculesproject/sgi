import { IPartidaPresupuestaria } from './partida-presupuestaria';

export interface IProyectoPartida extends IPartidaPresupuestaria {
  proyectoId: number;
  convocatoriaPartidaId: number;
  descripcion: string; // TODO: al pasar a i18n eliminar la descripcion aqui y en IConvocatoriaPartidaPresupuestaria y dejar solo I18nFieldValue[] en la descripcion en IPartidaPresupuestaria
}
