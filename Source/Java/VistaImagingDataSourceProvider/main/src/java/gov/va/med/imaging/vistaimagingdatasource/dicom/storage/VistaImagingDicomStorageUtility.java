package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.business.dicom.UIDCheckResult;
import gov.va.med.imaging.url.vista.StringUtils;

import org.apache.log4j.Logger;

public class VistaImagingDicomStorageUtility
{
	private static Logger logger = Logger.getLogger(VistaImagingDicomStorageUtility.class);


	public static UIDCheckResult translateUIDCheckResults(String returnValue, String originalUID, String fieldSeparator)
	{
		UIDCheckResult result = null;

		if (returnValue == null) 
		{
			result = UIDCheckResult.createResultFatalError();
		}
		else
		{
			returnValue = returnValue.trim();

			if (returnValue.equals(UIDCheckResult.NODUPLICATEFOUND))
			{
				// normal case
				result = UIDCheckResult.createResultNoDuplicateFound(originalUID);
			}
			else if (returnValue.startsWith(UIDCheckResult.DUPLICATEFOUND))
			{
				// Duplicate found: Parse out the new value
				String cleanUID = StringUtils.Split(returnValue, fieldSeparator)[2];
				result = UIDCheckResult.createResultDuplicateFound(originalUID, cleanUID);
			}
			else if (returnValue.startsWith(UIDCheckResult.ILLEGALUID) )
			{
				// illegal UID found: Parse out the new value
				String cleanUID = StringUtils.Split(returnValue, fieldSeparator)[2];
				result = UIDCheckResult.createResultIllegalUIDFound(originalUID, cleanUID);
			}
			else if (returnValue.startsWith(UIDCheckResult.SOPINSTANCERESEND))
			{
				result = UIDCheckResult.createResultSOPInstanceResendFound(originalUID);
			}
			else // -1 (fatal Error) received
			{
				result = UIDCheckResult.createResultFatalError();
			}
		}
		
		return result;
	}
}
