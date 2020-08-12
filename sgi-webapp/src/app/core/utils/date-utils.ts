/**
 * Clase para definir funciones para trabajar con fechas
 */
export class DateUtils {

  /**
   * Recupera la fecha de inicio del dia correspondiente a la fecha (yyyy-MM-dd 00:00:00.000).
   * @param fecha una fecha
   *
   * @return una nueva fecha correspondiente al inicio del dia
   */
  static getFechaInicioDia(fecha: Date): Date {
    if (fecha) {
      const fechaInicioDia = new Date(fecha);
      fechaInicioDia.setHours(0, 0, 0, 0);
      return fechaInicioDia;
    }
    return null;
  }

  /**
   * Recupera la fecha de fin del dia correspondiente a la fecha (yyyy-MM-dd 23:59:59.999).
   * @param fecha una fecha
   *
   * @return una nueva fecha correspondiente al fin del dia
   */
  static getFechaFinDia(fecha: Date): Date {
    if (fecha) {
      const fechaFinDia = new Date(fecha);
      fechaFinDia.setHours(23, 59, 59, 999);
      return fechaFinDia;
    }
    return null;
  }

  /**
   * Formatea la fecha con formato ISO yyyy-MM-dd, ej. 2020-07-23.
   *
   * @param fecha una fecha
   * @return un string con la fecha formateada o '' si la fecha no esta definida.
   */
  static formatFechaAsISODate(fecha: Date): string {
    if (!fecha) {
      return '';
    }

    let mes = (fecha.getMonth() + 1).toString();
    let dia = fecha.getDate().toString();
    const anio = fecha.getFullYear().toString();

    if (mes.length < 2) {
      mes = '0' + mes;
    }

    if (dia.length < 2) {
      dia = '0' + dia;
    }

    return [anio, mes, dia].join('-');
  }


  /**
   * Formatea la fecha con hora con formato ISO yyyy-MM-dd'T'HH:mm:ss, ej. 2020-07-23T09:17:00.
   *
   * @param fecha una fecha
   * @return un string con la fecha formateada o '' si la fecha no esta definida.
   */
  static formatFechaAsISODateTime(fecha: Date): string {
    if (!fecha) {
      return '';
    }

    const fechaString = this.formatFechaAsISODate(fecha);

    let hora = fecha.getHours().toString();
    let minuto = fecha.getMinutes().toString();
    let segundo = fecha.getSeconds().toString();

    if (hora.length < 2) {
      hora = '0' + hora;
    }

    if (minuto.length < 2) {
      minuto = '0' + minuto;
    }

    if (segundo.length < 2) {
      segundo = '0' + segundo;
    }

    const horaString = [hora, minuto, segundo].join(':');


    return [fechaString, horaString].join('T');
  }


}
