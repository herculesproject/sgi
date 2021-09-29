package org.crue.hercules.sgi.rep.report;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

/**
 
 */
public abstract class AbstractReportGenerator {

  /**
   * Performs the basic initialization required to generate a report
   */
  public AbstractReportGenerator() {
    // Initialize the reporting engine
    ClassicEngineBoot.getInstance().start();
  }

  public abstract MasterReport getReportDefinition(String reportPath);

  public abstract DataFactory getDataFactory(Collection data);

  public abstract Map<String, Object> getReportParameters();

  public byte[] generateReport(final OutputReportType outputType, Collection data, String reportPath)
      throws IllegalArgumentException, ReportProcessingException {

    // Get the report and data factory
    final MasterReport report = getReportDefinition(reportPath);
    if (report == null) {
      // TODO
      throw new IllegalArgumentException();
    }
    final DataFactory dataFactory = getDataFactory(data);

    // Set the data factory for the report
    if (dataFactory != null) {
      report.setDataFactory(dataFactory);
    }

    // Add any parameters to the report
    final Map<String, Object> reportParameters = getReportParameters();
    if (null != reportParameters) {
      for (String key : reportParameters.keySet()) {
        report.getParameterValues().put(key, reportParameters.get(key));
      }

    }

    // TODO
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStream outputStream = new BufferedOutputStream(baos);

    // Prepare to generate the report
    AbstractReportProcessor reportProcessor = null;
    try {
      // Greate the report processor for the specified output type
      switch (outputType) {
        case PDF: {
          final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), outputStream,
              report.getResourceManager());
          reportProcessor = new PageableReportProcessor(report, outputProcessor);
          break;
        }

        case EXCEL: {
          final FlowExcelOutputProcessor target = new FlowExcelOutputProcessor(report.getConfiguration(), outputStream,
              report.getResourceManager());
          reportProcessor = new FlowReportProcessor(report, target);
          break;
        }

        case HTML: {
          final StreamRepository targetRepository = new StreamRepository(outputStream);
          final ContentLocation targetRoot = targetRepository.getRoot();
          final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor(report.getConfiguration());
          final HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
          printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, "index", "html"));
          printer.setDataWriter(null, null);
          printer.setUrlRewriter(new FileSystemURLRewriter());
          outputProcessor.setPrinter(printer);
          reportProcessor = new StreamReportProcessor(report, outputProcessor);
          break;
        }
      }

      // Generate the report
      reportProcessor.processReport();

      // return byte
      return baos.toByteArray();
    } finally {
      if (reportProcessor != null) {
        reportProcessor.close();
      }
    }
  }
}