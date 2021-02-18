
/**
 * Compone el filtro para buscar por la lista de ids recuperada del path
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("find-all-by-ids-mediator.mediate() - start");
  
  var ids = mc.getProperty('ids');
  var filterQuery = " id IN(";

  for each (var id in String(ids).split('|')) {
    filterQuery += "'" + id + "',";
  }

  filterQuery = String(filterQuery).slice(0, -1) + ") ";
          
  mc.setProperty('filterQuery', encodeURI(filterQuery));
  mc.setProperty('sortQuery', "");
  mc.setProperty('paginationQuery', "");

  log.info("find-all-by-ids-mediator.mediate() - filterQuery=" + filterQuery);

  log.info("find-all-by-ids-mediator.mediate() - end");
}