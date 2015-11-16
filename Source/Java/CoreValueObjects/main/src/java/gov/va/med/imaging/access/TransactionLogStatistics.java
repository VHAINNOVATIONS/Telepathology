/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jul 1, 2008
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
package gov.va.med.imaging.access;


import gov.va.med.imaging.exchange.enums.ByteTransferType;


/**
 * @author VHAISWBECKEC
 *
 * A TransactionLogEntry derivation that may be used to build the
 * statistics of properties of some number of TransactionLogEntry instances.
 * Derivations of this class implement the specific statistic (arithmetic mean,
 * median, maxima, etc ...).  The getter accessor methods defined in the 
 * TransactionLogEntry interface return the statistic for the columns.
 * 
 * To use this class create an instance of the desired statistic, iterate
 * over the collection of TransactionLogEntry and call this method's 
 * update method with each TransactionLogEntry instance.
 */
public abstract class TransactionLogStatistics 
implements TransactionLogEntry
{	
	public abstract void update(TransactionLogEntry entry);

	/**
	 * Determine the number of bytes sent/received by a Vix operation based on the type of byte transfer.
	 * @param entry The Transaction Log entry.
	 * @param byteTransferType The type of byte transfer.
	 * @return the number of bytes sent/received.
	 */
	protected Long getByteCount (TransactionLogEntry entry,
			ByteTransferType    byteTransferType)
	{

		if(entry == null)
			return null;

		Long bytesTransferred = null;

		switch (byteTransferType)
		{
		case FACADE_BYTES_SENT:
			bytesTransferred = entry.getFacadeBytesSent ();
			break;

		case FACADE_BYTES_RECEIVED:
			bytesTransferred = entry.getFacadeBytesReceived ();
			break;

		case DATASOURCE_BYTES_SENT:
			bytesTransferred = entry.getDataSourceBytesSent ();
			break;

		case DATASOURCE_BYTES_RECEIVED:
			bytesTransferred = entry.getDataSourceBytesReceived ();
			break;

		default:
			bytesTransferred = null;
		break;
		}

		return bytesTransferred;

	} // getByteCount

	@Override
	public String getTransactionId()
	{
		return "Statistical";
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesSent()
	 */
	@Override
	public Long getFacadeBytesSent()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesReceived()
	 */
	@Override
	public Long getFacadeBytesReceived()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesSent()
	 */
	@Override
	public Long getDataSourceBytesSent()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesReceived()
	 */
	@Override
	public Long getDataSourceBytesReceived()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDatasourceProtocol()
	 */
	@Override
	public String getDatasourceProtocol()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getElapsedTime()
	 */
	@Override
	public Long getElapsedTime()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getErrorMessage()
	 */
	@Override
	public String getErrorMessage()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getExceptionClassName()
	 */
	@Override
	public String getExceptionClassName()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getItemCount()
	 */
	@Override
	public Integer getItemCount()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getModality()
	 */
	@Override
	public String getModality()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getOriginatingHost()
	 */
	@Override
	public String getOriginatingHost()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPatientIcn()
	 */
	@Override
	public String getPatientIcn()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPurposeOfUse()
	 */
	@Override
	public String getPurposeOfUse()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQuality()
	 */
	@Override
	public String getQuality()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryFilter()
	 */
	@Override
	public String getQueryFilter()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryType()
	 */
	@Override
	public String getQueryType()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRealmSiteNumber()
	 */
	@Override
	public String getRealmSiteNumber()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRequestingSite()
	 */
	@Override
	public String getRequestingSite()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getResponseCode()
	 */
	@Override
	public String getResponseCode()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getStartTime()
	 */
	@Override
	public Long getStartTime()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUrn()
	 */
	@Override
	public String getUrn()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUser()
	 */
	@Override
	public String getUser()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#isCacheHit()
	 */
	@Override
	public Boolean isCacheHit()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getCommandClassName()
	 */
	@Override
	public String getCommandClassName ()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getMachineName()
	 */
	@Override
	public String getMachineName ()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTimeToFirstByte()
	 */
	@Override
	public Long getTimeToFirstByte ()
	{
		return null;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getVixSoftwareVersion()
	 */
	@Override
	public String getVixSoftwareVersion()
	{
		return null;
	}

	/*
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRespondingSite()
	 */
	@Override
	public String getRespondingSite() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceItemsReceived()
	 */
	@Override
	public Integer getDataSourceItemsReceived() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#isAsynchronousCommand()
	 */
	@Override
	public Boolean isAsynchronousCommand() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getCommandId()
	 */
	@Override
	public String getCommandId() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getParentCommandId()
	 */
	@Override
	public String getParentCommandId() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRemoteLoginMethod()
	 */
	@Override
	public String getRemoteLoginMethod() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeImageFormatSent()
	 */
	@Override
	public String getFacadeImageFormatSent()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeImageQualitySent()
	 */
	@Override
	public String getFacadeImageQualitySent()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceImageFormatReceived()
	 */
	@Override
	public String getDataSourceImageFormatReceived()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceImageQualityReceived()
	 */
	@Override
	public String getDataSourceImageQualityReceived()
	{
		return null;
	}

	@Override
	public String getClientVersion()
	{
		return null;
	}

	@Override
	public String getDataSourceMethod()
	{
		return null;
	}

	@Override
	public String getDataSourceVersion()
	{
		return null;
	}

	@Override
	public String getDebugInformation()
	{
		return null;
	}

	@Override
	public String getDataSourceResponseServer()
	{
		return null;
	}

	@Override
	public String getThreadId()
	{
		return null;
	}

	@Override
	public String getVixSiteNumber()
	{
		return null;
	}

	@Override
	public String getRequestingVixSiteNumber()
	{
		return null;
	}
}
