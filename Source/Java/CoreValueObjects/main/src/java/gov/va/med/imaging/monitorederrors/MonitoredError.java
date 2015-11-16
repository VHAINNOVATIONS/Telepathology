/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 28, 2013
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
package gov.va.med.imaging.monitorederrors;

import java.util.Date;

/**
 * @author VHAISWWERFEJ
 *
 */
public class MonitoredError
{
	private final String errorMessageContains;
	private long count;
	private Date lastOccurrence;
	private boolean active;

	/**
	 * @param errorMessageContains
	 */
	public MonitoredError(String errorMessageContains, boolean active)
	{
		super();
		this.count = 0L;
		this.lastOccurrence = null;
		this.active = active;
		this.errorMessageContains = errorMessageContains;
	}

	/**
	 * @return the errorMessageContains
	 */
	public String getErrorMessageContains()
	{
		return errorMessageContains;
	}
	
	public void incrementErrorCount()
	{
		count++;
		lastOccurrence = new Date();
	}

	/**
	 * @return the count
	 */
	public long getCount()
	{
		return count;
	}

	/**
	 * @return the lastOccurrence
	 */
	public Date getLastOccurrence()
	{
		return lastOccurrence;
	}
	
	public String getLastOccurrenceString()
	{
		return (lastOccurrence == null ? "never" : lastOccurrence.toString());
	}

	/**
	 * @return the active
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public boolean isErrorMsgMonitored(String errorMsg)
	{
		if(errorMsg == null)
			return false;
		return errorMsg.contains(this.errorMessageContains);
	}
}
