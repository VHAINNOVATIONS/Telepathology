package gov.va.med.imaging.proxy;

import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Utilities
{
	
	private final static Logger logger = Logger.getLogger(Utilities.class); 
	
	/**
	 * Create a Url string for retrieving a photo Id for a specified patient and site
	 * @param proxyServices
	 * @param patientIcn
	 * @param siteNumber
	 * @return
	 * @throws ProxyServiceNotFoundException
	 */
	public static String createPhotoIdUrl(ProxyServices proxyServices, String patientIcn, String siteNumber)
	throws ProxyServiceNotFoundException
	{
		ProxyService service = proxyServices.getProxyService(ProxyServiceType.photo);
		return service.getConnectionURL() + "?" + 
			"patientIcn=" + patientIcn + 
			"&siteNumber=" + siteNumber;
	}
	
	
	/**
	 * Create a URL string in the form:
	 * <protocol>://<host>:<port>/<application>/<path>?imageURN=<imageUrn>
	 * Ex: http://localhost:8080/ImagingExchangeWebApp/xchange/xchange?imageURN=urn:vaimage:SLC-65532&imageQuality=nnn&contentType=xx/yy
	 * 
	 * @param imageUrn
	 * @return
	 */
	public static String createImageUrl(ProxyServices proxyServices,
			String imageUrn, 
			ImageFormatQualityList requestFormatQualityList,
			ProxyServiceType imageProxyServiceType)
	throws ProxyServiceNotFoundException
	{
		ProxyService service = proxyServices.getProxyService(imageProxyServiceType);
		int imageQuality = requestFormatQualityList.get(0).getImageQuality().getCanonical();
		
		return service.getConnectionURL() + "?" + 
		"imageURN=" + imageUrn +
		(imageQuality > 0 ? "&imageQuality=" + imageQuality : "") + 
		(requestFormatQualityList != null ? "&contentType=" + requestFormatQualityList.getAcceptString(false) : "");
	}
	
	public static String createTxtUrl(
			ProxyServices proxyServices,
			String imageUrn, 
			String accept,
			ProxyServiceType textProxyServiceType)
	throws ProxyServiceNotFoundException
	{
		ProxyService service = null;
		try
		{
			service = proxyServices.getProxyService(textProxyServiceType);
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			logger.debug("Cannot find proxy service for type '" + ProxyServiceType.text.getIdsOperationType() + "', using image");
			service = null;
		}
		if(service == null)
		{
			service = proxyServices.getProxyService(ProxyServiceType.image);
		}
		return service.getConnectionURL() + "?" + 
		"imageURN=" + imageUrn +
		(accept != null ? "&contentType=" + accept : "") + 
		("&textFile=true");
	}
	
	/**
	 * YYYYMMDDHHMMSS.FFFFFF+ZZZZ
	 * The components of this string, from left to right, are 
	 * YYYY = Year, 
	 * MM = Month, 
	 * DD = Day, 
	 * HH = Hour, 
	 * MM = Minute, 
	 * SS = Second,
	 * FFFFFF = Fractional Second, 
	 * “+” or “-” and ZZZZ = Hours and Minutes of offset.
	 * &ZZZZ is an optional suffix for plus/minus offset from Coordinated Universal Time.
	 */
	public static final String dicomDateFormat = "yyyyMMddHHmmss.SSSZ";
	
	/**
	 * Return a DateFormat instance that will format DICOM dates.
	 * NOTE: SimpleDateFormat is not a thread safe class so be careful
	 * re-using instances returned from here.
	 * 
	 * @return
	 */
	public static final DateFormat getDicomDateFormat()
	{
		return new SimpleDateFormat(dicomDateFormat);
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static String translateToDicomDate(DateFormat sourceDateFormat, String dateString) 
	throws ParseException
	{
		Date date = sourceDateFormat.parse(dateString);
		return toDicomDateFormat(date);
	}
	
	/**
	 * 
	 * @param date - a Date instance to convert to DICOM format
	 * @return - a DICOM formatted date or a zero-length string if the date is null
	 */
	public static String toDicomDateFormat(Date date)
	{
		return date == null ? "" : getDicomDateFormat().format(date);
	}
}
