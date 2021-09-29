package org.crue.hercules.sgi.rep.report;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestReport extends AbstractReportGenerator {

  public TestReport() {
    super();
  }

  public MasterReport getReportDefinition(String reportPath) {

    // Using the classloader, get the URL to the reportDefinition file
    final ClassLoader classloader = this.getClass().getClassLoader();
    // final URL reportDefinitionURL =
    // classloader.getResource("org/crue/hercules/sgi/rep/TestReportTypeModel.prpt");
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

  public DataFactory getDataFactory(Collection data) {
    return null;
  }

  public Map<String, Object> getReportParameters() {
    final Map<String, Object> parameters = new HashMap<String, Object>();

    parameters.put("Example", "Parameters");

    return parameters;
  }
}
