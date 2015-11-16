package gov.va.med.imaging.protocol.vista;

import gov.va.med.imaging.exchange.business.dicom.CFindResults;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.exchange.business.dicom.ModalityConfig;
// import gov.va.med.imaging.exchange.business.dicom.SourceAESecurityConfig;
import gov.va.med.imaging.exchange.business.dicom.DGWEmailInfo;
import gov.va.med.imaging.exchange.business.dicom.UIDActionConfig;
import gov.va.med.imaging.protocol.vista.exceptions.ResultSetException;
import gov.va.med.imaging.url.vista.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class VistaImagingDicomTranslator
{
	private static Logger logger = Logger.getLogger(VistaImagingDicomTranslator.class);

	public static List<InstrumentConfig> translateInstrumentList(String returnValue)
	{
		List<InstrumentConfig> instruments = new ArrayList<InstrumentConfig>();
		
		String[] rawInstruments = DicomTranslatorUtility.createResultsArrayStrippingHeaderLines(returnValue, 2);
		for (String rawInstrument : rawInstruments)
		{
			InstrumentConfig instrument = new InstrumentConfig();

			String[] fields = StringUtils.Split(rawInstrument, StringUtils.CARET);
			instrument.setHostName(fields[0]);
			instrument.setConfigTimestamp(fields[1]);
			instrument.setNickName(fields[2]);
			instrument.setDescription(fields[3]);
			instrument.setService(fields[4]);
			instrument.setPort(new Integer(fields[5]));
			instrument.setSiteId(fields[6]);
			instrument.setSite(fields[7]);
			instrument.setMachineId(fields[8]);
			
			String instrumentMachineID = instrument.getMachineId();
			String serverHostname = DicomServerConfiguration.getConfiguration().getHostName();
			if(instrumentMachineID != null && instrumentMachineID.length()>0){
				instrumentMachineID = instrumentMachineID.trim();
			}
			if(serverHostname != null && serverHostname.length()>0){
				serverHostname = serverHostname.trim();
			}				
			if(instrumentMachineID.equalsIgnoreCase(serverHostname)){	
				instruments.add(instrument);
			}
			
			if(instrument.getSiteId() == null || instrument.getSiteId().equals("")){
				instrument.setSiteId(DicomServerConfiguration.getConfiguration().getSiteId());
			}

		}
		return instruments;
	}

	public static List<ModalityConfig> translateModalityList(String returnValue)
	{
		List<ModalityConfig> modalities = new ArrayList<ModalityConfig>();
		
		String[] rawModalities = DicomTranslatorUtility.createResultsArrayStrippingHeaderLines(returnValue, 2);
		for (String rawModality : rawModalities)
		{
			ModalityConfig modality = new ModalityConfig();

			String[] fields = StringUtils.Split(rawModality, StringUtils.CARET);
			modality.setHostName(fields[0]);
			modality.setConfigTimestamp(fields[1]);
			modality.setManufacturer(fields[2]);
			modality.setModel(fields[3]);
			modality.setModality(fields[4]);
			modality.setImagingService(fields[5]);
			modality.setActive(new Boolean(fields[6]));
			
			modalities.add(modality);
		}
		
		return modalities;
	}

	public static DGWEmailInfo translateDGWEmailInfo(String returnValue)
	{
		DGWEmailInfo dgwEMI = new DGWEmailInfo("","","","", null,"");
		
		String[] fields = StringUtils.Split(returnValue, StringUtils.TILDE);
		if (fields.length > 6) { // prevent exception
			//Note counting starts from 1 because same separator used for result and data
			dgwEMI.setHostName(fields[1]);
			dgwEMI.setEMailAddress(fields[2]);
			dgwEMI.setSmtpAddress(fields[3]);
			dgwEMI.setSmtpPort(fields[4]);
			dgwEMI.setImporterRunning(fields[5].startsWith("Y"));
			dgwEMI.setDgwSiteID(fields[6]);
		}
				
		return dgwEMI;
	}

	public static List<UIDActionConfig> translateUIDActions(String returnValue)
	{
		List<UIDActionConfig> uidActions = new ArrayList<UIDActionConfig>();
		
		String[] rawUIDActions = DicomTranslatorUtility.createResultsArrayStrippingHeaderLines(returnValue, 2);
		for (String rawUIDAction : rawUIDActions)
		{
			UIDActionConfig uidAction = new UIDActionConfig();

			String[] fields = StringUtils.Split(rawUIDAction, StringUtils.CARET);
			uidAction.setUid(fields[0]);
			uidAction.setDescription(fields[1]);
			uidAction.setActionCode(fields[2]);
			uidAction.setActionComment(fields[3]);

			uidActions.add(uidAction);
		}
		
		return uidActions;
	}

	@Deprecated
	public static void addCFindResults(String rtn, CFindResults findResults, int elementCount)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * This method parses the results of the CFind MUMPS routine to determine whether
	 * or not the query had any results.
	 * 
	 * @param rtn
	 * @param count
	 * @return
	 * @throws ResultSetException
	 */
	@Deprecated
	public static boolean isResultSetEmpty(String rtn, int count) throws ResultSetException
	{
		boolean isEmpty = true;
		boolean errrtn = false;
		for (int i = 0; i < count; i++)
		{
			String line = StringUtils.Piece(rtn, "\n", i + 2);
			if (line.length() < 19)
				isEmpty = false;

			if ((line.length() > 18) && !(line.substring(0, 18).equals("0000,0902^No match")))
				isEmpty = false;

			if ((line.length() > 9) && (line.substring(0, 9).equals("0000,0902")))
			{
				boolean ok = false;
				if ((line.length() > 18) && (line.substring(0, 19).equals("0000,0902^Result # ")))
					ok = true;

				if (!ok)
					errrtn = true;
			}

		}

		if ((!isEmpty) & (errrtn))
		{
			throw new ResultSetException(rtn);
		}

		return isEmpty;
		
	}
	
	/**
	 * Responsible for parsing the results of a CFind MUMPS result into a CFindResults instance.
	 * Note that this class expects the complete results string, not portions of it.
	 * 
	 * @param resultSetString
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public static CFindResults createCFindResults(String resultSetString) throws SQLException
	{
		CFindResults cFindResults = new CFindResults();
		return cFindResults;
	}

}
