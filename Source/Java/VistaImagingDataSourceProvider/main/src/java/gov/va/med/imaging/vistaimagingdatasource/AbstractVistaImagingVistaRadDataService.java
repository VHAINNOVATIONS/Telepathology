/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 4, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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

import java.io.IOException;

import org.apache.log4j.Logger;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.protocol.vista.VistaImagingVistaRadTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

/**
 * This is an abstract base for both metadata and image requests for VistARad
 * 
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractVistaImagingVistaRadDataService
extends AbstractVersionableDataSource
{
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	private final static boolean useTgaImages = false;
	private final static Logger logger = Logger.getLogger(AbstractVistaImagingVistaRadDataService.class);
	
	public AbstractVistaImagingVistaRadDataService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	protected ExamImages getExamImagesFromExamId(VistaSession vistaSession, String encodedExamId, 
			String patientIcn, Site site, boolean forceImagesFromJb)
	throws MethodException, ConnectionException
	{
		logger.info("getExamImagesFromExamId(" + encodedExamId + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			// CTB 29Nov2009
			//String examId = Base32ConversionUtility.base32Decode(encodedExamId);
			String examId = encodedExamId;
			VistaQuery imagesQuery = getExamImagesQuery(examId, useTgaImages, 
					forceImagesFromJb);
			String result = vistaSession.call(imagesQuery);			
			return VistaImagingVistaRadTranslator.translateExamImagesIntoExamsMap(result, encodedExamId, patientIcn, site);		
		}
		catch(URNFormatException iurnfX)
		{
			logger.error("Error getting exam images for exam [" + encodedExamId + "]", iurnfX);
			throw new MethodException(iurnfX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error getting exam images for exam [" + encodedExamId + "]", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error getting exam images for exam [" + encodedExamId + "]", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting exam images for exam [" + encodedExamId + "]", ioX);
			throw new ConnectionException(ioX);
		}
	}
	
	protected abstract VistaQuery getExamImagesQuery(String examId, boolean useTgaImages, 
			boolean forceImagesFromJb);
	
}
