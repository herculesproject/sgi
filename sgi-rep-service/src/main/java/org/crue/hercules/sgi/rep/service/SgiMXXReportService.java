package org.crue.hercules.sgi.rep.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.ElementOutput;
import org.crue.hercules.sgi.rep.dto.EtiMXXReportOutput;
import org.crue.hercules.sgi.rep.dto.PersonaDto;
import org.crue.hercules.sgi.rep.dto.SgiReport;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportMxxException;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes de ética M10, M20, M30
 */
@Service
@Slf4j
public class SgiMXXReportService {

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  private static final String COMPONENT_ID = "component_id";
  private static final String COMPONENT_TYPE = "component_type";
  private static final String BAND_TYPE = "band";
  private static final String QUERY_TYPE = "query";
  private static final String SUBREPORT_TYPE = "subreport";
  private static final String TABLE_CRUD_TYPE = "table-crud";
  private static final String DATOS_SOLICITANTE_TYPE = "datosSolicitanteType";
  private static final String SEPARATOR_KEY = "_";

  // @formatter:off
  private static final String[] COLUMNS_TABLE_MODEL = new String[] { 
    "bloque_id", "bloque_nombre", "bloque_orden", 
    "apartado_id", "apartado_nombre", "apartado_orden", 
    "apartado_padre_id", "apartado_hijo_id", "apartado_hijo_nombre", "apartado_hijo_orden",
    COMPONENT_ID, COMPONENT_TYPE, "content", "content_orden", "titulo" };
  // @formatter:on

  public SgiMXXReportService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Obtiene el report
   * 
   * @param reportPath ruta del report
   */
  private MasterReport getReportDefinition(String reportPath) {

    // Using the classloader, get the URL to the reportDefinition file
    final ClassLoader classloader = this.getClass().getClassLoader();
    final URL reportDefinitionURL = classloader.getResource(reportPath);

    try {
      // Parse the report file
      final ResourceManager resourceManager = new ResourceManager();
      resourceManager.registerDefaults();
      Resource directly = resourceManager.createDirectly(reportDefinitionURL, MasterReport.class);
      return (MasterReport) directly.getResource();
    } catch (ResourceException e) {
      log.error(e.getMessage(), e);
    }
    return null;

  }

  /**
   * Devuelve un formulario M10, M20 o M30 a partir de un json de pruebas
   * 
   * @param reportOutput   SgiReport
   * @param outputJsonPath String
   * @param titulo         Título del informe
   */
  public void getReportMXXFromJson(SgiReport reportOutput, String outputJsonPath, String titulo) {

    Map<String, DefaultTableModel> hmTableModel = createMockQueryMxxDataModel(outputJsonPath, titulo);

    getReportMXXIntern(reportOutput, hmTableModel);
  }

  /**
   * Devuelve un formulario M10, M20 o M30 a partir del microservicio de eti
   * 
   * @param reportOutput SgiReport
   * @param idMemoria    Id de la memoria
   */
  public void getReportMXX(SgiReport reportOutput, Long idMemoria) {

    EtiMXXReportOutput etiReportOutput = this.getMXX(idMemoria);

    Map<String, DefaultTableModel> hmTableModel = generateTableModelFromEtiReportOutput(etiReportOutput);

    getReportMXXIntern(reportOutput, hmTableModel);
  }

