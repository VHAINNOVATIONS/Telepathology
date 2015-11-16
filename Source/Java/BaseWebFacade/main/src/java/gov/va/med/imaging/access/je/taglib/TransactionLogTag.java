/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 28, 2008
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
package gov.va.med.imaging.access.je.taglib;

import gov.va.med.imaging.BaseWebFacadeRouter;

import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogMaxima;
import gov.va.med.imaging.access.TransactionLogMean;
import gov.va.med.imaging.access.TransactionLogMedian;
import gov.va.med.imaging.access.TransactionLogMinima;
import gov.va.med.imaging.access.TransactionLogSum;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.access.TransactionLogWriterHolder;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
//import gov.va.med.imaging.BaseWebFacadeRouter;
//import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.ByteTransferPath;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class TransactionLogTag 
extends BodyTagSupport 
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	private Enumeration<TransactionLogEntry> logEntryEnum;
	private TransactionLogEntry currentTransactionLogEntry;
	
	private Date startDate;
	private Date endDate;
	private ImageQuality quality = null;
	private String user;
	private String modality;
	private DatasourceProtocol datasourceProtocol = null;
	private String errorMessage;
	private String transactionId;
	private String imageUrn;
	private int startIndex = 0;
	private int endIndex = Integer.MAX_VALUE;
	private boolean forward = true;
	private String emptyResultMessage = null;
	private int requestedRowsPerPage = 0; // DKB
	private int actualRowsPerPage = 0; // DKB
	private boolean hasNextPage = false; // JMW - determines if there is a next page to be displayed
	
	private ByteTransferPath byteTransferPath = ByteTransferPath.DS_IN_FACADE_OUT;
	
	// the first date format in the array 
	private DateFormat[] dateFormats = new DateFormat[]
	{
		new SimpleDateFormat("MM/dd/yyyy"),
		new SimpleDateFormat("ddMMyyyy")
	};
	
	private String formatDate(Date date)
	{
        String stringDate = dateFormats[0].format(date);
        return stringDate;
	}
	
	private Date parseDate(String stringDate)
	{
		for(DateFormat df : dateFormats)
		{
			try
            {
	            Date date = df.parse(stringDate);
	            return date;
            } 
			catch (ParseException e)
            {
            }
		}
		return null;
	}
	
	/**
	 * 
	 */
	public TransactionLogTag()
	{
		
	}

	// =======================================================================
	public String getStartDateAsString()
    {
		if(startDate == null)
			return null;
    	return formatDate(startDate);
    }
	public void setStartDateAsString(String startDateAsString)
    {
		if(startDateAsString == null)
			startDate = null;
		else
			startDate = parseDate(startDateAsString);
    }
	public Date getStartDate()
    {
    	return startDate;
    }
	public void setStartDate(Date startDate)
    {
    	this.startDate = startDate;
    }

	// =======================================================================
	public String getEndDateAsString()
    {
		if(endDate == null)
			return null;
    	return formatDate(endDate);
    }
	public void setEndDateAsString(String endDateAsString)
    {
		if(endDateAsString == null)
			endDate = null;
		else
			endDate = parseDate(endDateAsString);
    }
	public Date getEndDate()
    {
    	return endDate;
    }
	public void setEndDate(Date endDate)
    {
    	this.endDate = endDate;
    }

	// =================================================================
	// The Image Quality may be set by Enum, name, ordinal value or qValue.
	// All of these properties are stored in the same field, so they will
	// overwrite one another.
	public ImageQuality getQuality()
    {
    	return quality;
    }
	public void setQuality(ImageQuality quality)
    {
    	this.quality = quality;
    }
	public String getQualityName()
    {
    	return quality.name();
    }
	public void setQualityName(String qualityName)
    {
		if(qualityName == null)
			this.quality = null;
        else
        {
	        try
            {
	            this.quality = ImageQuality.valueOf(qualityName);
            } 
			catch (RuntimeException e)
            {
				this.quality = null;
            }
        }
    }
	public int getQualityOrdinal()
    {
    	return quality == null ? -1 : quality.ordinal();
    }
	public void setQualityOrdinal(int ordinal)
    {
		this.quality = null;
		
		for(ImageQuality quality : ImageQuality.values())
			if(quality.ordinal() == ordinal)
				this.quality = quality;
    }

	public int getQualityValue()
    {
    	return quality == null ? 0 : quality.getCanonical();
    }
	public void setQualityValue(int qvalue)
    {
		this.quality = null;
		
		for(ImageQuality quality : ImageQuality.values())
			if( quality.isApplicableQuality(qvalue) )
				this.quality = quality;
    }
	
	// =================================================
	public String getUser()
    {
    	return user;
    }

	public void setUser(String user)
    {
    	this.user = user != null && user.length() == 0 ? null : user;
    }

	// =================================================================
	public String getModality()
    {
    	return modality;
    }

	public void setModality(String modality)
    {
    	this.modality = modality != null && modality.length() == 0 ? null : modality;
    }

	// =================================================================
	public DatasourceProtocol getDatasourceProtocol()
    {
    	return datasourceProtocol;
    }
	public void setDatasourceProtocol(DatasourceProtocol datasourceProtocol)
    {
    	this.datasourceProtocol = datasourceProtocol;
    }
	public int getDatasourceProtocolOrdinal()
    {
    	return datasourceProtocol == null ? -1 : datasourceProtocol.ordinal();
    }
	public void setDatasourceProtocolOrdinal(int ordinal)
    {
		this.datasourceProtocol = null;
		
		for(DatasourceProtocol datasourceProtocol : DatasourceProtocol.values())
			if(datasourceProtocol.ordinal() == ordinal)
				this.datasourceProtocol = datasourceProtocol;
    }
	public String getDatasourceProtocolAsString()
    {
    	return datasourceProtocol == null ? null : datasourceProtocol.name();
    }
	public void setDatasourceProtocolAsString(String datasourceProtocolAsString)
    {
		if(datasourceProtocolAsString == null)
			this.datasourceProtocol = null;
		else
			this.datasourceProtocol = DatasourceProtocol.valueOf(datasourceProtocolAsString);
    }
	
	/**
	 * Get/set the error message REGEX pattern.
	 * 
     * @return the errorMessage
     */
    public String getErrorMessage()
    {
    	return errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
    	this.errorMessage = errorMessage != null && errorMessage.length() == 0 ? null : errorMessage;
    }

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId != null && transactionId.length() == 0 ? null : transactionId;
	}

	/**
	 * @return the imageUrn
	 */
	public String getImageUrn() {
		return imageUrn;
	}

	/**
	 * @param imageUrn the imageUrn to set
	 */
	public void setImageUrn(String imageUrn) {
		this.imageUrn = imageUrn != null && imageUrn.length() == 0 ? null : imageUrn;
	}

	/**
	 * Get/set the starting index of log entries.
     * @return the startIndex
     */
    public int getStartIndex()
    {
    	return startIndex;
    }
    public void setStartIndex(int startIndex)
    {
    	this.startIndex = startIndex;
    }

	/**
	 * Get/set the ending index of log entries.
	 * If the endIndex is before the start index then no log entries will
	 * be returned.
     * @return the endIndex
     */
    public int getEndIndex()
    {
    	return endIndex;
    }
    public void setEndIndex(int endIndex)
    {
    	this.endIndex = endIndex;
    }

    /**
     * Controls whether the transaction log will be iterated in increasing (forward) or
     * decreasing date order.
     * 
     * @return
     */
	public boolean isForward()
    {
    	return forward;
    }

	public void setForward(boolean forward)
    {
    	this.forward = forward;
    }

	/**
	 * Get/set the message to show if the result set is empty (i.e. no log messages)
	 * By default this is null.
	 * 
	 * @return
	 */
	public String getEmptyResultMessage()
    {
    	return emptyResultMessage;
    }

	public void setEmptyResultMessage(String emptyResultMessage)
    {
    	this.emptyResultMessage = emptyResultMessage;
    }

	/**
	 * Get/set the path of byte transfer we're interested in.
	 * By default this is DS_IN_FACADE_OUT.
	 * 
	 * @return
	 */
	public ByteTransferPath getByteTransferPath ()
	{
		return byteTransferPath;
	}
	
	public void setByteTransferPath (ByteTransferPath byteTransferPath)
	{
		this.byteTransferPath = byteTransferPath;
	}
	
	/**
     * @return the logEntryEnum
     */
    Enumeration<TransactionLogEntry> getLogEntryEnum()
    {
    	return logEntryEnum;
    }

	/**
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
    public int doStartTag() 
	throws JspException
    {
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setRequestType("View Transaction Log");
		transactionContext.setServicedSource(transactionContext.getLoggerSiteNumber());
		hasNextPage = false;
		
		requestedRowsPerPage = endIndex - startIndex;
		if (requestedRowsPerPage < 0) requestedRowsPerPage *= -1;
		actualRowsPerPage = 0;
		
		try
		{
			BaseWebFacadeRouter router = FacadeRouterUtility.getFacadeRouter (BaseWebFacadeRouter.class);
			TransactionLogWriterHolder logHolder = new TransactionLogWriterHolder();
			router.getTransactionLogEntries(logHolder, 	        
	               getStartDate(), getEndDate(), 
	               quality, getUser(), getModality(), datasourceProtocol, getErrorMessage(),
	               getImageUrn(), getTransactionId(), new Boolean (isForward()), 
	               getStartIndex(), getEndIndex());
			List<TransactionLogEntry> logEntryList = logHolder.getEntries();
	        java.util.Vector<TransactionLogEntry> vLogEntries = new java.util.Vector<TransactionLogEntry> ();
			for(int i = 0; i < logEntryList.size(); i++)
	        {
	        	 // the result from the router will include the range requested + 1, if there is +1 that means
	        	 // there is more data so hasNextPage should be true.  We don't actually want to display this +1 entry
	        	 // so exclude it from the results.
	        	 TransactionLogEntry logEntry = logEntryList.get(i);
	        	 if(i >= requestedRowsPerPage)
	        	 {
	        		 hasNextPage = true;
	        	 }
	        	 else
	        	 {
	        		 vLogEntries.add(logEntry);
	        	 }
	         }
	         logEntryEnum = vLogEntries.elements ();
		}
		
		catch (Exception x)
		{
         logger.error ("Error:", x);
         throw new JspException("Unable to acquire a reference to the TransactionLogger.");
		}
      
		initializeStatistics();
		currentTransactionLogEntry = null;
	    if( getLogEntryEnum() != null && getLogEntryEnum().hasMoreElements() )
	    {
	    	// position to the start index
	    	/*
	    	for(currentTransactionLogEntry = getLogEntryEnum().nextElement();
	    		currentTransactionLogEntry != null; 
	    		currentTransactionLogEntry = getLogEntryEnum().nextElement())
	    			++currentIndex;
	    	*/
	    	
	    	// position to the start index
	    	// JMW 3/1/10 - no longer need to position because the results are always the exact requested range of data
	    	/*
	    	for(currentTransactionLogEntry = getLogEntryEnum().nextElement();
	    		currentTransactionLogEntry != null && currentIndex < getStartIndex(); 
	    		currentTransactionLogEntry = getLogEntryEnum().nextElement())
	    	{
	    			if (getLogEntryEnum().hasMoreElements() == false) // DKB
	    			{
	    				currentTransactionLogEntry = null;
	    				break;
	    			}
	    			++currentIndex;
	    	}*/
	    	
	    	// get first element
	    	currentTransactionLogEntry = getLogEntryEnum().nextElement();
	    	
	    	// if we have not positioned beyond the end of the log entries
	    	// update the statistics gathering
	    	if(currentTransactionLogEntry != null)
	    		updateStatistics(currentTransactionLogEntry);
	    }
	    if(currentTransactionLogEntry == null)
	    {
	    	if(getEmptyResultMessage() != null)
	    		try{pageContext.getOut().write(getEmptyResultMessage());}
	    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}
	    	return BodyTag.SKIP_BODY;
	    }
	    else
	    	return BodyTag.EVAL_BODY_INCLUDE;
    }

	/**
     * @see gov.va.med.imaging.access.je.taglib.TransactionLogEnumerationParent#getCurrentTransactionLogEntry()
     */
   public TransactionLogEntry getCurrentTransactionLogEntry()
	{
		return currentTransactionLogEntry;
	}
	
	/**
	 * Iterate to the next log entry element.
	 * @return
	 */
	boolean nextLogEntryElement()
	{
		if( getLogEntryEnum() != null && getLogEntryEnum().hasMoreElements())
	    {
	    	currentTransactionLogEntry = getLogEntryEnum().nextElement();
	    	updateStatistics(currentTransactionLogEntry);
	    	return true;
	    }
	    return false;
	}
	
	public void incrementPerPageRowCount()
	{
		actualRowsPerPage++;
	}
	
	/**
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
    public int doAfterBody() 
	throws JspException
    {
    	return BodyTag.SKIP_BODY;
    }

	/**
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
    public int doEndTag() 
	throws JspException
    {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<script>");
//		
//		// My apologies - DKB
//		if (startIndex == 0) // disable rewind and previous page buttons
//		{
//			sb.append("document.all.vcrRewind.disabled = 'disabled';");
//			sb.append("document.all.vcrPrevPage.disabled = 'disabled';");
//		}
//		
//		// disable next page button
//		if ((requestedRowsPerPage == actualRowsPerPage && getLogEntryEnum().hasMoreElements() == false) || actualRowsPerPage < requestedRowsPerPage )
//		{
//			sb.append("document.all.vcrNextPage.disabled = 'disabled';");
//		}
//		sb.append("</script>");
//		
//		String jscriptFragment = sb.toString();
//		if (jscriptFragment.compareTo("<script></script>") != 0)
//		{
//			try
//			{
//				pageContext.getOut().write(jscriptFragment);
//			}
//			catch(IOException ioX){;} // not an error if the buttons don't get disabled
//		}

		if (startIndex == 0)
		{
			pageContext.setAttribute("hasPreviousPage", false);
		}
		else
		{
			pageContext.setAttribute("hasPreviousPage", true);
		}
		pageContext.setAttribute("hasNextPage", hasNextPage);
		
		logEntryEnum = null;
		currentTransactionLogEntry = null;
		
	    return BodyTag.EVAL_PAGE;
    }
	
	// =====================================================================================
	//
	// =====================================================================================
	private TransactionLogMaxima maximumValues;
	private TransactionLogMinima minimumValues;
	private TransactionLogMean meanValues;
	private TransactionLogMedian medianValues;
	private TransactionLogSum sumValues;
	
	private void initializeStatistics()
    {
		maximumValues = new TransactionLogMaxima(byteTransferPath);
		minimumValues = new TransactionLogMinima(byteTransferPath);
		meanValues = new TransactionLogMean(byteTransferPath);
		medianValues = new TransactionLogMedian(byteTransferPath);
		sumValues = new TransactionLogSum(byteTransferPath);
    }

	private void updateStatistics(TransactionLogEntry logEntry)
    {
		maximumValues.update(logEntry);
		minimumValues.update(logEntry);
		meanValues.update(logEntry);
		medianValues.update(logEntry);
		sumValues.update(logEntry);
    }

	/**
     * @return the maximumValues
     */
    TransactionLogMaxima getMaximumValues()
    {
    	return maximumValues;
    }

	/**
     * @return the minimumValues
     */
    TransactionLogMinima getMinimumValues()
    {
    	return minimumValues;
    }

	/**
     * @return the meanValues
     */
    TransactionLogMean getMeanValues()
    {
    	return meanValues;
    }

	/**
     * @return the medianValues
     */
    TransactionLogMedian getMedianValues()
    {
    	return medianValues;
    }
    
    TransactionLogSum getSumValues()
    {
    	return sumValues;
    }
    
}
