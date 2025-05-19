<#assign data = PRC_COM_PROCESO_BAREMACION_ERROR_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_ERROR_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que se ha producido el siguiente error en el proceso de baremación:

- Año: ${data.anio}
- Error producido: ${data.error}

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

You are informed that the following error in the grading process has occurred:

- Year: ${data.anio}
- Error occurred: ${data.error}

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan: 

- Urtea: ${data.anio}
- Akatsa: ${data.error}

Jaso agur bero bat.
Ikerketa Kudeatzeko Zuzendaritza
convocatorias.dgi@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>