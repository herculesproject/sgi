
/**
 * Transforma el objeto recibido en un obejto valido para el dataService
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("transform-input-object-mediator.mediate() - start");

  var body = mc.getPayloadJSON();
  var id = mc.getProperty('id');
  
  if (body && id) {
	  body.id = id;
  }
  
  var responseBody = {
    item: body
  };
    
  mc.setPayloadJSON(responseBody);

  log.info("transform-input-object-mediator.mediate() - end");
}