  /**
   * Devuelve un formulario M10, M20 o M30
   * 
   * @param reportOutput SgiReport
   * @param hmTableModel Map<String, DefaultTableModel>
   */
  private void getReportMXXIntern(SgiReport reportOutput, Map<String, DefaultTableModel> hmTableModel) {

    if (null != hmTableModel && !hmTableModel.isEmpty()) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

        MasterReport report = getReportDefinition(reportOutput.getPath());

        TableDataFactory dataFactory = new TableDataFactory();

        for (String queryKey : hmTableModel.keySet()) {
          TableModel tableModel = hmTableModel.get(queryKey);

          String[] queryKeys = queryKey.split(SEPARATOR_KEY);
          String actionType = queryKeys[1];
          String elementType = queryKeys[2];

          if (elementType.equals(DATOS_SOLICITANTE_TYPE)) {
            for (SubReport subReport : report.getItemBand().getSubReports()) {
              if (subReport.getName().contains(DATOS_SOLICITANTE_TYPE)) {
                subReport.setQuery(queryKey);
                TableDataFactory dataFactorySubReport = new TableDataFactory();
                dataFactorySubReport.addTable(queryKey, tableModel);
                subReport.setDataFactory(dataFactorySubReport);
              }
            }

          } else if (actionType.equals(SUBREPORT_TYPE)) {
            String elementKey = queryKeys[3];
            if (elementType.equals(TABLE_CRUD_TYPE)) {

              SubReport subReportTableCrud = getSubreportTableCrud(tableModel);

              FormulaExpression function = new FormulaExpression();
              function.setName("expression_" + elementKey);
              function.setFormula("=IF(AND([" + COMPONENT_ID + "]=\"" + elementKey
                  + "\";[component_type]=\"table-crud\");\"true\";\"false\")");

              subReportTableCrud.setStyleExpression(ElementStyleKeys.VISIBLE, function);

              TableDataFactory dataFactorySubReportTableCrud = new TableDataFactory();
              dataFactorySubReportTableCrud.addTable(queryKey, tableModel);
              subReportTableCrud.setDataFactory(dataFactorySubReportTableCrud);

              subReportTableCrud.setQuery(queryKey);
              report.getItemBand().addSubReport(subReportTableCrud);
            }
          } else {
            dataFactory.addTable(queryKey, tableModel);
          }
        }
        report.setDataFactory(dataFactory);

        PdfReportUtil.createPDF(report, baos);

        reportOutput.setContent(baos.toByteArray());

      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * A partir de u json (EtiReporOutput) crea un mock de DefaultTableModel para la
   * carga de d tos en el report y un xls para la carga directa de datos de prueba
   * desde el report designer
   * 
   * @param outputJsonPath String
   * @param titulo         String
   * @return Map<String, DefaultTableModel>
   */
  private Map<String, DefaultTableModel> createMockQueryMxxDataModel(String outputJsonPath, String titulo) {
    Map<String, DefaultTableModel> hmTableModel = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        String outputJson = scanner.next();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        EtiMXXReportOutput etiReportOutput = objectMapper.readValue(outputJson, EtiMXXReportOutput.class);
        etiReportOutput.setTitulo(titulo);

        hmTableModel = generateTableModelFromEtiReportOutput(etiReportOutput);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return hmTableModel;
  }

  /**
   * Genera el tableModel y/o un xls a partir de un EtiReportOutput
   * 
   * @param etiReportOutput EtiMXXReportOutput
   * @return Map<String, DefaultTableModel>
   */
  private Map<String, DefaultTableModel> generateTableModelFromEtiReportOutput(EtiMXXReportOutput etiReportOutput) {
    Map<String, DefaultTableModel> hmTableModel = new HashMap<>();

    final DefaultTableModel tableModelGeneral = new DefaultTableModel();

    tableModelGeneral.setColumnIdentifiers(COLUMNS_TABLE_MODEL);

    for (BloqueOutput bloque : etiReportOutput.getBloques()) {
      for (ApartadoOutput apartado : bloque.getApartados()) {
        for (int i = 0; i < apartado.getElementos().size(); i++) {
          ElementOutput elemento = apartado.getElementos().get(i);

          parseElementTypeFromTableModel(hmTableModel, elemento);

          String titulo = i == 0 ? etiReportOutput.getTitulo() : "";

         // @formatter:off
          Object[] objectRow = new Object[] { 
              bloque.getId(), bloque.getNombre(), bloque.getOrden(), 
              apartado.getId(), apartado.getTitulo(), apartado.getOrden(), 
              "", "", "", "",
              elemento.getNombre(), elemento.getTipo(), elemento.getContent(), i, titulo
          };
          // @formatter:on  
          tableModelGeneral.addRow(objectRow);
        }

        if (null != apartado.getApartadosHijos()) {
          for (ApartadoOutput apartadoHijo : apartado.getApartadosHijos()) {
            for (int j = 0; j < apartadoHijo.getElementos().size(); j++) {
              ElementOutput elementoHijo = apartadoHijo.getElementos().get(j);

              parseElementTypeFromTableModel(hmTableModel, elementoHijo);

              // @formatter:off
              Object[] objectHijoRow = new Object[] { bloque.getId(), bloque.getNombre(), bloque.getOrden(), 
                  apartado.getId(), apartado.getTitulo(), apartado.getOrden(), 
                  apartadoHijo.getId(), apartadoHijo.getId(), apartadoHijo.getTitulo(), apartadoHijo.getOrden(), 
                  elementoHijo.getNombre(), elementoHijo.getTipo(), elementoHijo.getContent(), j, ""
              };
              // @formatter:on   
              tableModelGeneral.addRow(objectHijoRow);
            }
          }
        }
      }
    }

    hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + "general" + SEPARATOR_KEY + "mXX", tableModelGeneral);

    return hmTableModel;
  }

