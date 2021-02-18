
/**
 * Transforma el objeto devuelto por el dataService en una lista y setea X-Page-Total-Count
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("transform-result-list-mediator.mediate() - start");

  var body = mc.getPayloadJSON();
  var responseBody = body.result ? (body.result.items ? body.result.items  : []) : [];

  mc.setPayloadJSON(responseBody);

  mc.setProperty("X-Page-Total-Count", responseBody.length.toString());

  log.info("transform-result-list-mediator.mediate() - end");
}