
/**
 * Recupera el path sin parametros y lo alamacena en cleanPath
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("clean-path-mediator.mediate() - start");
	  
  var path = mc.getProperty('path');
  var indexOfParams = path.indexOf('?');
	  
  var cleanPath = path;
  if (indexOfParams > 0) {
    cleanPath = path.substr(0, path.indexOf('?'));
  }

  log.info("clean-path-mediator.mediate() - cleanPath=" + cleanPath);

  mc.setProperty('cleanPath', cleanPath);
  
  log.info("clean-path-mediator.mediate() - end");
}