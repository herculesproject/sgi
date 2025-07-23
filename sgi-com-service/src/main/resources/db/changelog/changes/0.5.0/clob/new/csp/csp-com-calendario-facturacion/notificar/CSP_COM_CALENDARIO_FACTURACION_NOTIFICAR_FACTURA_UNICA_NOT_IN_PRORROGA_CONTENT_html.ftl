<#ftl output_format="HTML">
<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Una vez firmado el contrato refernciado a continuación, se debe emitir la correspondiente factura. Este proceso está condicionado a la necesaria entrega de resultados. Para ello necesitamos que nos confirme que los trabajos han finalizado.</p>
<p>
- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}<br>
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}<br>
- N.º de previsión: ${data.numPrevision}<br>
- Entrega: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
<p>
En espera de su respuesta, reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Once the contract below has been signed, the relevant invoice is to be issued. This process is conditional on the necessary delivery of results. For this we need you to confirm that the work has been completed.</p>
<p>
- Company(ies): ${data.entidadesFinanciadoras?join(", ")}<br>
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Associated project code(s):  ${data.codigosSge?join(", ")}<br>
- Forecast no.: ${data.numPrevision}<br>
- Delivery: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>In relation to the work you have performed under this contract, please send us an e-mail or completion report on the services you have provided to the company(ies) in order to get your opinion, degree of implementation and completion of the work completed.</p>
<p>
We look forward to hearing from you. Kind regards,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Behean aipatutako kontratua sinatu ostean, dagokion faktura jaulki behar da. Prozesu hori gauzatzeko ezinbestekoa da emaitzak entregatzea. Horretarako, lanak amaituta daudela baieztatu behar diguzu.</p>
<p>
- Enpresak: ${data.entidadesFinanciadoras?join(", ")}<br>
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}<br>
- Aurreikuspen zk.: ${data.numPrevision}<br>
- Entrega: ${sgi.getFieldValue(data.tipoFacturacion)}
</p>
<p>Kontratu horren baitan egin dituzun lanei dagokienez, mesedez, bidal iezaguzu enpresei emandako zerbitzuei buruzko mezu elektroniko edo amaiera txosten bat, amaitutako lanen gauzatze eta betetze maila ezagutzeko, baita haien gaineko iritzia ere.</p>
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