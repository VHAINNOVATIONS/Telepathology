package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.url.vista.StringUtils;

public class DicomUtils 
{
	public static String reformatDicomName(String dicomName) 
	{
		if (dicomName == null)
			return "";
		
		String[] nameParts = StringUtils.Split(dicomName, StringUtils.CARET);
		
		if (nameParts.length == 1)
		{
			return nameParts[0];
		}
		else
		{
			String name = "";
			for (int i=0; i<nameParts.length; i++)
			{
				name += nameParts[i];
				
				if (i == 0)
				{
					name += ",";
				}
				
				name += " ";
			}
			
			return name.trim();
		}
	}
	
	public String getServiceFromAccessionNumber(String accessionNumber) {
		String service = "RAD";
		
		accessionNumber.toUpperCase();
		if (accessionNumber.contains("GMR"))
		{
			service = "CON";
		}
		else if (accessionNumber.startsWith("SP") || accessionNumber.startsWith("EM") || accessionNumber.startsWith("CY")) 
		{ 
			service = "LAB";
		}
		return service;
	}
}