  /**
   * Devuelve un informe pdf del formulario M10, M20 o M30
   *
   * @param idMemoria Id de la memoria
   * @return EtiMXXReportOutput Datos a presentar en el informe
   */
  private EtiMXXReportOutput getMXX(Long idMemoria) {
    log.debug("getMXX(idMemoria)- start");
    Assert.notNull(idMemoria, "idMemoria no puede ser nulo");

    EtiMXXReportOutput reportOutput = null;
    try {

      final ResponseEntity<EtiMXXReportOutput> response = restTemplate.exchange(
          restApiProperties.getEtiUrl() + "/reports/mxx/" + idMemoria, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), EtiMXXReportOutput.class);

      reportOutput = (EtiMXXReportOutput) response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (null == reportOutput) {
      throw new GetDataReportMxxException();
    }

    log.debug("getMXX(idMemoria) - end");
    return reportOutput;
  }

  /**
   * Devuelve datos de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return PersonaDto
   */
  private PersonaDto getPersonaById(String personaRef) {
    log.debug("getPersonaById(personaRef)- start");
    Assert.notNull(personaRef, "personaRef no puede ser nulo");
    PersonaDto persona = null;
    try {
      List<PersonaDto> personas = new ArrayList<>();

      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append("/api/sgp/personas");

      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url.toString()).queryParam("q",
          "id==\"" + personaRef + "\"");

      final ResponseEntity<List<PersonaDto>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<PersonaDto>>() {
          });

      personas = (List<PersonaDto>) response.getBody();

      if (null != personas && !personas.isEmpty()) {
        persona = personas.get(0);
        persona.setDatosContacto(getDatosContactoPersonaById(personaRef));

        // TODO coger datos de vinculacion cuando arreglen desde MURCIA la llamada a ESB
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (null == persona) {
      throw new GetDataReportMxxException();
    }

    log.debug("getPersonaById(personaRef) - end");
    return persona;
  }

  /**
   * Devuelve datos de contacto de una persona a través de una consulta al ESB
   *
   * @param personaRef String
   * @return PersonaDto
   */
  private DatosContactoDto getDatosContactoPersonaById(String personaRef) {
    log.debug("getDatosContactoPersonaById(personaRef)- start");
    Assert.notNull(personaRef, "personaRef no puede ser nulo");
    DatosContactoDto datosContacto = null;
    try {

      StringBuilder url = new StringBuilder();
      url.append(restApiProperties.getSgpUrl());
      url.append("/api/sgp/datos-contacto/persona/");
      url.append(personaRef);

      final ResponseEntity<DatosContactoDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), DatosContactoDto.class);

      datosContacto = (DatosContactoDto) response.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (null == datosContacto) {
      throw new GetDataReportMxxException();
    }

