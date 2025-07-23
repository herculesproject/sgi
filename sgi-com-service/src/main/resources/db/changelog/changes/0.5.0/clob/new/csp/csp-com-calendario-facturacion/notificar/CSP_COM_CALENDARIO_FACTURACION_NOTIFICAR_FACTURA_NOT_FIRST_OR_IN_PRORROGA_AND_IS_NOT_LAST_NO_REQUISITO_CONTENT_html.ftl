<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Siguiendo el calendario de facturación del contrato referenciado más abajo, está prevista la emisión de la factura que se indica. Para poder emitirla y emviarla a la empresa, es necesario que nos confirme que los trabajos progresan adecuadamente.</p>
<p>
- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}<br>
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}<br>
- N.º de previsión: ${data.numPrevision}
</p>
<p>
En espera de su respuesta, reciba un cordial saludo,<br>
Oficina de Transferencia de Resultados de Investigación<br>
otri@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>In accordance with the below contract invoicing schedule, the following invoice is expected to be issued. In order to be able to issue it and send it to the company, you need to confirm that the work is progressing properly.</p>
<p>
- Company(ies): ${data.entidadesFinanciadoras?join(", ")}<br>
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Associated project code(s): ${data.codigosSge?join(", ")}<br>
- Forecast no.: ${data.numPrevision}
</p>
<p>
We look forward to hearing from you. Kind regards,<br>
Office for the Transfer of Research Results<br>
otri@ehu.eus
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Behean aipatuta ageri den kontratuaren fakturazio egutegiari jarraituz, adierazitako faktura jaulkitzea aurreikusita dago. Faktura jaulki eta enpresara bidali ahal izateko, lanak behar bezala doazela baieztatu behar diguzu.</p>
<p>
- Enpresak: ${data.entidadesFinanciadoras?join(", ")}<br>
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}<br>
- Aurreikuspen zk.: ${data.numPrevision}
</p>
<p>
Zure erantzunaren zain, jaso agur bero bat.<br>
Ikerketaren Emaitzak Transferitzeko Bulegoa<br>
otri@ehu.eus
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