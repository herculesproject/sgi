<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>${data.nombreApellidosSolicitante} ha registrado en nuestra base de datos, con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")}, una alegación a la resolución  de la convocatoria ${data.tituloConvocatoria} con  fecha provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")}, en relación a la solicitud ${data.codigoInternoSolicitud}.</p>
  </body>
</html>
