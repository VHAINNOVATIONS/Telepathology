package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.url.vista.VistaQuery;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VistaImagingDicomQueryFactory
{
	// MAG rpc calls
    public static final String CFIND_QUERY = "MAG CFIND QUERY";
    
    
    
    //Context RPCs
    public static final String DICOM_GATEWAY_FULL_CONTEXT = "MAG DICOM GATEWAY FULL";
    public final static String DICOM_QR_CONTEXT = "MAG DICOM VISA"; // "MAG DICOM QUERY RETRIEVE";
    public final static String MAG_WINDOWS_CONTEXT = "MAG WINDOWS";

    //RPCs
    public static final String CHECK_AETITLE = "MAG DICOM CHECK AE TITLE";
    public static final String GET_VISTA_AETITLE = "MAG DICOM VISTA AE TITLE";
    public static final String GET_GATEWAY_INFO = "MAG DICOM GET GATEWAY INFO";
    public static final String STUDY_UID_QUERY = "MAG STUDY UID QUERY";
    public static final String GET_CURRENT_IMAGE_INFO = "MAG IMAGE CURRENT INFO";
    
	private final static String RPC_DGW_INSTRUMENT_LIST = "MAGV DGW INSTRUMENT LIST";
	private final static String RPC_DGW_MODALITY_LIST = "MAGV DGW MODALITY LIST";
	private final static String RPC_GET_SRC_AE_SEC_MX = "MAGV GET SRC AE SEC MX";
	private final static String RPC_MAGV_GET_DGW_EMAIL_INFO = "MAGV GET DGW CONFIG"; // "MAGV GET EMAIL INFO";
	private final static String RPC_DGW_UID_ACTION_LIST = "MAGV DGW ACTION UID LIST";




	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // for DICOM UID generation only
	
	private static Logger getLogger()
	{
		return Logger.getLogger(VistaImagingDicomQueryFactory.class);
	}
	
	/**
	 * Initiates the background task on VistA to retrieve the results of the query
	 * 
	 * @param patientICN
	 * @return
	 * @throws MethodException
	 */
	@Deprecated
	public static VistaQuery createInitiateCFindTaskVistaQuery(HashMap<String, String> tags, String result, String offset, String maxReturn) 
	{
		VistaQuery vm = new VistaQuery(CFIND_QUERY);
		vm.addParameter(VistaQuery.LIST, tags);
		vm.addParameter(VistaQuery.LITERAL, result);
		vm.addParameter(VistaQuery.LITERAL, offset);
		vm.addParameter(VistaQuery.LITERAL, maxReturn);

		return vm;
	}
	
	/**
	 * Checks to see if the background VistA task has completed, and returns query results if possible
	 * 
	 * @param tags
	 * @param result
	 * @param offset
	 * @param maxReturn
	 * @return
	 */
	@Deprecated
	public static VistaQuery createRetrieveCFindResultsVistaQuery(HashMap<String, String> tags, String result, String offset, String maxReturn) 
	{
		VistaQuery vm = new VistaQuery(CFIND_QUERY);
		vm.addParameter(VistaQuery.LITERAL, tags);
		vm.addParameter(VistaQuery.LITERAL, result);
		vm.addParameter(VistaQuery.LITERAL, offset);
		vm.addParameter(VistaQuery.LITERAL, maxReturn);

		return vm;
	}
	
	/**
	 * Cleans up the temporary storage for the results on the VistA server
	 * 
	 * @param tags
	 * @param result
	 * @param maxReturn
	 * @return
	 */
	@Deprecated
	public static VistaQuery createCleanUpCFindResultsVistaQuery(HashMap<String, String> tags, String result, String maxReturn) 
	{
		VistaQuery vm = new VistaQuery(CFIND_QUERY);
		vm.addParameter(VistaQuery.LITERAL, tags);
		vm.addParameter(VistaQuery.LITERAL, result);
		vm.addParameter(VistaQuery.LITERAL, "-1");
		vm.addParameter(VistaQuery.LITERAL, maxReturn);


		return vm;
	}

	public static VistaQuery createGetDgwInstrumentListQuery(String hostName)
	{
		VistaQuery vm = new VistaQuery(RPC_DGW_INSTRUMENT_LIST);
		vm.addParameter(VistaQuery.LITERAL, hostName);
		
		return vm;
	}

	public static VistaQuery createGetDgwModalityListQuery(String hostName)
	{
		VistaQuery vm = new VistaQuery(RPC_DGW_MODALITY_LIST);
		vm.addParameter(VistaQuery.LITERAL, hostName);
		
		return vm;
	}

	public static VistaQuery createGetSourceAESecurityMatrix()
	{
		VistaQuery vm = new VistaQuery(RPC_GET_SRC_AE_SEC_MX);
		
		return vm;
	}
	
	public static VistaQuery createGetDGWEmailInfo(String hostName)
	{
		VistaQuery vm = new VistaQuery(RPC_MAGV_GET_DGW_EMAIL_INFO);
		vm.addParameter(VistaQuery.LITERAL, hostName);
		
		return vm;
	}
	
	public static VistaQuery createGetDgwUIDActionTableQuery(String type, String subType, String action)
	{
		VistaQuery vm = new VistaQuery(RPC_DGW_UID_ACTION_LIST);
		vm.addParameter(VistaQuery.LITERAL, type); // MAGTYPE
		vm.addParameter(VistaQuery.LITERAL, subType); // MAGSUBT
		vm.addParameter(VistaQuery.LITERAL, action); // MAGACT
		
		return vm;
	}
	
}
