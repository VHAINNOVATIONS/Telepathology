package gov.va.med.imaging.datasource;

import java.io.Serializable;

import gov.va.med.imaging.exchange.business.StudyFilter;

/**
 * A Serializable value object used to store the parameters to a getPatientStudies() call
 * for testing.
 * 
 * @author VHAISWBECKEC
 *
 */
public class GetPatientStudiesParameters
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final StudyFilter filter;
	private final String patientIcn;
	
	public GetPatientStudiesParameters(StudyFilter filter, String patientIcn)
    {
	    super();
	    this.filter = filter;
	    this.patientIcn = patientIcn;
    }

	public StudyFilter getFilter()
    {
    	return filter;
    }

	public String getPatientIcn()
    {
    	return patientIcn;
    }
}
