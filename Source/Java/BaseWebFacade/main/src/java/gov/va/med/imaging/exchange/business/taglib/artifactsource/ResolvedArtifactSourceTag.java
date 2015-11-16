/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 9, 2010
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
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import java.util.List;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class ResolvedArtifactSourceTag 
extends AbstractResolvedArtifactSourceTag 
{
	private static final long serialVersionUID = 1L;
	
	private String siteNumber = null;
	private Logger logger = Logger.getLogger(this.getClass());

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.artifactsource.AbstractResolvedArtifactSourceTag#getResolvedArtifactSource()
	 */
	@Override
	protected ResolvedArtifactSource getResolvedArtifactSource()
	throws JspException 
	{
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
    		
    		List<ResolvedArtifactSource> sources = router.getResolvedArtifactSource( 
    			RoutingTokenImpl.createVARadiologySite(getSiteNumber())
    		);
    		
    		return sources.get(0);
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

	/**
     * @return the siteNumber
     */
    public String getSiteNumber()
    {
    	return siteNumber;
    }
    
	/**
     * @param siteNumber the siteNumber to set
     */
    public void setSiteNumber(String siteNumber)
    {
    	this.siteNumber = siteNumber;
    	//resolvedSite = null;
    }
}
