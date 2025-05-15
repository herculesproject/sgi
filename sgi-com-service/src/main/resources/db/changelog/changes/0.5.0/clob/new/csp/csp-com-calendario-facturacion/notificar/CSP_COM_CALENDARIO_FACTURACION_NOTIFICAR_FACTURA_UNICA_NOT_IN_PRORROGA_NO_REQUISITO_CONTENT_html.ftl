<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA:
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
<p>Una vez firmado el contrato abajo referenciado, se debe emitir la corespondiente factura. Para ello necesitamos que nos confirme que los trabajos han finalizado.</p>
<p>
- Contrato con la/s empresa/s: ${data.entidadesFinanciadoras?join(", ")}<br>
- Titulo: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Inverstigador/a responsable: ${data.nombreApellidosValidador}<br>
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
</p>
<p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
<p>
En espera de su respuesta, reciba un cordial saludo,<br>
Oficina de Transferencia de Resultados de Investigación<br>
otri@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Once the contract below has been signed, the relevant invoice is to be issued. For this we need you to confirm that the work has been completed.</p>
<p>
- Contract with the company(ies): ${data.entidadesFinanciadoras?join(", ")}<br>
- Title: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Responsible researcher: ${data.nombreApellidosValidador}<br>
- Associated project code(s): ${data.codigosSge?join(", ")}
</p>
<p>In relation to the work you have performed under this contract, please send us an e-mail or completion report on the services you have provided to the company(ies) in order to get your opinion, degree of implementation and completion of the work completed.</p>
<p>
We look forward to hearing from you. Kind regards,<br>
Office for the Transfer of Research Results<br>
otri@ehu.eus
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Behean aipatutako kontratua sinatu ostean, dagokion faktura jaulki behar da. Horretarako, lanak amaituta daudela baieztatu behar diguzu.</p>
<p>
- Kontratua sinatu duten enpresak: ${data.entidadesFinanciadoras?join(", ")}<br>
- Izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Ikertzaile arduraduna: ${data.nombreApellidosValidador}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
</p>
<p>Kontratu horren baitan egin dituzun lanei dagokienez, mesedez, bidal iezaguzu enpresei emandako zerbitzuei buruzko mezu elektroniko edo amaiera txosten bat, amaitutako lanen gauzatze eta betetze maila ezagutzeko, baita haien gaineko iritzia ere.</p>
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