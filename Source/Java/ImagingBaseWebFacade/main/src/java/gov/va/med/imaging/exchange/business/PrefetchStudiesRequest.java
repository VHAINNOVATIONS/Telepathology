/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 6, 2008
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.PatientIdentifier;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.ImagingBaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.exchange.RoutingTokenHelper;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author VHAISWBECKEC
 *
 */
public class PrefetchStudiesRequest 
extends AbstractBaseServlet
{
	private static final long serialVersionUID = 1L;
	private final static String PATIENT_ICN_PARAMETER_NAME = "patientIcn";
	private final static String SITE_NUMBER_PARAMETER_NAME = "siteNumber";
	private final static String IMAGE_QUALITY_PARAMETER_NAME = "imageQuality";
	private final static String CONTENT_TYPE_PARAMETER_NAME = "contentType";
	
	/**
	 * 
	 */
	public PrefetchStudiesRequest()
	{
	}

	/**
	 * Post a request for prefetch on the asynchronous router queue and return immediately.
	 * The response from this servlet does not include a body.  Any errors are indicated in the HTTP headers.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException
    {
		String patientIcn = req.getParameter(PATIENT_ICN_PARAMETER_NAME);
		String siteNumber = req.getParameter(SITE_NUMBER_PARAMETER_NAME);
		String imageQuality = req.getParameter(IMAGE_QUALITY_PARAMETER_NAME);
		String contentType = req.getParameter(CONTENT_TYPE_PARAMETER_NAME);
		
		ImageFormatQualityList imageFormatList = new ImageFormatQualityList();
		
		ImageQuality iq = ImageQuality.REFERENCE;		// default value
		if(imageQuality != null)
		{
			try
			{
				int imageQualityOrdinal = Integer.parseInt(imageQuality);
				iq = ImageQuality.getImageQuality(imageQualityOrdinal);
			}
			catch(NumberFormatException nfX)
			{
				getLogger().warn("Invalid image quality parameter '" + imageQuality + "' is being ignored, reference quality images will be prefetched.");
			}
		}
		
		ImageFormat imageFormat = iq.equals(ImageQuality.THUMBNAIL) ? ImageFormat.JPEG : ImageFormat.DICOMJPEG2000;
		if(contentType != null)
		{
			imageFormat = ImageFormat.valueOfMimeType(contentType);
			if(ImageFormat.DICOM.equals(imageFormat) )
				imageFormat = ImageFormat.DICOMJPEG2000;
		}
		imageFormatList.add(new ImageFormatQuality(imageFormat, iq) );
		
		if(patientIcn == null)
		{
			writeErrorResponse(resp, "Required parameters '" + PATIENT_ICN_PARAMETER_NAME + "' was not provided.");
			return;
		}
		if(siteNumber == null)
		{
			writeErrorResponse(resp, "Required parameters '" + SITE_NUMBER_PARAMETER_NAME + "' was not provided.");
			return;
		}
		
		try
		{
			getRouter().prefetchPatientStudyList(RoutingTokenHelper.createSiteAppropriateRoutingToken(siteNumber), 
					PatientIdentifier.icnPatientIdentifier(patientIcn), null, imageFormatList);
		}
		catch (RoutingTokenFormatException rtfX)
		{
			writeErrorResponse(resp, rtfX.getMessage());
			return;
		}
		
		writeSuccessResponse(resp, null);
    }

	private void writeSuccessResponse(HttpServletResponse resp, String msg) 
	throws IOException
	{
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/xml");
		PrintWriter writer = resp.getWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		writer.write("<prefetch-request-response>");
		writer.write("<status>SUCCESS</status>");
		writer.write("<message>");
		writer.write(msg == null ? "Success" : msg);
		writer.write("</message>");
		writer.write("</prefetch-request-response>");
	}
	
	private void writeErrorResponse(HttpServletResponse resp, String message) 
	throws IOException
	{
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/xml");
		PrintWriter writer = resp.getWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		writer.write("<prefetch-request-response>");
		writer.write("<status>ERROR</status>");
		writer.write("<message>");
		writer.write(message == null ? "Error" : message);
		writer.write("</message>");
		writer.write("</prefetch-request-response>");
	}
	
	protected synchronized ImagingBaseWebFacadeRouter getRouter()
	throws ServletException
	{
		ImagingBaseWebFacadeRouter router;
		try
		{
			router = FacadeRouterUtility.getFacadeRouter(ImagingBaseWebFacadeRouter.class);
		} 
		catch (Exception x)
		{
			getLogger().error("Exception getting the facade router implementation.", x);
			return null;
		}
		
		return router;
	}
}
