package com.ghx.api.operations.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.config.ReportConfigProperties;
import com.ghx.api.operations.dto.AuditExportDTO;
import com.ghx.api.operations.dto.ExportDTO;
import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.dto.UserDetailsInfo;
import com.ghx.api.operations.enums.ExportType;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import static com.ghx.api.operations.util.ConstantUtils.JRXML_LOCATION;

/**
 * @author Anithaa K S
 * 
 *         This class is used to generate and download report(csv,pdf,xls)
 *
 */

@Component
public class ReportUtils {

	/** The Constant LOGGER. */
	private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(ReportUtils.class);
	
	/** Report Config Properties */
	@Autowired
	private ReportConfigProperties reportConfigProperties;
	
	/**
	 * Generate report.
	 *
	 * @param <T>             the generic type
	 * @param exportType      the export type
	 * @param objectList      the object List
	 * @param response        the response
	 * @param resourceLoader  the resource loader
	 * @param filename        the filename
	 * @param exportTemplateFile the export Template File Name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void generateReport(String exportType, List<T> objectList, HttpServletResponse response,
			ResourceLoader resourceLoader, String filename, Map<String, Object> params, String exportTemplateFile) throws IOException {
		Properties jasperProps = fetchJasperProperties(resourceLoader);
		JasperPrint jasperPrint = null;
		String fileNameWithType = new StringBuilder(filename).append(".")
				.append(exportType.toLowerCase(Locale.getDefault())).toString();

		try {
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(objectList);
			Exporter exporter = null;
			JasperReport jasperReport = null;
			if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
				jasperReport = JasperCompileManager.compileReport(
						getInputStreamFromResource((jasperProps.getProperty(JRXML_LOCATION).concat(exportTemplateFile).concat("_PDF.jrxml")), resourceLoader));
				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

				exporter = new JRPdfExporter();
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
			} else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {

				jasperReport = JasperCompileManager.compileReport(
						getInputStreamFromResource((jasperProps.getProperty(JRXML_LOCATION).concat(exportTemplateFile).concat("_XLS.jrxml")), resourceLoader));

				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

				exporter = new JRCsvExporter();
				exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
			} else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {

				jasperReport = JasperCompileManager.compileReport(
						getInputStreamFromResource((jasperProps.getProperty(JRXML_LOCATION).concat(exportTemplateFile).concat("_XLS.jrxml")), resourceLoader));
				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

				exporter = new JRXlsExporter();
				SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
				xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
				xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
				exporter.setConfiguration(xlsConf);
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
			} else {
				throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
			}

			response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
			response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
			response.setContentType(fetchContentType(exportType));

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.exportReport();
		} catch (JRException | IOException e) {
			LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
			throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
		}
	}

	/**
	 * Gets the input stream from resource.
	 *
	 * @param filePath       the file path
	 * @param resourceLoader the resource loader
	 * @return the input stream from resource
	 */
	private static InputStream getInputStreamFromResource(String filePath, ResourceLoader resourceLoader) {
		try {
			Resource resource = resourceLoader.getResource(filePath);
			return resource.getInputStream();
		} catch (IOException ex) {
			LOGGER.error(CustomMessageSource.getMessage("report.generate.source.path.not.present", filePath, ex));
			throw new BusinessException(
					CustomMessageSource.getMessage("report.generate.source.path.not.present", filePath, ex));
		}
	}

	/**
	 * Fetch content type.
	 *
	 * @param exportType the export type
	 * @return String
	 */
	public static String fetchContentType(String exportType) {
		String contentType = "";
		if (ConstantUtils.REPORT_TYPE_PDF.equalsIgnoreCase(exportType)) {
			contentType = ConstantUtils.CONTENT_TYPE_PDF;
		} else if (ConstantUtils.REPORT_TYPE_XLS.equalsIgnoreCase(exportType)) {
			contentType = ConstantUtils.CONTENT_TYPE_XLS;
		} else if (ConstantUtils.REPORT_TYPE_CSV.equalsIgnoreCase(exportType)) {
			contentType = ConstantUtils.CONTENT_TYPE_CSV;
		}
		return contentType;
	}

	/**
	 * Fetch jasper properties.
	 *
	 * @param resourceLoader the resource loader
	 * @return the properties
	 * @throws IOException
	 */
	private static Properties fetchJasperProperties(ResourceLoader resourceLoader) throws IOException {
		Properties properties = new Properties();
		Resource resource = resourceLoader.getResource(ConstantUtils.JASPER_PROPERTIES);
		try (InputStream in = resource.getInputStream()) {
			properties.load(in);
		}
		return properties;
	}
	