    log.debug("getDatosContactoPersonaById(personaRef) - end");
    return datosContacto;
  }

  /**
   * Genera un tableModel en función del tipo de elemento que le pasemos
   * 
   * @param hmTableModel Map<String, DefaultTableModel>
   * @param elemento     ElementOutput
   */
  private void parseElementTypeFromTableModel(Map<String, DefaultTableModel> hmTableModel, ElementOutput elemento) {
    try {
      if (elemento.getTipo().equals(DATOS_SOLICITANTE_TYPE)) {

        DefaultTableModel tableModelDatosSolicitante = new DefaultTableModel();
        String personaRef = elemento.getContent();
        PersonaDto persona = getPersonaById(personaRef);
        String telefono = "";
        if (null != persona.getDatosContacto() && null != persona.getDatosContacto().getEmails()
            && !persona.getDatosContacto().getEmails().isEmpty()) {
          telefono = persona.getDatosContacto().getEmails().get(0);
        }
        String email = "";
        if (null != persona.getDatosContacto() && null != persona.getDatosContacto().getTelefonos()
            && !persona.getDatosContacto().getTelefonos().isEmpty()) {
          email = persona.getDatosContacto().getTelefonos().get(0);
        }

        Vector<Object> columnsDataSolicitante = new Vector<>();
        Vector<Vector<Object>> rowsDataSolicitante = new Vector<>();
        Vector<Object> elementsRow = new Vector<>();

        columnsDataSolicitante.add("nombre");
        elementsRow.add(persona.getNombre());
        columnsDataSolicitante.add("nif");
        elementsRow.add(persona.getNumeroDocumento());
        columnsDataSolicitante.add("telefono");
        elementsRow.add(telefono);
        columnsDataSolicitante.add("email");
        elementsRow.add(email);
        // TODO rellenar estos campos cuando tengamos datos de vinculacion
        columnsDataSolicitante.add("departamento");
        elementsRow.add("");
        columnsDataSolicitante.add("area");
        elementsRow.add("");

        rowsDataSolicitante.add(elementsRow);

        tableModelDatosSolicitante.setDataVector(rowsDataSolicitante, columnsDataSolicitante);
        hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + BAND_TYPE + SEPARATOR_KEY + DATOS_SOLICITANTE_TYPE,
            tableModelDatosSolicitante);
      } else if (elemento.getTipo().equals(TABLE_CRUD_TYPE)) {

        elemento.setNombre(elemento.getNombre() + "-" + RandomStringUtils.randomAlphanumeric(3));

        DefaultTableModel tableCrudTableModel = parseTableCrudTableModel(elemento);
        String keyTableModel = QUERY_TYPE + SEPARATOR_KEY + SUBREPORT_TYPE + SEPARATOR_KEY + TABLE_CRUD_TYPE
            + SEPARATOR_KEY + elemento.getNombre();
        hmTableModel.put(keyTableModel, tableCrudTableModel);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /**
   * Obtiene el DefaultTableModel de un campo de tipo table-crud
   * 
   * @param elemento ElementOutput
   * @return DefaultTableModel
   */
  private DefaultTableModel parseTableCrudTableModel(ElementOutput elemento) {
    DefaultTableModel tableModel = new DefaultTableModel();
    try {
      ObjectMapper mapper = new ObjectMapper();
      List<List<ElementOutput>> elementsTableCrud = mapper.readValue(elemento.getContent(),
          new TypeReference<List<List<ElementOutput>>>() {
          });

      Vector<Object> columns = new Vector<>();
      Vector<Vector<Object>> elements = new Vector<>();
      for (int i = 0; i < elementsTableCrud.size(); i++) {
        List<ElementOutput> rowElementTableCrud = elementsTableCrud.get(i);

        Vector<Object> elementsRow = new Vector<>();
        for (ElementOutput elementTableCrud : rowElementTableCrud) {
          if (i == 0) {
            String columnName = null != elementTableCrud.getNombre() ? elementTableCrud.getNombre() : "";
            columns.add(columnName);
          }
          String content = null != elementTableCrud.getContent() ? elementTableCrud.getContent() : "";
          elementsRow.add(content);
        }
        elements.add(elementsRow);
      }

      tableModel.setDataVector(elements, columns);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return tableModel;
  }

  /**
   * Obtiene un subreport de tipo table-crud a partir de un tableModel
   * 
   * @param tableModel TableModel
   * @return SubReport
   */
  private SubReport getSubreportTableCrud(TableModel tableModel) {
    final SubReport subreport = new SubReport();
    float posX = 0f;
    float posY = 0f;
    float minimumWidth = 450f / tableModel.getColumnCount();
    float minimumHeight = 20f;

    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      LabelElementFactory labelFactory = new LabelElementFactory();
      String fieldName = tableModel.getColumnName(columnIndex);
      labelFactory.setText(fieldName);
      labelFactory.setX(posX);
      labelFactory.setY(posY + 20f);
      labelFactory.setMinimumWidth(minimumWidth);
      labelFactory.setMinimumHeight(minimumHeight + 10f);
      labelFactory.setBorderTopStyle(BorderStyle.SOLID);
      labelFactory.setBorderWidth(0.5f);
      labelFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
      labelFactory.setPaddingLeft(4f);
      labelFactory.setPaddingRight(4f);
      labelFactory.setPaddingTop(4f);
      labelFactory.setPaddingBottom(4f);
      labelFactory.setWrapText(Boolean.TRUE);

      subreport.getReportHeader().addElement(labelFactory.createElement());

      TextFieldElementFactory textFactoryProduct = new TextFieldElementFactory();
      textFactoryProduct.setFieldname(fieldName);
      textFactoryProduct.setX(posX);
      textFactoryProduct.setY(posY);
      textFactoryProduct.setMinimumWidth(minimumWidth);
      textFactoryProduct.setMinimumHeight(minimumHeight);
      textFactoryProduct.setBorderTopStyle(BorderStyle.SOLID);
      textFactoryProduct.setBorderWidth(0.5f);
      textFactoryProduct.setVerticalAlignment(ElementAlignment.MIDDLE);
      textFactoryProduct.setPaddingLeft(4f);
      textFactoryProduct.setPaddingRight(4f);
      textFactoryProduct.setPaddingTop(4f);
      textFactoryProduct.setPaddingBottom(4f);
      textFactoryProduct.setDynamicHeight(Boolean.TRUE);
      textFactoryProduct.setWrapText(Boolean.TRUE);
      subreport.getItemBand().addElement(textFactoryProduct.createElement());

      posX += minimumWidth;
    }

    return subreport;
  }

}
