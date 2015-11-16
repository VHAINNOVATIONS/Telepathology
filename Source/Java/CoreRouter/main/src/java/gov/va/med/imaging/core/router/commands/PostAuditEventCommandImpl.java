/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
  Description: 

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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.EventLoggingDataSourceSpi;
import gov.va.med.imaging.exchange.business.AuditEvent;

/**
 * This Router command allows logging Audit Events into a Data Source.  The Audit Event Log 
 * is a VA requirement to show basic operations and/or interactions.  Examples would be System
 * Shutdown and Startup, and DICOM Service requests.
 * 
 * Though this command was originally created to log DICOM Activity, it is part of the Core 
 * Router so it may be used by any application within VistA Imaging.
 * 
 * The Audit Event is automatically sent to Log4J as well.
 * 
 * A Boolean is returned to notify of the success to log the Audit Event object into the 
 * Data Source.
 * 
 * @author vhaiswpeterb
 * 
 */
public class PostAuditEventCommandImpl 
extends AbstractDataSourceCommandImpl<Boolean, EventLoggingDataSourceSpi> {

	private static final long serialVersionUID = 2582434733580430837L;
	private static final String SPI_METHOD_NAME = "postAuditEvent";

	private AuditEvent auditEvent = null;
	private RoutingToken routingToken;
	
	public PostAuditEventCommandImpl(RoutingToken routingToken, AuditEvent auditEvent){
		this.auditEvent = auditEvent;
		this.routingToken = routingToken;
	}

	public AuditEvent getAuditEvent() {
		return auditEvent;
	}

	@Override
	public RoutingToken getRoutingToken() {
		if(this.routingToken == null){
			try {
				this.routingToken = this.getLocalRealmRadiologyRoutingToken();
			} catch (MethodException e) {
				getLogger().warn(this.getClass().getName()+": Failed to create Routing Token.");
			}
		}
		return this.routingToken;
	}

	@Override
	protected Boolean getCommandResult(EventLoggingDataSourceSpi spi)
			throws ConnectionException, MethodException {
		
		getLogger().info("Audit Event: "+auditEvent.getEvent()
				+"; "+auditEvent.getHostname()
				+": "+auditEvent.getApplicationName());
		
		return spi.postAuditEvent(auditEvent).isSuccessful();
	}

	@Override
	protected Class<EventLoggingDataSourceSpi> getSpiClass() {
		return EventLoggingDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { AuditEvent.class };
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] { getAuditEvent() };
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() {
		return this.routingToken.getRepositoryUniqueId();
	}

}
