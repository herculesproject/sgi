
/**
 * Genera el path en el que almacenar el archivo con el formato
 * /yyyy/mm/dd/uuid.fileExtension
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("generate-relative-file-path-mediator.mediate() - start");
    
  // Ruta del fichero (fecha)
  var currentDate = new Date();
  var month = currentDate.getMonth() + 1;
  month = month < 10 ? '0' + month : month;

  var date = currentDate.getDate();
  date =  date < 10 ? '0' + date : date;
  
  var datePath =  currentDate.getFullYear() + '/' +  month + '/' + date;


  // nombre del fichero (uuid + extension fichero)
  var regex = /(?:\.([^.]+))?$/;
  var extension = regex.exec(mc.getProperty('fileName'))[1];
  
  if (!extension) {
    extension = 'undefined';
  }
  
  var fileName = mc.getProperty('uuid') + '.' + extension;
  
  
  // relativeFilePath
  var relativeFilePath = String('/' + datePath + '/' + fileName);
  mc.setProperty('relativeFilePath', relativeFilePath);

  log.info("generate-relative-file-path-mediator.mediate() - relativeFilePath: " + relativeFilePath);
  
  log.info("generate-relative-file-path-mediator.mediate() - end");
}
                    