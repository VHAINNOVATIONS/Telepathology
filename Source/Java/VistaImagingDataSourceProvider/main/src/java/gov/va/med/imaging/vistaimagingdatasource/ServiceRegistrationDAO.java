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
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.ServiceRegistry;
import gov.va.med.imaging.exchange.business.ServiceStatus;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class ServiceRegistrationDAO extends EntityDAO<ServiceRegistration> {
	private final static String DELIMITER = "`";
	private final static String LINEDELIMITER = "\r\n";
	private final static String SUCCESS = "0";
	private final static String FAILURE = "1";

	private final static String RPC_GET_SERVICE_REG = "MAGV ENS GET SERVICE REG";
	private final static String RPC_GET_SERVICES = "MAGV ENS GET SERVICES";
	private final static String RPC_CREATE_SERVICE_REG = "MAGV ENS CREATE SERVICE REG";
	private final static String RPC_UPDATE_SERVICE_REG = "MAGV ENS UPDATE SERVICE REG";
	private final static String RPC_DELETE_SERVICE_REG = "MAGV ENS DELETE SERVICE REG";

	private final static String SR_PK = "PK";
	private final static String SR_SERVICE_ID = "SERVICE ID";
	private final static String SR_SERVICE_VERSION = "SERVICE VERSION";
	private final static String SR_SERVER_NAME = "SERVER NAME";
	private final static String SR_IP_ADDRESS = "IP ADDRESS";
	private final static String SR_PLACE_FK = "PLACE FK";
	private final static String SR_STATUS = "STATUS";
	private final static String SR_URL = "URL";
	private final static String SR_SERVICE_DATA = "SERVICE DATA";
	private final static String SR_LAST_STATUS_RECEIVED_DATETIME = "LAST STATUS RECEIVED DATETIME";

	//
	// Constructor
	//
	public ServiceRegistrationDAO(VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	public ServiceRegistration create(ServiceRegistration registration)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_CREATE_SERVICE_REG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SR_SERVICE_ID, registration.getServiceId());
		hm.put(SR_SERVICE_VERSION, registration.getServiceVersion());
		hm.put(SR_SERVER_NAME, registration.getServerName());
		hm.put(SR_IP_ADDRESS, registration.getIpAddress());
		hm.put(SR_PLACE_FK, Integer.toString(registration.getPlace().getId()));
		if (registration.getStatus() == ServiceStatus.Offline)
			hm.put(SR_STATUS, "0");
		else {
			hm.put(SR_STATUS, "1");
		}
		hm.put(SR_URL, registration.getUrl());
		hm.put(SR_SERVICE_DATA, registration.getServiceData());
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		if (result.startsWith(FAILURE)) {
			throw new MethodException("Failed to save ServiceRegistration");
		}
		String[] resultParts = result.split(DELIMITER);
		registration.setId(Integer.parseInt(resultParts[2]));
		return registration;
	}

	@Override
	public void delete(int registrationId) throws MethodException,
			ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_DELETE_SERVICE_REG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SR_PK, Integer.toString(registrationId));
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		if (result.startsWith(FAILURE)) {
			throw new MethodException("Failed to update ServiceRegistration "
					+ Integer.toString(registrationId));
		}
	}

	public List<ServiceRegistration> findByServiceId(String serviceId)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_GET_SERVICES);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SR_SERVICE_ID, serviceId);
		hm.put(SR_LAST_STATUS_RECEIVED_DATETIME, "20100101.000000");
		vm.addParameter(VistaQuery.ARRAY, hm);
		return translateServiceRegistrations(executeRPC(vm));
	}

	@Override
	public ServiceRegistration getEntityById(String id) throws MethodException,
			ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_GET_SERVICE_REG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SR_PK, id);
		vm.addParameter(VistaQuery.ARRAY, hm);
		return translateServiceRegistration(executeRPC(vm));
	}

	public ServiceRegistration translateServiceRegistration(String message) {
		return null;
	}

	public List<ServiceRegistration> translateServiceRegistrations(
			String message) throws MethodException, ConnectionException {
		int firstLineBreakIndex = message.indexOf("\r\n");
		String firstLine;
		if (firstLineBreakIndex > 0)
			firstLine = message.substring(0, firstLineBreakIndex);
		else
			firstLine = message;
		String[] lineParts = firstLine.split(DELIMITER);
		if (Integer.parseInt(lineParts[0]) == -1)
			throw new MethodException(message);
		String messageContent = message.substring(firstLineBreakIndex + 2);
		ServiceRegistry registry;
		try {
			XStream deserializer = getConfiguredXStream();
			registry = (ServiceRegistry) deserializer.fromXML(messageContent);
		} catch (XStreamException xsX) {
			throw new MethodException(xsX.getMessage(), xsX);
		}
		return registry.getRegistrations();
	}

	protected XStream getConfiguredXStream() {
		XStream xstream = new XStream();

		xstream.alias("SERVICEREGISTRYS", ServiceRegistry.class);
		xstream.addImplicitCollection(ServiceRegistry.class, "registrations");
		xstream.alias("SERVICEREGISTRY", ServiceRegistration.class);
		xstream.aliasAttribute(ServiceRegistration.class, "id", "PK");
		xstream.useAttributeFor(ServiceRegistration.class, "id");

		xstream.aliasAttribute(ServiceRegistration.class, "serviceId",
				"SERVICEID");
		xstream.useAttributeFor(ServiceRegistration.class, "serviceId");

		xstream.aliasAttribute(ServiceRegistration.class, "serviceVersion",
				"SERVICEVERSION");
		xstream.useAttributeFor(ServiceRegistration.class, "serviceVersion");

		xstream.aliasAttribute(ServiceRegistration.class, "serverName",
				"SERVERNAME");
		xstream.useAttributeFor(ServiceRegistration.class, "serverName");

		xstream.aliasAttribute(ServiceRegistration.class, "ipAddress",
				"IPADDRESS");
		xstream.useAttributeFor(ServiceRegistration.class, "ipAddress");

		xstream.aliasAttribute(ServiceRegistration.class, "url", "URL");
		xstream.useAttributeFor(ServiceRegistration.class, "url");

		xstream.aliasAttribute(ServiceRegistration.class, "placeId", "PLACEFK");
		xstream.useAttributeFor(ServiceRegistration.class, "placeId");

		xstream.aliasAttribute(ServiceRegistration.class, "statusId", "STATUS");
		xstream.useAttributeFor(ServiceRegistration.class, "statusId");

		xstream.aliasAttribute(ServiceRegistration.class,
				"registrationDateTimeString", "REGISTRATIONDATETIME");
		xstream.useAttributeFor(ServiceRegistration.class,
				"registrationDateTimeString");

		xstream.aliasAttribute(ServiceRegistration.class,
				"lastStatusReceivedDateTimeString", "LASTSTATUSRECEIVEDDATETIME");
		xstream.useAttributeFor(ServiceRegistration.class,
				"lastStatusReceivedDateTimeString");

		xstream.aliasAttribute(ServiceRegistration.class, "serviceData",
				"SERVICEDATA");
		xstream.useAttributeFor(ServiceRegistration.class, "serviceData");

		return xstream;
	}

	@Override
	public ServiceRegistration update(ServiceRegistration registration)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_UPDATE_SERVICE_REG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(SR_PK, Integer.toString(registration.getId()));
		if (registration.getStatus() == ServiceStatus.Offline)
			hm.put(SR_STATUS, "0");
		else {
			hm.put(SR_STATUS, "1");
		}
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		if (result.startsWith(FAILURE)) {
			throw new MethodException("Failed to update ServiceRegistration "
					+ Integer.toString(registration.getId()));
		}
		return registration;
	}
}
