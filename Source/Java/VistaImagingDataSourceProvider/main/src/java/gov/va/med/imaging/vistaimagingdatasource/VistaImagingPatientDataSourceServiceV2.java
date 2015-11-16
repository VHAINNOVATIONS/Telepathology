/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 18, 2012
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
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPatientDataSourceServiceV2
extends VistaImagingPatientDataSourceService
{
	public final static String MAG_REQUIRED_VERSION = "3.0P122"; // Patch 122 registers the MAGJ GET TREATING SITES rpc to MAG WINDOWS
	private final static Logger logger = Logger.getLogger(VistaImagingPatientDataSourceServiceV2.class);
	
	public VistaImagingPatientDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	protected String getRequiredVistaImagingVersion()
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	@Override
	public List<String> getTreatingSites(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier, boolean includeTrailingCharactersForSite200) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getTreatingSites", getDataSourceVersion());
		logger.info("getTreatingSites(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();        	
        	String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);
        	VistaQuery vm = VistaImagingQueryFactory.createMagJGetTreatingSitesQuery(patientDfn);
	        String rtn = localVistaSession.call(vm);
	        return VistaImagingTranslator.convertTreatingSiteListToSiteNumbers(rtn, includeTrailingCharactersForSite200);
        } 
        catch (VistaMethodException e)
        {
        	logger.error("Error in getTreatingSites", e);
        	throw new MethodException(e.getMessage());
        } 
        catch (InvalidVistaCredentialsException e)
        {
        	logger.error("Error in getTreatingSites", e);
        	throw new InvalidCredentialsException(e.getMessage());
        }
        catch(PatientNotFoundException pnfX)
        {
        	logger.error("Patient not found [" + patientIdentifier + "]", pnfX);
        	// JMW 12/14/2010 - throw the exception now
        	// necessary so we can provide the correct error for XCA requests
        	throw pnfX;
        	// return null;
        	// JMW 9/22/2009
        	// not really sure about this, was returning null but that creating inconsistencies
        	// with Federation which would return an empty array list since the command 
        	// converts the null to the array list, now doing that here instead to be 
        	// consistent.  Might want to throw exception or return null and handle 
        	// differently to allow knowledge of the patient not found versus having now sites.
        	//return new ArrayList<String>(0);
        }
        catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
        }
        finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	protected String getDataSourceVersion()
	{
		return "2";
	}
}
