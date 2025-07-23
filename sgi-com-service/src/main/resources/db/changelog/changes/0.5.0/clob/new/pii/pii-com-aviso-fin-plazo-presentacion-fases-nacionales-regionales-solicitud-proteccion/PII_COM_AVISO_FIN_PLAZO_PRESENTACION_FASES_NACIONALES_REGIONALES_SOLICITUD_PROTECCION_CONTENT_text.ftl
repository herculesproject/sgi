<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": [{"lang": "es", "value": "PROTECCION 1"}],
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a

Le informamos de la proxima finalización del plazo de extensión o entrada en fases nacionales/regionales de la invención:

- Meses restantes: ${data.monthsBeforeFechaFinPrioridad} 
- Fecha fin del plazo: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Título de la Invención: ${sgi.getFieldValue(data.solicitudTitle)}

Reciba un cordial saludo,
Servicio de Gestión de Patentes
iproperty.otri@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher

Please be advised that the period for extension or entry into national/regional phases of the following invention is about to expire:

- Remaining months: ${data.monthsBeforeFechaFinPrioridad} 
- Expiry date: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Invention title: ${sgi.getFieldValue(data.solicitudTitle)}

Yours sincerely,
Patent Management Service
iproperty.otri@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster amaituko dela asmakizuna hedatzeko edo nazio/eskualde mailako faseetan sartzeko epea:

- Epea amaitzeko geratzen diren hilabeteak: ${data.monthsBeforeFechaFinPrioridad} 
- Epearen amaiera data: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Asmakizunaren izenburua: ${sgi.getFieldValue(data.solicitudTitle)}

Jaso agur bero bat.
Patenteak Kudeatzeko Zerbitzua
iproperty.otri@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>