
/**
 * Transforma el objeto devuelto por el dataService en el objeto final y 
 * si no devuelve nada setea la propiedad notFound a true
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("transform-result-object-mediator.mediate() - start");

  var body = mc.getPayloadJSON();
  var responseBody = null;

  if (body && body.result && Object.keys(body.result).length > 0) {
	  responseBody = body.result;
  } else {
	  mc.setProperty("notFound", "true");
  }
    
  mc.setPayloadJSON(responseBody);

  log.info("transform-result-object-mediator.mediate() - end");
}
