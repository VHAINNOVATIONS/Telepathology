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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.DicomApplicationEntityDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;

public class VistaImagingDicomApplicationEntityDataSourceService 
extends AbstractVersionableDataSource 
implements DicomApplicationEntityDataSourceSpi, VistaSessionFactory {

	public final static String MAG_REQUIRED_VERSION = "3.0P116";
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	
	public VistaImagingDicomApplicationEntityDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
	}
	
	// to support local data source
	public VistaImagingDicomApplicationEntityDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource) 
	{
		super(resolvedArtifactSource, SUPPORTED_PROTOCOL);
	}
	
	@Override
	public VistaSession getVistaSession() throws IOException,
			ConnectionException, MethodException {
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}


	@Override
	public DicomAE getAEByAETandLocation(DicomAE appEntity)
				throws MethodException, ConnectionException {
		DicomAEDAO dao = new DicomAEDAO(this);
		return dao.getEntityByExample(appEntity);
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
