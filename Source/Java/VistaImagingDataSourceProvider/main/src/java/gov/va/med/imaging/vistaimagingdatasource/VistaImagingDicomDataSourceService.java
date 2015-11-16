package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.DicomDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.dicom.DicomGatewayConfiguration;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.protocol.vista.exceptions.DICOMGatewayConfigurationException;
import gov.va.med.imaging.protocol.vista.exceptions.ResultSetException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * An implementation of a DicomDataSourceSpi that talks to VistA.
 * 
 * NOTE: 1.) public methods that do Vista access (particularly anything defined
 * in the DicomDataSourceSpi interface) must acquire a VistaSession instance
 * using getVistaSession(). 2.) private methods which are only called from
 * public methods that do Vista access must include a VistaSession parameter,
 * they should not acquire their own VistaSession 3.) Where a method is both
 * public and called from within this class, there should be a public version
 * following rule 1, calling a private version following rule 2.
 * 
 * @author vhaiswlouthj
 * 
 */
public class VistaImagingDicomDataSourceService
extends AbstractVersionableDataSource
implements DicomDataSourceSpi
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	// TODO CHANGE TO 3.0P55 private final static String MAG_REQUIRED_VERSION =
	// "3.0P55";
	public final static String MAG_REQUIRED_VERSION = "3.0P83";

	private static final String CRLF = "\r\n";
	private static final String CR = "\r";
	private static String MAXRETURN = "500";

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // for
	// DICOM
	// UID
	// generation
	// only
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;

	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingDicomDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	// to support local data source
	public VistaImagingDicomDataSourceService(ResolvedArtifactSource resolvedArtifactSource)
	{
		super(resolvedArtifactSource, SUPPORTED_PROTOCOL);
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
	

	private VistaSession getVistaSession() throws IOException, ConnectionException, MethodException
	{
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	@Override
	public boolean isVersionCompatible()
	throws SecurityCredentialsExpiredException
	{
		return true;
	}

	

	private HashMap<String, String> getVistaRepresentation(HashMap<String, String> request)
	{
		HashMap<String, String> tagparam = new HashMap<String, String>();
		Set<String> keylist = request.keySet();
		Object keys[] = keylist.toArray();
		for (int i = 0; i < keylist.size(); i++)
		{
			String key = String.valueOf(i + 1);
			String value = String.format("{0}|1|1|{1}", keys[i], request.get(keys[i]));

			logger.debug("Add " + key + " = " + value);
			tagparam.put(key, value);
		}

		return tagparam;
	}

	private int populateHeader(HashMap<String, String> header, Object[] keys)
	{
		logger.debug("Empty result set, creating pro-forma header");
		for (int i = 0; i < keys.length; i++)
		{
			String columnNumber = String.valueOf(i + 1);
			header.put(columnNumber, keys[i].toString());
			logger.debug("Column " + columnNumber + " = " + keys[i]);
		}

		return keys.length;
	}

	private void sleep()
	{
		// Sleep half a second, then try again
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String executeVistaQuery(VistaSession localVistaSession, VistaQuery vm) throws ResultSetException
	{
		String rtn;
		rtn = null;
		try
		{
			rtn = localVistaSession.call(vm);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			throw new ResultSetException(ex.getMessage());
		}

		if (rtn == null)
		{
			throw new ResultSetException("No data returned from RPC Call.");
		}

		logQueryResults(rtn);
		return rtn;
	}

	private void logQueryResults(String rtn)
	{
		String logMessage = String.format("{0} returns [{1}]", VistaImagingDicomQueryFactory.CFIND_QUERY, DicomTranslatorUtility.displayEncodedChars(rtn));

		logger.debug(logMessage);

	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws VistaException
	 */
	public boolean disconnect() throws IOException
	{
		// don;t do anything, let the connection timeout
		return true;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.dicom.common.interfaces.DicomConfigurationFacade#isModalityDeviceAuthenticated(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean isModalityDeviceAuthenticated(String manufacturer, String model, String softwareVersion) throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void loadDicomGatewayConfig(String hostName) throws MethodException, ConnectionException
	{
		int status = 0;
		String line = null;
		String key = null;
		VistaSession localVistaSession = null;

		try{

			localVistaSession = getVistaSession();

			if (DicomGatewayConfiguration.getInstance().getHostName() == null){	

				DicomGatewayConfiguration.getInstance().setHostName(hostName);
				logger.debug("Get information for gateway " + DicomGatewayConfiguration.getInstance().getHostName() + ".");
				VistaQuery vm = new VistaQuery(VistaImagingDicomQueryFactory.GET_GATEWAY_INFO);
				vm.addParameter(VistaQuery.LITERAL, DicomGatewayConfiguration.getInstance().getHostName());
				String rtn = null;
				try{
					rtn = localVistaSession.call(vm);
				}
				catch (Exception ex){
					logger.error(ex.getMessage());
					throw new DICOMGatewayConfigurationException(ex);
				}
				if (rtn == null){
					throw new DICOMGatewayConfigurationException();
				}
				logger.info(VistaImagingDicomQueryFactory.GET_GATEWAY_INFO + " returns [" + StringUtils.displayEncodedChars(rtn) + "]");
				status = DicomTranslatorUtility.intVal(rtn);
				if (status < 0){
					logger.error(VistaImagingDicomQueryFactory.GET_GATEWAY_INFO + ": [" + StringUtils.displayEncodedChars(rtn) + "]");
					logger.error("Cannot obtain Gateway Information:\n" + rtn);
				}
				else{
					for (int t = 2; t <= status; t++){
						line = StringUtils.Piece(rtn, "\r\n", t);
						key = StringUtils.Piece(line, "=", 1);
						logger.debug("Property: " + line);

						if (key.compareTo("Loc") == 0)
							DicomGatewayConfiguration.getInstance().setLocation(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("In_s") == 0)
							DicomGatewayConfiguration.getInstance().setInstrumentTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("In_p") == 0)
							DicomGatewayConfiguration.getInstance().setInstrumentFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Mo_s") == 0)
							DicomGatewayConfiguration.getInstance().setModalityTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Mo_p") == 0)
							DicomGatewayConfiguration.getInstance().setModalityFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("PL_s") == 0)
							DicomGatewayConfiguration.getInstance().setPortListTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Pl_p") == 0)
							DicomGatewayConfiguration.getInstance().setPortListFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("SL_s") == 0)
							DicomGatewayConfiguration.getInstance().setSCUListTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("SL_p") == 0)
							DicomGatewayConfiguration.getInstance().setSCUListFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("WL_s") == 0)
							DicomGatewayConfiguration.getInstance().setWorkListTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("WL_p") == 0)
							DicomGatewayConfiguration.getInstance().setWorkListFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("CP_s") == 0)
							DicomGatewayConfiguration.getInstance().setCTParametersTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("CP_p") == 0)
							DicomGatewayConfiguration.getInstance().setCTParametersFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Ver") == 0)
							DicomGatewayConfiguration.getInstance().setGatewayVersion(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Vw_s") == 0)
							DicomGatewayConfiguration.getInstance().setDICOMViewerTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Vw_p") == 0)
							DicomGatewayConfiguration.getInstance().setDICOMViewerFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Ab_s") == 0)
							DicomGatewayConfiguration.getInstance().setMakeAbstractTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Ab_p") == 0)
							DicomGatewayConfiguration.getInstance().setMakeAbstractFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("CS_s") == 0)
							DicomGatewayConfiguration.getInstance().setCStoreTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("CS_p") == 0)
							DicomGatewayConfiguration.getInstance().setCStoreFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Rc_s") == 0)
							DicomGatewayConfiguration.getInstance().setReconstructorTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("Rc_p") == 0)
							DicomGatewayConfiguration.getInstance().setReconstructorFileName(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("DT_s") == 0)
							DicomGatewayConfiguration.getInstance().setDICOMtoTargaTimeStamp(StringUtils.Piece(line, "=", 2));
						if (key.compareTo("DT_p") == 0)
							DicomGatewayConfiguration.getInstance().setDICOMtoTargaFileName(StringUtils.Piece(line, "=", 2));
					}
				}
			}
		}
		catch (IOException e){
			throw new ConnectionException(e);
		}
		catch (Exception e){
			logger.warn("Did not load DICOM Gateway Configuration.");
			logger.warn("Reason unknown.");
		}
		finally{
			try{
				localVistaSession.close();
			}
			catch (Throwable t){
			
			}
		}
	}

	@Override
	@Deprecated
	public String getStudyDetails(String studyId) throws MethodException, ConnectionException
	{
		VistaSession localVistaSession = null;
		String studyDetails = null;

		try
		{
			localVistaSession = getVistaSession();
			VistaQuery query = new VistaQuery(VistaImagingDicomQueryFactory.STUDY_UID_QUERY);
			query.addParameter(VistaQuery.LITERAL, studyId);
			studyDetails = localVistaSession.call(query);

			logger.info(VistaImagingDicomQueryFactory.STUDY_UID_QUERY + " returns [" + StringUtils.displayEncodedChars(studyDetails) + "]");

		}
		catch (VistaMethodException mX)
		{
			throw new MethodException(mX);
		}
		catch (InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e);
		}
		finally
		{
			try
			{
				localVistaSession.close();
			}
			catch (Throwable t)
			{
			}
		}

		return studyDetails;
	}

	@Override
	@Deprecated
	public String getImageDetails(String image) throws MethodException, ConnectionException
	{

		VistaSession localVistaSession = null;
		String imageDetails = null;

		try
		{
			localVistaSession = getVistaSession();
			VistaQuery query = new VistaQuery(VistaImagingDicomQueryFactory.GET_CURRENT_IMAGE_INFO);
			query.addParameter(VistaQuery.LITERAL, image);
			imageDetails = localVistaSession.call(query);
			logger.info(VistaImagingDicomQueryFactory.GET_CURRENT_IMAGE_INFO + " returns [" + StringUtils.displayEncodedChars(imageDetails) + "]");

		}
		catch (VistaMethodException mX)
		{
			throw new MethodException(mX);
		}
		catch (InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e);
		}
		finally
		{
			try
			{
				localVistaSession.close();
			}
			catch (Throwable t)
			{
			}
		}

		return imageDetails;

	}
}
