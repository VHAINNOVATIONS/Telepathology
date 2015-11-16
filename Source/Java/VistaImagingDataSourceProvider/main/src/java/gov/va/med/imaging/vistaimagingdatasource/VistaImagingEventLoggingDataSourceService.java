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

package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.EventLoggingDataSource;
import gov.va.med.imaging.exchange.business.AuditEvent;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;

public class VistaImagingEventLoggingDataSourceService 
extends EventLoggingDataSource 
implements VistaSessionFactory {

	public final static String MAG_REQUIRED_VERSION = "3.0P116";
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	

	public VistaImagingEventLoggingDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public VistaSession getVistaSession() throws IOException,
			ConnectionException, MethodException {
		//P116 - Check and confirm this is the correct context.  Event Logging is not specific to DICOM activites.
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.EventLoggingDataSource#postAuditEvent(gov.va.med.imaging.exchange.business.AuditEvent)
	 */
	@Override
	public AuditEvent postAuditEvent(AuditEvent auditEvent)
			throws MethodException, ConnectionException {
		AuditEventDAO dao = new AuditEventDAO(this);
		return dao.create(auditEvent);
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.AbstractVersionableDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() throws SecurityException {
		return true;
	}

	
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}
}
