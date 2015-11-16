/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 30, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.exchange.business.taglib.patient;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.TransactionContextHelper;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.log4j.Logger;

/**
 * NOTE: this class uses code from the Spring Framework to get the
 * context.  Because we must derive from AbstractStudyListTag and
 * therefore cannot derive from spring's RequestContextAwareTag 
 * we do the stuff that RequestContextAwareTag does for us in the
 * doStartTag() method.
 * 
 * @author VHAISWBECKEC
 * 
 */
public class PatientListTag 
extends AbstractPatientListTag 
implements TryCatchFinally
{
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());
	
	private String siteNumber;
	private String patientName;
	
	// ==============================================================================
	// Properties that may be set from the JSP
	// ==============================================================================
	
	/**
     * @return the siteNumber
     */
    public String getSiteNumber()
    {
    	return siteNumber;
    }
    public void setSiteNumber(String siteNumber)
    {
    	this.siteNumber = siteNumber;
    }

	/**
     * @return the patientId
     */
    public String getPatientName()
    {
    	return patientName;
    }
    public void setPatientName(String patientName)
    {
    	this.patientName = patientName;
    }
    
	@Override
    protected List<Patient> getPatientList() 
    throws JspException
    {
    	try 
    	{
    		if(getPatientName() == null || getPatientName().length() < 1)
    			return null;
    		
    		if(getSiteNumber() == null || getSiteNumber().length() < 1)
    			return null;
    		
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
    		TransactionContextHelper.setTransactionContextFields("findPatients", "");
    		return router.getPatientList( getPatientName(), RoutingTokenImpl.createVARadiologySite(getSiteNumber()) );
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
