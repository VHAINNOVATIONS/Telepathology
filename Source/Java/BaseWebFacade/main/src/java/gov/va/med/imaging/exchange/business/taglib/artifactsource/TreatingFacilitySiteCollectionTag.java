package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.taglib.exceptions.MissingRequiredArgumentException;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class TreatingFacilitySiteCollectionTag 
extends AbstractArtifactSourceCollectionTag
{
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());
	private String patientIcn;
	private String siteNumber;

	public String getPatientIcn()
    {
    	return patientIcn;
    }

	public void setPatientIcn(String patientIcn)
    {
    	this.patientIcn = patientIcn;
    }

	public String getSiteNumber()
    {
    	return siteNumber;
    }

	public void setSiteNumber(String siteNumber)
    {
    	this.siteNumber = siteNumber;
    }


	@Override
	protected Collection<ResolvedArtifactSource> getArtifactSources() 
	throws JspException, MissingRequiredArgumentException
	{
		if(getPatientIcn() == null || getPatientIcn().length() < 1)
			throw new MissingRequiredArgumentException("The patient ICN is required but was not provided.");
		
		if(getSiteNumber() == null || getSiteNumber().length() < 1)
			throw new MissingRequiredArgumentException("The site number is required but was not provided.");
		
    	//Router vixCore = ApplicationContextUtil.getRouter(this.pageContext);
    	
    	try 
    	{
        	BaseWebFacadeRouter router;
    		try
    		{
    			router = FacadeRouterUtility.getFacadeRouter(BaseWebFacadeRouter.class);
    		} 
    		catch (Exception x)
    		{
    			logger.error("Exception getting the facade router implementation.", x);
    			throw new JspException(x);
    		}
    		
    		return router.getTreatingSites( 
    			RoutingTokenImpl.createVARadiologySite(getSiteNumber()),
    			PatientIdentifier.icnPatientIdentifier(getPatientIcn())
    		);
    	}
    	catch(MethodException mX)
    	{
    		logger.error(mX);
    		throw new JspException(mX);
    	}
    	catch(ConnectionException mX)
    	{
    		logger.error(mX);
    		throw new JspException(mX);
    	}
		catch (RoutingTokenFormatException rtfX)
		{
    		logger.error(rtfX);
    		throw new JspException(rtfX);
		}
    }

	
}
