/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 15, 2008
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
package gov.va.med.imaging.exchange.business.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import gov.va.med.imaging.exchange.business.Site;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ExchangeUtil {
	
	private final static String DOD_SITE_NUMBER = "200";
	private final static Logger logger = Logger.getLogger(ExchangeUtil.class);
	
	/**
	 * Returns the designated site number for the DOD
	 * @return
	 */
	public final static String getDodSiteNumber()
	{
		return DOD_SITE_NUMBER;
	}

	/**
	 * Determines if the site number represents a DOD site
	 * @param siteNumber Site number to examine
	 * @return True if DOD, false otherwise
	 */
	public static boolean isSiteDOD(String siteNumber)
	{
		if(DOD_SITE_NUMBER.equals(siteNumber))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if the site object is for a DOD site
	 * @param site Site to examine
	 * @return True if DOD, false otherwise
	 */
	public static boolean isSiteDOD(Site site)
	{
		if(site == null)
			return false;
		return isSiteDOD(site.getSiteNumber());
	}
	
	/**
	 * Determines the string format of the date based on the length of the date. Assumes date is in a DICOM format but not sure how many levels of precision it contains
	 * @param date DICOM date with unknown amount of precision
	 * @return A formatter string for parsing the date 
	 */
	private static String getDateFormat(String date) {
		if(date == null)
			return "";
		switch(date.length()) {
			case 4:
				return "yyyy";
			case 6:
				return "yyyyMM";
			case 8:
				return "yyyyMMdd";
			case 10:
				return "yyyyMMddHH";
			case 12:
				return "yyyyMMddHHmm";
			case 14:
				return "yyyyMMddHHmmss";
			default:
				return "yyyyMMddHHmmss";				
		}			
	}
	
	public static Date convertDICOMDateToDate(String dicomDate)
	{
		if((dicomDate == null) || (dicomDate.equals(""))) {
			return null;// Date();
		}
		if(dicomDate.length() < 8) {
			return null;
		}
		
		//TODO: update this function to handle if only part of the date is given (no month, etc)
		//TODO: month and day are now required, do a check for length and parse on that
		//TODO: if the date is invalid, should this throw an exception or always get full list of studies?
		//String dicomDate = "20061018143643.655321+0200";
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
		
		String format = getDateFormat(dicomDate);
		if("".equals(format))
			return null;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		Date d = null;
		try 
		{
			d = sdf.parse(dicomDate);
			return d;
		}
		catch(ParseException pX) {
			logger.error(pX);
			return null;	
		}
	}

}
