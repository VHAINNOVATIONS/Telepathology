/**
 * 
 */
package gov.va.med;

import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.ReferenceMap.ReferenceKey;
import gov.va.med.imaging.BhieStudyURN;
import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.ResolvedSiteImpl;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * @author vhaiswbeckec
 * 
 * Abstract only because the derived classes do the work of creating business objects.
 */
public class VistaDataGenerator
extends GenericDataGenerator
{
	/**
	 * 
	 */
	public VistaDataGenerator(DataGenerationConfiguration configuration)
	{
		super(configuration);
	}
	
	protected static final String DEFAULT_PATIENT_NAME = "Julian Werfel";
	protected static final String DEFAULT_PATIENT_ICN = "6553211234V1234";
	protected static final String DEFAULT_PATIENT_DFN = "655321";
	protected static final String DEFAULT_STUDY_ID = "11111";
	protected static final String DEFAULT_EXAM_ID = "22222";
	protected static final String DEFAULT_IMAGE_ID = "33333";
	protected static final String DEFAULT_IMAGE_IEN = "4444";
	protected static final String DEFAULT_SERIES_IEN = "5555";
	protected static final String DEFAULT_SERIES_NUMBER = "2";
	protected static final String DEFAULT_SITE_NUMBER = "660";
	protected static final String DEFAULT_RAW_HEADER = "DefaultRawHeader";
	protected static final ObjectOrigin DEFAULT_OBJECT_ORIGIN = ObjectOrigin.VA;
	protected static final String DEFAULT_MODALITY = "CR";
	protected static final String DEFAULT_SSN = "123456789";
	
	protected static final Date DEFAULT_START_DATE = new Date(0L);
	protected static final Date DEFAULT_END_DATE = new Date();
	
	private static final long MAXIMUM_AGE = 120L;		// the maximum age in years a person is expected to live
	
	/**
	 * @return
	 */
	public RoutingToken getOrCreateRoutingToken(ReferenceMap references)
	{
		RoutingToken value = (RoutingToken)references.getReference(ReferenceKey.ROUTING_TOKEN);
		if(value == null)
		{
			value = createRoutingToken();
			references.putReference(ReferenceKey.ROUTING_TOKEN, value);
		}
		return value;
	}
	
	public RoutingToken createRoutingToken()
	{
		try
		{
			return SiteImpl.createLocalTestSite().createRoutingToken();
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
			return null;
		}
	}
	
	public ResolvedArtifactSource createResolvedArtifactSource()
	{
		try
		{
			return ResolvedSiteImpl.createLocalResolvedTestSite();
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
			return null;
		}
	}
	
	public String getOrCreateSiteNumber(ReferenceMap references)
	{
		String siteNumber = (String)references.getReference(ReferenceKey.SITE_NUMBER);
		if(siteNumber == null)
		{
			siteNumber = createSiteNumber();
			references.putReference(ReferenceKey.SITE_NUMBER, siteNumber);
		}
		return siteNumber;
	}
	public String createSiteNumber()
	{
		return isRandomize() ? createRandomString("[1-9][0-9]{2}") : DEFAULT_SITE_NUMBER;
	}
	
	public String getOrCreateStudyId(ReferenceMap references)
	{
		String studyIen = (String)references.getReference(ReferenceKey.STUDY_IEN);
		if(studyIen == null)
		{
			studyIen = createStudyId();
			references.putReference(ReferenceKey.STUDY_IEN, studyIen);
		}
		return studyIen;
	}
	public String createStudyId()
	{
		return isRandomize() ? createRandomString("1[0-9]{4,9}") : DEFAULT_STUDY_ID;
	}
	
	public String getOrCreateExamId(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.EXAM_ID);
		if(value == null)
		{
			value = createExamId();
			references.putReference(ReferenceKey.EXAM_ID, value);
		}
		return value;
	}
	public String createExamId()
	{
		return isRandomize() ? createRandomString("2[0-9]{4,9}") : DEFAULT_EXAM_ID;
	}
	
	public String getOrCreateImageIen(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.IMAGE_IEN);
		if(value == null)
		{
			value = createImageIen();
			references.putReference(ReferenceKey.IMAGE_IEN, value);
		}
		return value;
	}
	public String createImageIen()
	{
		return isRandomize() ? createRandomString("4[0-9]{4,9}") : DEFAULT_IMAGE_IEN;
	}
	
	public String getOrCreatePatientName(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.PATIENT_NAME);
		if(value == null)
		{
			value = createPatientName();
			references.putReference(ReferenceKey.PATIENT_NAME, value);
		}
		return value;
	}
	public String createPatientName()
	{
		return isRandomize() ? createRandomName() : DEFAULT_PATIENT_NAME;
	}
	
	public String createPatientSSN()
	{
		return isRandomize() ? createRandomString("9[0-9]") : DEFAULT_SSN;
	}
	
	public String getOrCreatePatientICN(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.PATIENT_ICN);
		if(value == null)
		{
			value = createPatientICN();
			references.putReference(ReferenceKey.PATIENT_ICN, value);
		}
		return value;
	}
	public String createPatientICN()
	{
		return isRandomize() ? createRandomString("[1-9][0-9]{9}V[1-9][0-9]{5}") : DEFAULT_PATIENT_ICN;
	}
	
	public String getOrCreatePatientDFN(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.PATIENT_DFN);
		if(value == null)
		{
			value = createPatientDFN();
			references.putReference(ReferenceKey.PATIENT_DFN, value);
		}
		return value;
	}
	public String createPatientDFN()
	{
		return isRandomize() ? createRandomString("[1-9][0-9]{9}") : DEFAULT_PATIENT_DFN;
	}
	
	public String getOrCreateRawHeader1(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.RAW_HEADER1);
		if(value == null)
		{
			value = createRawHeader();
			references.putReference(ReferenceKey.RAW_HEADER1, value);
		}
		return value;
	}
	public String getOrCreateRawHeader2(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.RAW_HEADER2);
		if(value == null)
		{
			value = createRawHeader();
			references.putReference(ReferenceKey.RAW_HEADER2, value);
		}
		return value;
	}
	public String createRawHeader()
	{
		return isRandomize() ? createRandomString("RawHeader[A-Z][A-Za-z0-9]{2,16}") : DEFAULT_RAW_HEADER;
	}
	
	public ObjectOrigin getOrCreateObjectOrigin(ReferenceMap references)
	{
		ObjectOrigin value = (ObjectOrigin)references.getReference(ReferenceKey.OBJECT_ORIGIN);
		if(value == null)
		{
			value = createObjectOrigin();
			references.putReference(ReferenceKey.OBJECT_ORIGIN, value);
		}
		return value;
	}
	public ObjectOrigin createObjectOrigin()
	{
		return isRandomize() ? selectEnum(ObjectOrigin.class) : DEFAULT_OBJECT_ORIGIN;
	}
	
	public String getOrCreateModality(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.MODALITY);
		if(value == null)
		{
			value = createModality();
			references.putReference(ReferenceKey.MODALITY, value);
		}
		return value;
	}
	public String createModality()
	{
		return isRandomize() ? createRandomString("[A-Z]{2}") : DEFAULT_MODALITY;
	}
	
	/**
	 * @return
	 */
	public String getOrCreateSeriesNumber(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.SERIES_NUMBER);
		if(value == null)
		{
			value = createSeriesNumber();
			references.putReference(ReferenceKey.SERIES_NUMBER, value);
		}
		return value;
	}

	public String createSeriesNumber()
	{
		return isRandomize() ? Integer.toString(createRandomInt(1, 128)) : DEFAULT_SERIES_NUMBER;
	}
	
	/**
	 * @return
	 */
	public String getOrCreateSeriesIen(ReferenceMap references)
	{
		String value = (String)references.getReference(ReferenceKey.SERIES_IEN);
		if(value == null)
		{
			value = createSeriesIen();
			references.putReference(ReferenceKey.SERIES_IEN, value);
		}
		return value;
	}

	public String createSeriesIen()
	{
		return isRandomize() ? Integer.toString(createRandomInt(100, 999)) : DEFAULT_SERIES_IEN;
	}
	
	/**
	 * @return
	 * @throws URNFormatException 
	 */
	public StudyURN createStudyUrn() 
	throws URNFormatException
	{
		return StudyURN.create( createSiteNumber(), createStudyId(), createPatientICN() );
	}

	public BhieStudyURN createBhieStudyUrn() 
	throws URNFormatException
	{
		return BhieStudyURN.create( createRandomString("[a-zA-Z]{2,2}-[a-zA-Z]{16,32}") );
	}

	/**
	 * @return
	 */
	public Date createDOB()
	{
		Date now = new Date();
		Date oldestDOB = new Date( now.getTime() - (MAXIMUM_AGE * DateUtil.SECONDS_IN_YEAR * 1000) );
		
		return createRandomDate(oldestDOB, now);
	}

	/**
	 * @return
	 */
	public PatientSex createPatientSex()
	{
		return selectEnum(PatientSex.class);
	}

	/**
	 * @return
	 */
	public String createVeteranStatus()
	{
		// just a random string.  the big pattern is to make it look more like a word
		return createRandomString("[A-Z][aeiou][a-z]{1,2}[aeiou][a-z]{1,2}[aeiou][a-z]{1,2}");
	}

	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @param references
	 * @return
	 */
	public PatientSensitiveValue createPatientSensitiveValue(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		PatientSensitiveValue result = null;
		
		if( InstancePopulation.NULL == instancePopulation )
			result = new PatientSensitiveValue(null, null);
		else if(InstancePopulation.DEFAULT == instancePopulation)
			result = new PatientSensitiveValue(selectEnum(PatientSensitivityLevel.class), createRandomString("[A-Za-z ]{32,128}") );
		else if(InstancePopulation.REQUIRED == instancePopulation)
			result = new PatientSensitiveValue(selectEnum(PatientSensitivityLevel.class), createRandomString("[A-Za-z ]{32,128}") );
		else if(InstancePopulation.FULL == instancePopulation)
			result = new PatientSensitiveValue(selectEnum(PatientSensitivityLevel.class), createRandomString("[A-Za-z ]{32,128}") );
		
		return result;
	}
	
}
