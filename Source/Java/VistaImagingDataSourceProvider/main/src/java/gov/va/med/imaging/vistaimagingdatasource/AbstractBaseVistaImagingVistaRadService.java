/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 16, 2009
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.*;
import gov.va.med.imaging.protocol.vista.VistaImagingVistaRadTranslator;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingVistaRadCommonUtilities;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract study graph service for VistA.  This implements some of the common functions needed by the version 0 and main
 * VistA study graph service. This just eliminates some duplicate code that was in both classes 
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseVistaImagingVistaRadService 
extends AbstractVistaImagingVistaRadDataService
implements VistaRadDataSourceSpi 
{
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public AbstractBaseVistaImagingVistaRadService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
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

	/**
	 * Return the required version of VistA Imaging necessary to use this service
	 * @return
	 */
	protected abstract String getRequiredVistaImagingVersion();
	
	protected abstract String getDataSourceVersion();
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		getLogger().info("isVersionCompatible searching for version [" + getRequiredVistaImagingVersion() + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;		
		try
		{			
			localVistaSession = getVistaSession();
			getLogger().info("Got VistaSession (" + localVistaSession.getSessionIndex() + ") for version [" + getRequiredVistaImagingVersion() + "].");
			return true;
		}
		
		catch(SecurityCredentialsExpiredException sceX)
		{
			// caught here to be sure it gets thrown as SecurityCredentialsExpiredException, not ConnectionException
			throw sceX;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			// error already displayed
		}
		catch(MethodException mX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", mX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (mX == null ? "<null error>" : mX.getMessage()));
		}
		catch(ConnectionException cX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", cX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (cX == null ? "<null error>" : cX.getMessage()));
		}
		catch(IOException ioX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", ioX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (ioX == null ? "<null error>" : ioX.getMessage()));
		}
		finally
		{
			try{localVistaSession.close();}
			catch(Throwable t){}
		}
		return false;
	}	
	
	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, InvalidVistaVistaRadVersionException//, SecurityCredentialsExpiredException
    {
		return VistaImagingVistaRadCommonUtilities.getVistaSession(getMetadataUrl(), getSite(), getRequiredVistaImagingVersion());
    }
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getRelevantPriorCptCodes(java.lang.String)
	 */
	@Override
	public String[] getRelevantPriorCptCodes(RoutingToken globalRoutingToken, String cptCode)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getRelevantPriorCptCodes", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getRelevantPriorCptCodes(" + cptCode + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			return getRelevantPriorCptCodesInternal(vistaSession, cptCode);
		}
		
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}		
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}
	
	protected String[] getRelevantPriorCptCodesInternal(VistaSession vistaSession, String cptCode)
	throws MethodException, InvalidCredentialsException, IOException
	{
		try
		{
			
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJCptMatchQuery(cptCode);
			String rtn = vistaSession.call(query);
			return VistaImagingVistaRadTranslator.translateRelevantCptCodeResponse(rtn);
		}
		catch(VistaMethodException mX)
		{
			throw new MethodException(mX); 
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getActiveExams(java.lang.String, java.lang.String)
	 */
	@Override
	public ActiveExams getActiveExams(RoutingToken globalRoutingToken, String listDescriptor)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getActiveExams", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getActiveExams(" + listDescriptor + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();			
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJGetActiveExamsQuery(listDescriptor);
			String rtn = vistaSession.call(query);			
			ActiveExams activeExams = translateActiveExamsResponse(rtn, vistaSession);		
			getLogger().info("Returning active exams containing '" + activeExams.size() + "' exams.");
			return activeExams;
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}
	
	/**
	 * Convert output from VistA from the MAGJ RADACTIVEEXAMS rpc into a map of ExamImage objects
	 * 
	 * Translator is here because more RPC calls will need to be made
	 * 
	 * @param response
	 * @return
	 */
	private ActiveExams translateActiveExamsResponse(String response, VistaSession vistaSession)
	{
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);
		getLogger().info("Translating '" + (lines != null ? lines.length : 0) + "' lines into active exams response.");
		String headerLine1 = "";
		String headerLine2 = "";
		if(lines.length > 0)
		{
			headerLine1 = lines[0];
		}
		if(lines.length > 1)
		{
			headerLine2 = lines[1];
		}
		HashMap<String, String> patientDfnMap = new HashMap<String, String>();
		ActiveExams result = new ActiveExams(getSite().getSiteNumber(), headerLine1, headerLine2);
		for(int i = 2; i < lines.length; i++)
		{
			ActiveExam activeExam = translateActiveExam(lines[i], vistaSession, patientDfnMap);
			if(activeExam != null)
				result.add(activeExam);			
		}
		return result;
	}
	
	/**
	 * This used to be an abstract method implemented by the derived classes but now the RPC calls made
	 * here always need to be available so this is no longer abstract.
	 * 
	 * @param line
	 * @param vistaSession
	 * @param patientDfnMap
	 * @return
	 */
	private ActiveExam translateActiveExam(String line, VistaSession vistaSession, 
			HashMap<String, String> patientDfnMap)
	{
		// both patch 90 and 101 should have the RPC calls necessary to do this conversion. 
		String examId = StringUtils.MagPiece(line, StringUtils.STICK, 2);		
		String patientDfn = StringUtils.MagPiece(examId, StringUtils.CARET, 1);
		
		String patientIcn = patientDfnMap.get(patientDfn);
		if(patientIcn == null)
		{
			getLogger().debug("Did not find ICN in map for DFN '" + patientDfn + "'");
			try
			{
				patientIcn = VistaImagingVistaRadCommonUtilities.getPatientICN(vistaSession, patientDfn);
			}
			catch(PatientNotFoundException pnfX)
			{
				String msg = "Unable to convert patient DFN '" + patientDfn + "' to ICN, maintaining exam without valid exam Id.";
				getLogger().warn(msg, pnfX);
				patientIcn = "";
			}
			catch(IOException ioX)
			{
				String msg = "Unable to convert patient DFN '" + patientDfn + "' to ICN, maintaining exam without valid exam Id.";
				getLogger().warn(msg, ioX);
				patientIcn = "";
			}
			catch(ConnectionException cX)
			{
				String msg = "Unable to convert patient DFN '" + patientDfn + "' to ICN, maintaining exam without valid exam Id.";
				getLogger().warn(msg, cX);
				patientIcn = "";
			}
			catch(MethodException mX)
			{
				String msg = "Unable to convert patient DFN '" + patientDfn + "' to ICN, maintaining exam without valid exam Id.";
				getLogger().warn(msg, mX);
				patientIcn = "";
			}	
			patientDfnMap.put(patientDfn, patientIcn);
		}
		else
		{
			getLogger().debug("Found ICN '" + patientIcn + "' in map for DFN '" + patientDfn + "'");
		}
		
		ActiveExam activeExam = new ActiveExam(getSite().getSiteNumber(), 
				examId, patientIcn);
		activeExam.setRawValue(line);
		return activeExam;
	}
	
	protected String getExamPresentationStateInternal(VistaSession vistaSession, StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		try
		{
			// CTB 29Nov2009
			//String examId = Base32ConversionUtility.base32Decode(studyUrn.getStudyId());
			String examId = studyUrn.getStudyId();
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJStudyDataQuery(examId);
			String rtn = vistaSession.call(query);
			return VistaImagingCommonUtilities.extractInvalidCharactersFromReport(rtn);
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
	}
	
	protected String getExamReportInternal(VistaSession vistaSession, StudyURN studyUrn)
	throws MethodException, ConnectionException 
	{
		try
		{
			// CTB 29Nov2009
			//String examId = Base32ConversionUtility.base32Decode(studyUrn.getStudyId());
			String examId = studyUrn.getStudyId();
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJExamReportQuery(examId);
			String rtn = vistaSession.call(query);
			return VistaImagingCommonUtilities.extractInvalidCharactersFromReport(rtn);
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getExamReport(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public String getExamReport(StudyURN studyUrn) 
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getExamReport", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getExamReport(" + studyUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();	
			String report = getExamReportInternal(vistaSession, studyUrn);
			TransactionContextFactory.get().setDataSourceBytesReceived(report == null ? 0L : report.length());
			return report;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getExamRequisitionReport(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public String getExamRequisitionReport(StudyURN studyUrn)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getExamRequisitionReport", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getExamRequisitionReport(" + studyUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();	
			String report = getExamRequisitionReportInternal(vistaSession, studyUrn);
			TransactionContextFactory.get().setDataSourceBytesReceived(report == null ? 0L : report.length());
			return report;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}
	
	protected String getExamRequisitionReportInternal(VistaSession vistaSession, StudyURN studyUrn)
	throws MethodException, ConnectionException 
	{
		try
		{
			// CTB 29Nov2009
			//String examId = Base32ConversionUtility.base32Decode(studyUrn.getStudyId());
			String examId = studyUrn.getStudyId();
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJRequisitionReportQuery(examId);
			String rtn = vistaSession.call(query);
			return VistaImagingCommonUtilities.extractInvalidCharactersFromReport(rtn);
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
	}
	
	private List<Exam> getShallowExamsForPatientInternal(VistaSession vistaSession, String patientICN)
	throws MethodException, VistaMethodException, IOException, ConnectionException, InvalidVistaCredentialsException
	{
		getLogger().info("Getting shallow exams for patient '" + patientICN + "' from site '" + getSite().getSiteNumber() + "'.");
		String patientDfn = VistaImagingVistaRadCommonUtilities.getPatientDFN(vistaSession, patientICN);
		getLogger().info("translated patient ICN '" + patientICN + "' into DFN '" + patientDfn + "', about to make call to get patient exams.");
		VistaQuery patientExamsQuery = VistaImagingVistaRadQueryFactory.createMagJGetPatientExamsQuery(patientDfn);
		String result = vistaSession.call(patientExamsQuery);
		List<Exam> exams = VistaImagingVistaRadTranslator.translateExamsResponse(result, getSite(), patientICN);
		return exams;
	}

	public ExamListResult getExamsForPatient(RoutingToken globalRoutingToken, String patientICN, 
			boolean fullyLoadExams, boolean forceRefresh, boolean forceImagesFromJb)
    throws MethodException, ConnectionException
    {
		VistaCommonUtilities.setDataSourceMethodAndVersion("getExamsForPatient", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getExamsForPatient(" + patientICN + ") " + 
				"fullyLoaded=" + fullyLoadExams + ", forceImagesFromJb=" + forceImagesFromJb + " TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			List<Exam> exams = getShallowExamsForPatientInternal(vistaSession, patientICN);
			getLogger().info("Found '" + exams.size() + "' exams for patient [" + patientICN + "]");
			if(fullyLoadExams)
			{
				for(Exam exam : exams)
				{
					loadExam(vistaSession, exam, forceImagesFromJb);
				}
			}
			return ExamListResult.createFullResult(exams);			
		}
		catch(VistaMethodException mX)
		{
			throw new MethodException(mX); 
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
    }
	
	private void loadExam(VistaSession vistaSession, Exam exam, boolean forceImagesFromJb)
	throws ConnectionException, MethodException
	{				
		StudyURN studyUrn = exam.getStudyUrn();
		getLogger().info("loadExam(" + studyUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		String report = getExamReportInternal(vistaSession, studyUrn);
		exam.setExamReport(report);
		
		String requisitionReport = getExamRequisitionReportInternal(vistaSession, studyUrn);
		exam.setExamRequisitionReport(requisitionReport);
		
		ExamImages images = getExamImages(vistaSession, studyUrn, forceImagesFromJb);
		exam.setImages(images);			
		
		String presentationStateData = getExamPresentationStateInternal(vistaSession, studyUrn);
		exam.setPresentationStateData(presentationStateData);
		getLogger().info("Exam '" + studyUrn.toString() + "' loaded with '" + (images == null ? "<null>" : images.size()) + "' images.");		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getExamImagesForExam(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public ExamImages getExamImagesForExam(StudyURN studyUrn)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getExamImagesForExam", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getExamsForPatient(" + studyUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			ExamImages images = getExamImages(vistaSession, studyUrn, true);
			getLogger().info("Found '" + images.size() + "' images for exam URN '" + studyUrn.toString() + "'.");
			return images;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	private ExamImages getExamImages(VistaSession vistaSession, StudyURN studyUrn, 
			boolean forceImagesFromJb)
	throws MethodException, ConnectionException
	{
		return getExamImagesFromExamId(vistaSession, 
				studyUrn.getStudyId(), studyUrn.getPatientId(), getSite(),
				forceImagesFromJb);		
	}

	public PatientRegistration getNextPatientRegistration(RoutingToken globalRoutingToken)
    throws MethodException, ConnectionException    
    {
		throw new UnsupportedServiceMethodException(
			VistaRadDataSourceSpi.class, null, "getNextPatientRegistration()"
		);
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#getExam(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public Exam getExam(StudyURN studyUrn) 
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getExam", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getExam(" + studyUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			
			List<Exam> exams = getShallowExamsForPatientInternal(vistaSession, studyUrn.getPatientId());
			Exam exam = null;
			for(Exam ex : exams)
			{
				if(ex.getExamId().equals(studyUrn.getStudyId()))
				{
					exam = ex;
					break;
				}
			}				
			if(exam == null)
			{
				String msg = "Did not find exam that matches URN '" + studyUrn.toString() + ".";
				getLogger().error(msg);
				throw new MethodException(msg);
			}
			loadExam(vistaSession, exam, true);
			getLogger().info("Found exam for URN '" + studyUrn.toString() + "'.");
			return exam;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException mX)
		{
			throw new MethodException(mX); 
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}
	
	/**
	 * Return the simple name of the concrete class (not the abstract class) 
	 * @return
	 */
	private String getClassSimpleName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	protected VistaQuery getExamImagesQuery(String examId,
			boolean useTgaImages, boolean forceImagesFromJb)
	{
		return VistaImagingVistaRadQueryFactory.createMagJGetExamImages(examId, 
				useTgaImages);
	}
}