    /**
     * Generate Report for merge supplier and move user request
     * @param exportType
     * @param listDetail
     * @param response
     * @param resourceLoader
     * @param filename
     * @param params
     * @param value
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void generateMergeAndMoveReport(String exportType, List<?> listDetail, HttpServletResponse response, ResourceLoader resourceLoader,
            String filename, Map<String, Object> params) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint = null;
        String fileNameWithType = new StringBuilder(filename).append(".").append(exportType.toLowerCase(Locale.getDefault())).toString();
        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listDetail);
            Exporter exporter = null;
            JasperReport jasperReport = null;
            if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
                if (StringUtils.equalsIgnoreCase(filename, ConstantUtils.SUPPLIER_MERGEREQUEST_FILE)) {
                    jasperReport = JasperCompileManager.compileReport(getInputStreamFromResource(
                            jasperProps.getProperty(ConstantUtils.JASPER_XLS_SUPPLIER_MERGE_REQUEST_JRML), resourceLoader));
                } else if (StringUtils.equalsIgnoreCase(filename, ConstantUtils.MOVEUSER_REQUEST_FILE)) {
                    jasperReport = JasperCompileManager.compileReport(
                            getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_MOVE_USER_REQUEST_JRML), resourceLoader));
                } else if (StringUtils.equalsIgnoreCase(filename, ConstantUtils.TIER_MANAGEMENT_REQUEST_EXPORT_FILE)) {
                    jasperReport = JasperCompileManager.compileReport(
                            getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_TIER_CHANGE_REQUEST_JRXML), resourceLoader));
                }
                jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
                exporter = new JRXlsExporter();
                SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                exporter.setConfiguration(xlsConf);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }
            response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
            response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
            response.setContentType(fetchContentType(exportType));
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (JRException | IOException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }
    
    /**
     * Export the list.
     *
     * @param <T>
     *            the generic type
     * @param exportType
     *            the export type
     * @param transactionList
     *            the transaction list
     * @param response
     *            the response
     * @param resourceLoader
     *            the resource loader
     * @param filename
     *            the filename
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> void export(String exportType, List<T> transactionList, HttpServletResponse response, ResourceLoader resourceLoader,
            String filename, Map<String, Object> params) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint = null;
        String fileNameWithType = new StringBuilder(filename).append(".").append(exportType.toLowerCase(Locale.getDefault())).toString();

        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactionList);

            Exporter exporter = null;
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    getInputStreamFromResource(jasperProps.getProperty("jrxml." + exportType + "." + filename.toLowerCase(Locale.ENGLISH)+ ".location"), resourceLoader));
            jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
                exporter = new JRXlsExporter();
                SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                exporter.setConfiguration(xlsConf);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }

            response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
            response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
            response.setContentType(fetchContentType(exportType));

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (JRException | IOException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }
    
    /**
     * Generate Report for merge supplier and move user request
     * @param exportType
     * @param listDetail
     * @param response
     * @param resourceLoader
     * @param filename
     * @param params
     * @param value
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void generateTierChangeRequestsReport(String exportType, List<?> listDetail, HttpServletResponse response,
            ResourceLoader resourceLoader, String filename, Map<String, Object> params) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint = null;
        String fileNameWithType = StringUtils.join(filename, ".", exportType.toLowerCase(Locale.getDefault()));
        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listDetail);
            Exporter exporter = null;
            JasperReport jasperReport = null;
            if (reportConfigProperties.getTierChangeRequestexportTypes().contains(exportType.toUpperCase(Locale.getDefault()))) {
                jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_TIER_CHANGE_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
                exporter = new JRXlsExporter();
                if (StringUtils.equalsIgnoreCase(exportType, ExportType.PDF.getType())) {
                    exporter = new JRPdfExporter();
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                } else if (StringUtils.equalsIgnoreCase(exportType, ExportType.CSV.getType())) {
                    exporter = new JRCsvExporter();
                    exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
                }
                else {
                    SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                    xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                    xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                    exporter.setConfiguration(xlsConf);
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                }
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }
            response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
            response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
            response.setContentType(fetchContentType(exportType));
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (JRException | IOException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }
    
    /**
     * Export Move User requests, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param response
     * @param moveRequestList
     * @param resourceLoader
     * @param filename
     * @param params
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void exportMoveUserRequests(String exportType, HttpServletResponse response, List<MoveUserRequestDTO> moveRequests,
            ResourceLoader resourceLoader, String filename) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint = null;
        String fileNameWithType = new StringBuilder(filename).append(".").append(exportType.toLowerCase(Locale.getDefault())).toString();
        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(moveRequests);
            Exporter exporter = null;
            if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_PDF_MOVE_USER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_MOVE_USER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_MOVE_USER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRXlsExporter();
                SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                exporter.setConfiguration(xlsConf);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }
            response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
            response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
            response.setContentType(fetchContentType(exportType));
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (JRException | IOException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }
    
    /**
     * Export Merge Supplier requests, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param response
     * @param mergeSupplierRequestList
     * @param resourceLoader
     * @param filename
     * @param params
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void exportMergeSupplierRequests(String exportType, HttpServletResponse response,
            List<MergeSupplierRequestDTO> mergeSupplierRequestList, ResourceLoader resourceLoader, String filename) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint = null;
        String fileNameWithType = new StringBuilder(filename).append(".").append(exportType.toLowerCase(Locale.getDefault())).toString();
        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mergeSupplierRequestList);
            Exporter exporter = null;
            if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_PDF_MERGE_SUPPLIER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_MERGE_SUPPLIER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_MERGE_SUPPLIER_REQUEST_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRXlsExporter();
                SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                exporter.setConfiguration(xlsConf);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }
            response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNameWithType + "\"");
            response.setHeader(ConstantUtils.CONTENT_TRANSFER_ENCODING, ConstantUtils.BASE64);
            response.setContentType(fetchContentType(exportType));
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (JRException | IOException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }
    
    /**
     * Export Audit Trial Reports, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param response
     * @param auditTrailDTOs
     * @param resourceLoader
     * @param filename
     * @return 
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static AuditExportDTO exportAuditTrailsReports(String exportType,
    		List<Map<String, Object>> auditRecords, ResourceLoader resourceLoader, String filename) throws IOException {
        Properties jasperProps = fetchJasperProperties(resourceLoader);
        JasperPrint jasperPrint;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String fileNameWithType = new StringBuilder(filename).append('.').append(exportType.toLowerCase(Locale.getDefault())).toString();
        try {
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(auditRecords);
            Exporter exporter;
            if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_PDF_EXPORT_AUDIT_TRIALS_REPORTS_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRPdfExporter();
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_EXPORT_AUDIT_TRIALS_REPORTS_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRCsvExporter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            } else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
                JasperReport jasperReport = JasperCompileManager.compileReport(
                        getInputStreamFromResource(jasperProps.getProperty(ConstantUtils.JASPER_XLS_EXPORT_AUDIT_TRIALS_REPORTS_JRXML), resourceLoader));
                jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
                exporter = new JRXlsExporter();
                SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
                xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
                exporter.setConfiguration(xlsConf);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            } else {
                throw new BusinessException(CustomMessageSource.getMessage("report.unsupported.type", exportType));
            }
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
            byte[] output = outputStream.toByteArray();
            AuditExportDTO auditExportDTO = AuditExportDTO.builder().contentDisposition(fileNameWithType)
            		.contentType(fetchContentType(exportType)).exportData(output).build();
            return auditExportDTO;
        } catch (JRException e) {
            LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
            throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
        }
    }

	/**
     * Export userDeleteRequest userDetails, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param response
     * @param auditTrailDTOs
     * @param resourceLoader
     * @param filename
     * @return 
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static ExportDTO generateUserDeleteRequestReport(String exportType, List<UserDetailsInfo> userDetails,
			HttpServletResponse response, ResourceLoader resourceLoader, String filename) throws IOException {
		Properties jasperProps = fetchJasperProperties(resourceLoader);
		JasperPrint jasperPrint;
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String fileNameWithType = new StringBuilder(filename).append(".")
				.append(exportType.toLowerCase(Locale.getDefault())).toString();
		try {
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userDetails);
			Exporter exporter = null;
			JasperReport jasperReport = null;
			if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_PDF)) {
				jasperReport = JasperCompileManager.compileReport(getInputStreamFromResource(
						jasperProps.getProperty(ConstantUtils.JASPER_PDF_USERDELETEREQUEST_JRXML), resourceLoader));

				jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

				exporter = new JRPdfExporter();
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			} else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_CSV)) {
				jasperReport = JasperCompileManager.compileReport(getInputStreamFromResource(
						jasperProps.getProperty(ConstantUtils.JASPER_CSV_USERDELETEREQUEST_JRXML), resourceLoader));
				jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
				exporter = new JRCsvExporter();
				exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
			} else if (exportType.equalsIgnoreCase(ConstantUtils.REPORT_TYPE_XLS)) {
				jasperReport = JasperCompileManager.compileReport(getInputStreamFromResource(
						jasperProps.getProperty(ConstantUtils.JASPER_XLS_USERDELETEREQUEST_JRXML), resourceLoader));
				jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
				exporter = new JRXlsExporter();
				SimpleXlsReportConfiguration xlsConf = new SimpleXlsReportConfiguration();
				xlsConf.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
				xlsConf.setFontSizeFixEnabled(Boolean.TRUE);
				exporter.setConfiguration(xlsConf);
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			} else {
				throw new BusinessException(
						CustomMessageSource.getMessage(ErrorConstants.UNSUPPORTED_REPORT_TYPE, exportType));
			}
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.exportReport();
			byte[] output = outputStream.toByteArray();
			ExportDTO exportDTO = ExportDTO.builder().contentDisposition(fileNameWithType)
					.contentType(fetchContentType(exportType)).exportData(output).build();
			return exportDTO;
		} catch (JRException e) {
			LOGGER.error(CustomMessageSource.getMessage("report.generate.failed.with.exception", fileNameWithType, e));
			throw new BusinessException(CustomMessageSource.getMessage("report.generate.failed", fileNameWithType, e));
		}
	}

}
