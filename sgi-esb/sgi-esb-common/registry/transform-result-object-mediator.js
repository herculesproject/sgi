
/**
 * Transforma el objeto devuelto por el dataService en el objeto final y 
 * si no devuelve nada setea la propiedad notFound a true
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("transform-result-object-mediator.mediate() - start");

  var body = mc.getPayloadJSON();
  var responseBody = null;

  if (body && body.item && Object.keys(body.item).length > 0) {
	  responseBody = body.item;
  } else {
	  mc.setProperty("notFound", "true");
  }
    
  mc.setPayloadJSON(responseBody);

  log.info("transform-result-object-mediator.mediate() - end");
}
