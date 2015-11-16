/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 16, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.datasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

/**
 * 
 * This SPI defines operations that should be completed by other sites, not the local site. This SPI should never
 * be implemented by Vista/VistaImaging data source provider 
 * 
 * @author vhaiswwerfej
 *
 */
@SPI(description="This SPI defines operations that should be completed by other sites, not the local site.")
public interface ExternalSystemOperationsDataSourceSpi 
extends VersionableDataSourceSpi
{

	/**
	 * Tell the data source to initiate a prefetch operation for the specified exam. 
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean initiateExamPrefetchOperation(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Tell the data source to refresh the site service cache
	 */
	public abstract void refreshSiteServiceCache()
	throws MethodException, ConnectionException;
	
	/**
     * Request for the studies for the patient that match the filter be prefetched and cached. This does NOT include the 
     * binary image data, only the metadata
     * 
     * @param globalRoutingToken
     * @param patientICn
     * @param filter
     * @param studyLoadLevel
     * @throws MethodException
     * @throws ConnectionException
     */
    public abstract void prefetchPatientStudies(RoutingToken globalRoutingToken, String patientIcn, StudyFilter filter, StudyLoadLevel studyLoadLevel)
    throws MethodException, ConnectionException;
    
    /**
     * Request for the specific image to be prefetched and cached. The GAI may be from the VA or an external source and can 
     * @param imageUrn
     * @param imageFormatQualityList
     * @throws MethodException
     * @throws ConnectionException
     */
    public abstract void prefetchImage(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList)
    throws MethodException, ConnectionException;
    
    public abstract void prefetchExamImage(ImageURN imageUrn, ImageFormatQualityList imageFormatQualityList,
    		boolean includeTextFile)
    throws MethodException, ConnectionException;
    
    public abstract void prefetchGai(GlobalArtifactIdentifier gai)
    throws MethodException, ConnectionException;
}
