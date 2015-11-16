/**
 * 
 */
package gov.va.med.imaging.proxy.exchange;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.Requestor;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.proxy.AbstractRequestParameters;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * A simple serializable class of the parameters to a getStudies request.
 * 
 * As a derivation of AbstractRequestParameters, this class always has a 
 * Transaction ID once it has been created.
 * If one is not supplied in the constructor then one is created in the
 * constructor.  Once created the transaction ID is immutable. 

 * @author VHAISWBECKEC
 *
 */
public class StudyParameters
extends AbstractRequestParameters
implements Serializable
{
	private static final long serialVersionUID = -5323352387264924859L;
	public final static String defaultDatasource = "200";
	
	private Requestor requestor;
	private String patientId;
	private StudyFilter filter;
	private String datasource;
    
    /**
     * Required default, no-arg constructor for XMLEncoder
     *
     */
	public StudyParameters()
	{
		super();
	}

	/**
	 * 
	 * @param patientId
	 * @param fromDate
	 * @param toDate
	 * @param studyId
	 */
	public StudyParameters(String patientId, Date fromDate, Date toDate, GlobalArtifactIdentifier studyId)
	{
		this(
			patientId, 
			new StudyFilter(fromDate, toDate, studyId),
			defaultDatasource
		);
	}

	private StudyParameters(String patientId, StudyFilter filter, String datasource)
	{
		super();
		
		TransactionContext transactionContext = TransactionContextFactory.get();

		if(transactionContext != null)
			this.requestor = new Requestor(transactionContext.getFullName(), transactionContext.getSsn(), transactionContext.getSiteNumber(), transactionContext.getSiteName());
		
		this.patientId = patientId;
		this.filter = filter;
		this.datasource = datasource;
	}	
	
	// =============================================================================================
	// Convenience typpe-casting and passthrough methods
	// =============================================================================================
	public String getPatientId()
	{
		return this.patientId;
	}

	public void setPatientId(String patientId)
	{
		this.patientId = patientId;
	}

	public StudyFilter getFilter()
	{
		return this.filter;
	}
	
	public Date getFromDate() 
	throws ParseException
	{
		return getFilter().getFromDate();
	}

	public void setFromDate(Date fromDate)
	{
		getFilter().setFromDate( fromDate );
	}

	public GlobalArtifactIdentifier getStudyId()
	{
		return getFilter().getStudyId();
	}

	public void setStudyId(GlobalArtifactIdentifier studyId)
	{
		getFilter().setStudyId(studyId);
	}

	public Date getToDate() 
	throws ParseException
	{
		return getFilter().getToDate();
	}

	public void setToDate(Date toDate)
	{
		getFilter().setToDate( toDate );
	}

	public Requestor getRequestor()
	{
		return this.requestor;
	}

	public void setRequestor(Requestor requestor)
	{
		this.requestor = requestor;
	}

	public void setFilter(StudyFilter filter)
	{
		this.filter = filter == null ? new StudyFilter() : filter;
	}

	public String getDatasource()
	{
		return this.datasource;
	}

	public void setDatasource(String datasource)
	{
		this.datasource = datasource;
	}

    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("Class: ");
    	sb.append(this.getClass().getSimpleName());
    	sb.append(" [");

    	sb.append(" requestor:");
    	sb.append( getRequestor() == null ? "<null>" : getRequestor().toString() );
    	
    	sb.append(", patient ID:");
    	sb.append( getPatientId() );
    	
    	sb.append(", filter:");
    	sb.append( getFilter() == null ? "<null>" : getFilter().toString() );
    	
    	sb.append(", datasource:");
    	sb.append( getDatasource() );
    	
    	sb.append("]");
    	
    	return sb.toString();
    }
	
	
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.filter == null) ? 0 : this.filter.hashCode());
		result = PRIME * result + ((this.patientId == null) ? 0 : this.patientId.hashCode());
		result = PRIME * result + ((this.requestor == null) ? 0 : this.requestor.hashCode());
		return result;
	}
}
