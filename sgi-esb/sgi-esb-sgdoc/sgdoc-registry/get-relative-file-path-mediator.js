
/**
 * Recupera el path del documento y lo alamacena en relativeFilePath
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("get-relative-file-path-mediator.mediate() - start");
                  
  var body = mc.getPayloadJSON();
  var relativeFilePath = body.result ? body.result.archivo : '';
  
  log.info("get-relative-file-path-mediator.mediate() - relativeFilePath: " + relativeFilePath);
  mc.setProperty("relativeFilePath", String(relativeFilePath));
  
  log.info("get-relative-file-path-mediator.mediate() - end");
}
