/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Aug 1, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.transactioncontext;

/**
 * A Java Bean compliant class that gives access to some of the properties of the TransactionContext.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TransactionContextBean 
{
	private TransactionContext transactionContext;
	public TransactionContextBean()
	{
		transactionContext = TransactionContextFactory.get();
	}
	
	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeBytesSent()
	 */
	public Long getFacadeBytesSent()
	{
		return transactionContext == null ? null : transactionContext.getFacadeBytesSent();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeBytesReceived()
	 */
	public Long getFacadeBytesReceived()
	{
		return transactionContext == null ? null : transactionContext.getFacadeBytesReceived();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceBytesSent()
	 */
	public Long getDataSourceBytesSent()
	{
		return transactionContext == null ? null : transactionContext.getDataSourceBytesSent();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceBytesReceived()
	 */
	public Long getDataSourceBytesReceived()
	{
		return transactionContext == null ? null : transactionContext.getDataSourceBytesReceived();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDatasourceProtocol()
	 */
	public String getDatasourceProtocol()
	{
		return transactionContext == null ? null : transactionContext.getDatasourceProtocol();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDuration()
	 */
	public Long getDuration()
	{
		return transactionContext == null ? null : transactionContext.getDuration();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getEntriesReturned()
	 */
	public Integer getEntriesReturned()
	{
		return transactionContext == null ? null : transactionContext.getEntriesReturned();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return transactionContext == null ? null : transactionContext.getErrorMessage();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getExceptionClassName()
	 */
	public String getExceptionClassName()
	{
		return transactionContext == null ? null : transactionContext.getExceptionClassName();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getModality()
	 */
	public String getModality()
	{
		return transactionContext == null ? null : transactionContext.getModality();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getQuality()
	 */
	public String getQuality()
	{
		return transactionContext == null ? null : transactionContext.getQuality();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRealm()
	 */
	public String getRealm()
	{
		return transactionContext == null ? null : transactionContext.getRealm();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRequestType()
	 */
	public String getRequestType()
	{
		return transactionContext == null ? null : transactionContext.getRequestType();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getResponseCode()
	 */
	public String getResponseCode()
	{
		return transactionContext == null ? null : transactionContext.getResponseCode();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getServicedSource()
	 */
	public String getServicedSource()
	{
		return transactionContext == null ? null : transactionContext.getServicedSource();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSiteName()
	 */
	public String getSiteName()
	{
		return transactionContext == null ? null : transactionContext.getSiteName();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSiteNumber()
	 */
	public String getSiteNumber()
	{
		return transactionContext == null ? null : transactionContext.getSiteNumber();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getStartTime()
	 */
	public Long getStartTime()
	{
		return transactionContext == null ? null : transactionContext.getStartTime();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getTransactionId()
	 */
	public String getTransactionId()
	{
		return transactionContext == null ? null : transactionContext.getTransactionId();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getUrn()
	 */
	public String getUrn()
	{
		return transactionContext == null ? null : transactionContext.getUrn();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isItemCached()
	 */
	public Boolean isItemCached()
	{
		return transactionContext == null ? null : transactionContext.isItemCached();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getMachineName()
	 */
	public String getMachineName()
	{
		return transactionContext == null ? null : transactionContext.getMachineName();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getCommandClassName()
	 */
	public String getCommandClassName()
	{
		return transactionContext == null ? null : transactionContext.getCommandClassName();
	}

	/**
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getTimeToFirstByte()
	 */
	public Long getTimeToFirstByte()
	{
		return transactionContext == null ? null : transactionContext.getTimeToFirstByte();
	}
	
   /**
    * @see gov.va.med.imaging.transactioncontext.TransactionContext#getVixSoftwareVersion()
    */
   public String getVixSoftwareVersion()
   {
      return transactionContext == null ? null : transactionContext.getVixSoftwareVersion();
   }
}
