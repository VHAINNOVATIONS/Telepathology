/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Nov 3, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.ImagingBaseWebFacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class PatientDocumentSetResultTag
extends AbstractDocumentSetResultTag
{
	private static final long serialVersionUID = 1L;
	
	private String homeCommunityId;
	private String repositoryId;
	private String patientId;
	private String fromDate;
	private String toDate;
	private String documentClinicalType;
	private String emptyResultMessage;

	/**
	 * @return the homeCommunityId
	 */
	public String getHomeCommunityId()
	{
		return this.homeCommunityId;
	}
	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId()
	{
		return this.repositoryId;
	}
	/**
	 * @param homeCommunityId the homeCommunityId to set
	 */
	public void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(String repositoryId)
	{
		this.repositoryId = repositoryId;
	}
	/**
     * @return the patientId
     */
    public String getPatientId()
    {
    	return patientId;
    }
    public void setPatientId(String patientId)
    {
    	this.patientId = patientId;
    }
    
	public String getFromDate()
    {
    	return fromDate;
    }
	public void setFromDate(String fromDate)
    {
    	this.fromDate = fromDate;
    }
	
	public String getToDate()
    {
    	return toDate;
    }
	public void setToDate(String toDate)
    {
    	this.toDate = toDate;
    }
	
	public String getDocumentClinicalType()
	{
		return this.documentClinicalType;
	}
	public void setDocumentClinicalType(String documentClinicalType)
	{
		this.documentClinicalType = documentClinicalType;
	}
	
	/**
	 * @return the emptyResultMessage
	 */
	public String getEmptyResultMessage()
	{
		return this.emptyResultMessage;
	}
	/**
	 * @param emptyResultMessage the emptyResultMessage to set
	 */
	public void setEmptyResultMessage(String emptyResultMessage)
	{
		this.emptyResultMessage = emptyResultMessage;
	}
	
	/**
	 * The method that actually gets the data from the router.
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectTag#getBusinessObject()
	 */
	@Override
	public synchronized DocumentSetResult getBusinessObject() 
	throws JspException
	{
		if(getHomeCommunityId() == null || getRepositoryId() == null)
			return null;
		
		DocumentFilter documentFilter = new DocumentFilter(getPatientId(), getDocumentClinicalType());
		ImagingBaseWebFacadeRouter router = getFacadeRouter();
		try
		{
			return router.getDocumentSetResult(
				RoutingTokenImpl.create(
					getHomeCommunityId(), 
					getRepositoryId()), 
					documentFilter
			);
		}
		catch (MethodException x)
		{
			getLogger().error(x.getMessage());
			throw new JspException(x);
		}
		catch (ConnectionException x)
		{
			getLogger().error(x.getMessage());
			throw new JspException(x);
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x.getMessage());
			throw new JspException(x);
		}
	}

}
