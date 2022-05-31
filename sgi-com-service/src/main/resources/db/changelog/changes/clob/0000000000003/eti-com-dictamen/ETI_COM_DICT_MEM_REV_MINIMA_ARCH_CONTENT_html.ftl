<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>ante la ausencia de respuesta a las modificaciones solicitadas por el por el/la ${data.nombreInvestigacion}, con respecto a la solicitud de evaluación del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.</p>   
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>