<#ftl output_format="HTML">
<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Siguiendo el calendario de facturación del contrato referenciado más abajo, se debe emitir la ÚLTIMA factura de la PRORROGA. Para poder realizar la emisión de dicha factura, es necesario que nos indique si ha realizado las entregas previstas.</p>
<p>
- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}<br>
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}<br>
- N.º de previsión: ${data.numPrevision}<br>
- Tipo facturación: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>En relación a los trabajos que ha realizado en el marco de este contrato, es aconsejable que nos remita copia de los informes finales entregados a la/s empresa/s, objeto del contrato, para conocer del desarrollo, ejecución y cumplimiento de los trabajos.</p>
<p>
En espera de su respuesta, reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>In accordance with the below contract invoicing schedule, the LAST invoice of the EXTENSION is to be issued. In order to be able to issue such invoice, it is necessary to indicate whether you have made the expected deliveries.</p>
<p>
- Company(ies): ${data.entidadesFinanciadoras?join(", ")}<br>
- Title of the contract:  ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Associated project code(s): ${data.codigosSge?join(", ")}<br>
- Forecast no.: ${data.numPrevision}<br>
- Invoicing type: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>In relation to the work you have carried out within the framework of this contract, it is advisable that you send us a copy of the final reports delivered to the company(ies) subject to the contract, in order to know about the development, implementation and fulfilment of the work.</p>
<p>
We look forward to hearing from you. Kind regards,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Behean aipatutako kontratuaren fakturazio egutegiari jarraituz, LUZAPENEKO AZKEN faktura jaulki behar da. Faktura jaulki ahal izateko, aurreikusita zeuden entregak egin dituzun esan behar diguzu.</p>
<p>
- Enpresak: ${data.entidadesFinanciadoras?join(", ")}<br>
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}<br>
- Aurreikuspen zk.: ${data.numPrevision}<br>
- Fakturazio mota: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>Kontratu horren baitan egin dituzun lanei dagokienez, komenigarria da guri ere bidaltzea kontratuak xedetzat zituen enpresei aurkeztutako amaierako txostenen kopia bat, lanak nola garatu, gauzatu eta bete diren jakin dezagun.</p>
<p>
Zure erantzunaren zain, jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>