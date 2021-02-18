
/**
 * Recupera el numero de elementos devueltos por el dataService que hace el contador 
 * y setea las propiedades X-Page-Count y X-Total-Count y X-Page-Size si no tiene valor.
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("total-count-mediator.mediate() - start");
  
  var body = mc.getPayloadJSON();
  var count = body.result ? body.result.count : 0;
  
  var page = mc.getProperty('X-Page');
  var pageSize =  mc.getProperty('X-Page-Size');


  if (page && pageSize) {
    var pageCount = Math.ceil(count / pageSize);
    mc.setProperty("X-Page", page.toString());
    mc.setProperty("X-Page-Size", pageSize.toString());
    mc.setProperty("X-Page-Count", pageCount.toString());
  } else {
	mc.setProperty("X-Page", '0');
    mc.setProperty("X-Page-Count", '1');
    mc.setProperty('X-Page-Size', count.toString());
  }

  mc.setProperty("X-Total-Count", count.toString());
  
  log.info("total-count-mediator.mediate() - X-Page=" + mc.getProperty('X-Page'));
  log.info("total-count-mediator.mediate() - X-Page-Size=" + mc.getProperty('X-Page-Size'));
  log.info("total-count-mediator.mediate() - X-Page-Count=" + mc.getProperty('X-Page-Count'));
  log.info("total-count-mediator.mediate() - X-Total-Count=" + mc.getProperty('X-Total-Count'));

  log.info("total-count-mediator.mediate() - end");
}