<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que se ha producido el siguiente error en el proceso de baremación:

- Año: ${data.anio}

Puede consultar su resultado en el siguiente enlace, opción de menú "Informes": ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that the following error has occurred in the ranking process:

- Year:  ${data.anio}

You can consult your result in the following link, menu option "Reports": ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan:

- Urtea: ${data.anio}

Zure emaitza esteka honetan ikus dezakezu, "Txostenak" menuan: ${data.enlaceAplicacion}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>