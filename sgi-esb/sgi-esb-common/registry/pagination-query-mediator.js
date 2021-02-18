/**
 * Recupera del header X-Page y X-Page-Size y añade a la propiedad paginationQuery la informacion para hacer
 * la paginacion en formato sql
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("pagination-query-mediator.mediate() - start");

  var page = mc.getProperty('X-Page');
  var pageSize = mc.getProperty('X-Page-Size');

  var paginationQuery = " ";

  if (page && pageSize) {
    paginationQuery += " LIMIT " + pageSize + " OFFSET " + (pageSize * page);
  } else {
    mc.setProperty('X-Page', '0');
    mc.setProperty('X-Page-Size', null);
  }

  log.info("pagination-query-mediator.mediate() - paginationQuery=" + paginationQuery);

  mc.setProperty('paginationQuery', encodeURI(paginationQuery));

  log.info("pagination-query-mediator.mediate() - end");
}