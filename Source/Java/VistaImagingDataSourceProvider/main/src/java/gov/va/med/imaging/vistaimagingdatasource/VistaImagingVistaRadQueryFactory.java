/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 16, 2009
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

import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;

/**
 * Create queries for VistARad RPC calls
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingVistaRadQueryFactory 
{
	
	private final static String RPC_MAGJ_USER2 = "MAGJ USER2";
	private final static String RPC_MAGJ_CACHELOCATION = "MAGJ CACHELOCATION";
	private final static String RPC_MAGJ_EXAM_REPORT = "MAGJ EXAM REPORT";
	private final static String RPC_MAGJ_RADORDERDISP = "MAGJ RADORDERDISP";
	private final static String RPC_MAGJ_CPTMATCH = "MAGJ CPTMATCH";
	private final static String RPC_MAGJ_PT_ALL_EXAMS = "MAGJ PT ALL EXAMS";
	private final static String RPC_MAGJ_RADCASEIMAGES = "MAGJ RADCASEIMAGES";
	private final static String RPC_MAGJ_RADACTIVEEXAMS = "MAGJ RADACTIVEEXAMS";
	private final static String RPC_MAGJ_STUDYDATA = "MAGJ STUDY_DATA";
	private final static String RPC_MAGJ_VIX_LOG_REMOTE_IMG_ACCESS = "MAGJ VIX LOG REMOTE IMG ACCESS";
	
	/**
	 * Create query for the MAGJ RADACTIVEEXAMS rpc
	 * 
	 * @param examStatus
	 * @param modalities
	 * @return
	 */
	public static VistaQuery createMagJGetActiveExamsQuery(String listDescriptor)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_RADACTIVEEXAMS);
		query.addParameter(VistaQuery.LITERAL, listDescriptor); // not sure if these are the right parameters, don't seem to be working properly
		return query;
	}
	
	/**
	 * Create query for the MAGJ PT ALL EXAMS rpc
	 * 
	 * @param patientDfn
	 * @return
	 */
	public static VistaQuery createMagJGetPatientExamsQuery(String patientDfn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_PT_ALL_EXAMS);
		query.addParameter(VistaQuery.LITERAL, patientDfn + "^^99999");
		return query;
	}
	
	/**
	 * This version should only be used if Patch 104 KIDS is installed
	 * @param examId
	 * @param useTga
	 * @param forceImagesFromJb
	 * @return
	 */
	public static VistaQuery createMagJGetExamImages(String examId, boolean useTga,
			boolean forceImagesFromJb)
	{
		String vixParameterCode = (forceImagesFromJb ? "VIX-Open" : "VIX-Metadata");
		// use the forceImagesFromJb parameter when John C. makes it available as part of Patch 104
		return createMagJGetExamImages(examId, useTga,vixParameterCode);
	}
	
	/**
	 * Create query for the MAGJ RADCASEIMAGES rpc to get the images for an exam
	 * 
	 * @param examId
	 * @param useTga
	 * @return
	 */
	public static VistaQuery createMagJGetExamImages(String examId, boolean useTga)
	{
		return createMagJGetExamImages(examId, useTga, "VIX");
	}
	
	private static VistaQuery createMagJGetExamImages(String examId, boolean useTga,
			String vixParameterCode)
	{
		TransactionContextFactory.get().addDebugInformation("Getting exam images for exam '" + examId + "' with VIX parameter '" + vixParameterCode + "'");
		VistaQuery query = new VistaQuery(RPC_MAGJ_RADCASEIMAGES);
		StringBuilder queryParams = new StringBuilder();
		queryParams.append(vixParameterCode); // per John C. request on 11/5/2009
		queryParams.append("^");
		queryParams.append(examId);
		queryParams.append("^");
		queryParams.append("IK");
		queryParams.append("^");
		queryParams.append("0");
		queryParams.append("^");
		if(useTga)
		{
			queryParams.append("1");
		}
		else
		{
			queryParams.append("0");
		}
		query.addParameter(VistaQuery.LITERAL, queryParams.toString());
		return query;
	}
	
	/**
	 * Create query for the MAGJ CPTMATCH rpc
	 * 
	 * @param cptCode
	 * @return
	 */
	public static VistaQuery createMagJCptMatchQuery(String cptCode)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_CPTMATCH);
		StringBuilder queryParams = new StringBuilder();
		queryParams.append(cptCode);
		queryParams.append("^");
		queryParams.append("1");
		query.addParameter(VistaQuery.LITERAL, queryParams.toString());
		return query;
	}
	
	/**	 
	 * Create query for the MAGJ RADORDERDISP rpc to get the requisition report for an exam
	 * 
	 * @param examID expected to be in 1011^6979793.8675^1^86 form
	 * @return
	 */
	public static VistaQuery createMagJRequisitionReportQuery(String examId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_RADORDERDISP);
		query.addParameter(VistaQuery.LITERAL, examId);
		return query;
	}
	
	/**
	 * Create query for the MAGJ EXAM REPORT rpc to get the exam report
	 * 
	 * @param examID expected to be in 1011^6979793.8675^1^86 form
	 * @return
	 */
	public static VistaQuery createMagJExamReportQuery(String examId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_EXAM_REPORT);
		query.addParameter(VistaQuery.LITERAL, examId);
		return query;
	}
	
	/**
	 * Create query for the MAGJ USER2 rpc
	 * 
	 * @param version
	 * @param osVersion
	 * @return
	 */
	public static VistaQuery createMagJUserQuery(String version, String osVersion)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_USER2);
		StringBuilder queryParams = new StringBuilder();
		queryParams.append("^");
		queryParams.append(version);
		queryParams.append("^");
		queryParams.append(osVersion);
		query.addParameter(VistaQuery.LITERAL, queryParams.toString());
		return query;
	}
	
	/**
	 * Create query for the MAGJ CACHELOCATION rpc
	 * 
	 * @param version
	 * @param osVersion
	 * @return
	 */
	public static VistaQuery createMagJCacheLocationQuery(String cacheLocationId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_CACHELOCATION);
		StringBuilder queryParams = new StringBuilder();
		queryParams.append(cacheLocationId);		
		query.addParameter(VistaQuery.LITERAL, queryParams.toString());
		return query;
	}
	
	public static VistaQuery createMagJStudyDataQuery(String examId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_STUDYDATA);
		StringBuilder params = new StringBuilder();
		params.append("3");
		params.append("^");
		params.append(examId);
		params.append("^^1");
		query.addParameter(VistaQuery.LITERAL, params.toString());
		return query;
	}
	
	public static VistaQuery createMagJLogRemoteImgAccess(String inputParameter)
	{
		VistaQuery query = new VistaQuery(RPC_MAGJ_VIX_LOG_REMOTE_IMG_ACCESS);
		query.addParameter(VistaQuery.LITERAL, inputParameter);
		return query;
	}
}
