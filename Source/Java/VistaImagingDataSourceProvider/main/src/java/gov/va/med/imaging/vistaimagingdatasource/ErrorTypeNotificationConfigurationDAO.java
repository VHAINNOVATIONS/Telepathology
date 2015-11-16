/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */

package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import gov.va.med.imaging.xstream.VistaDateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ErrorTypeNotificationConfigurationDAO extends
		EntityDAO<ErrorTypeNotificationConfiguration> {
	class ErrorTypeNotificationConfigurationContainer {
		private List<ErrorTypeNotificationConfiguration> configs;

		public List<ErrorTypeNotificationConfiguration> getConfigs() {
			return configs;
		}

		public void setConfigs(List<ErrorTypeNotificationConfiguration> configs) {
			this.configs = configs;
		}
	}

	private final static String DELIMITER = "`";
	private final static String LINEDELIMITER = "\r\n";
	private final static String SUCCESS = "0";
	private final static String FAILURE = "-1";
	private final static String RPC_GET_ALL = "MAGV ENS GET ALL VENS";
	private final static String RPC_UPDATE = "MAGV ENS UPDATE VENS CONFIG";
	private final static String VC_PK = "PK";
	private final static String VC_LAST_EMAIL_SENT = "LAST EMAIL SENT DATETIME";
	private final static String VC_LAST_EXECUTION = "LAST EXECUTION DATETIME";

	//
	// Constructor
	//
	public ErrorTypeNotificationConfigurationDAO(
			VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	@Override
	public VistaQuery generateFindAllQuery() {
		VistaQuery vm = new VistaQuery(RPC_GET_ALL);
		return vm;
	}

	public ErrorTypeNotificationConfiguration getByErrorType(String errorType)
			throws MethodException, ConnectionException {
		List<ErrorTypeNotificationConfiguration> configs = this.findAll();
		for (ErrorTypeNotificationConfiguration config : configs) {
			if (config.getErrorType().equals(errorType)) {
				return config;
			}
		}
		throw new MethodException("ErrorTypeNotificationConfiguration "
				+ errorType + " not found");
	}

	protected XStream getConfiguredXStream() {

		class ErrorTypeNotificationConfigurationConverter implements Converter {

			@Override
			public boolean canConvert(Class clazz) {
				return clazz.equals(ErrorTypeNotificationConfiguration.class);
			}

			@Override
			public void marshal(Object arg0, HierarchicalStreamWriter arg1,
					MarshallingContext arg2) {

			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader,
					UnmarshallingContext context) {
				ErrorTypeNotificationConfiguration config = new ErrorTypeNotificationConfiguration();
				config.setId(Integer.parseInt(reader.getAttribute("PK")));
				config.setPlaceId(Integer.parseInt(reader
						.getAttribute("PLACEFK")));
				config.setErrorType(reader.getAttribute("ERRORTYPE"));
				config
						.setLastEmailSentDateTime(VistaDateConverter
								.parseDate(reader
										.getAttribute("LASTEMAILSENTDATETIME")));
				config.setExecutionDelayInSeconds(Integer.parseInt(reader
						.getAttribute("EXECUTIONDELAYINSECONDS")));
				config.setEvaluationPeriodInSeconds(Integer.parseInt(reader
						.getAttribute("EVALUATIONPERIODINSECONDS")));
				config.setMaxErrors(Integer.parseInt(reader
						.getAttribute("MAXERRORS")));
				config
						.setLastExecutionDateTime(VistaDateConverter
								.parseDate(reader
										.getAttribute("LASTEXECUTIONDATETIME")));
				config.setDailyExecutionTime(VistaDateConverter
						.parseDate(reader.getAttribute("DAILYEXECUTIONTIME")));
				reader.moveDown();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					config.getEmailAddresses()
							.add(reader.getAttribute("EMAIL"));
					reader.moveUp();
				}
				reader.moveUp();
				return config;
			}

		}

		XStream xstream = new XStream();
		xstream.alias("VEINSCONFIGURATIONS",
				ErrorTypeNotificationConfigurationContainer.class);
		xstream.addImplicitCollection(
				ErrorTypeNotificationConfigurationContainer.class, "configs");
		xstream.alias("VEINSCONFIGURATION",
				ErrorTypeNotificationConfiguration.class);
		xstream
				.registerConverter(new ErrorTypeNotificationConfigurationConverter());
		return xstream;
	}

	@Override
	public List<ErrorTypeNotificationConfiguration> translateFindAll(
			String returnValue) throws MethodException, RetrievalException {
		int firstLineBreakIndex = returnValue.indexOf("\r\n");
		String firstLine;
		if (firstLineBreakIndex > 0)
			firstLine = returnValue.substring(0, firstLineBreakIndex);
		else
			firstLine = returnValue;
		String[] lineParts = firstLine.split(DELIMITER);
		if (Integer.parseInt(lineParts[0]) == -1)
			throw new MethodException(returnValue);
		String messageContent = returnValue.substring(firstLineBreakIndex + 2);
		ErrorTypeNotificationConfigurationContainer container;
		try {
			XStream deserializer = getConfiguredXStream();
			container = (ErrorTypeNotificationConfigurationContainer) deserializer.fromXML(messageContent);
		} catch (XStreamException xsX) {
			throw new MethodException(xsX.getMessage(), xsX);
		}
		return container.getConfigs();
	}

	@Override
	public ErrorTypeNotificationConfiguration update(
			ErrorTypeNotificationConfiguration t) throws MethodException,
			ConnectionException {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd.hhmmss");
		VistaQuery vm = new VistaQuery(RPC_UPDATE);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(VC_PK, Integer.toString(t.getId()));
		hm.put(VC_LAST_EMAIL_SENT, formatter.format(t
				.getLastEmailSentDateTime()));
		hm.put(VC_LAST_EXECUTION, formatter.format(t
				.getLastExecutionDateTime()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		if (result.startsWith(FAILURE)) {
			throw new MethodException(result);
		}
		return t;
	}
}
