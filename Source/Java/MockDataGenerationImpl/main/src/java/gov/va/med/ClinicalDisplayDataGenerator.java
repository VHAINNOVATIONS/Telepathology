/**
 * 
 */
package gov.va.med;

import java.util.Date;
import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.ReferenceMap.ReferenceKey;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

/**
 * @author vhaiswbeckec
 *
 */
public class ClinicalDisplayDataGenerator
extends VistaDataGenerator
{
	public static final PatientSensitivityLevel DEFAULT_PATIENT_SENSITIVITY = PatientSensitivityLevel.NO_ACTION_REQUIRED;
	/**
	 * @param mode
	 */
	public ClinicalDisplayDataGenerator(DataGenerationConfiguration configuration)
	{
		super(configuration);
	}

	/**
	 * @return
	 */
	public StudyFilter createStudyFilter(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		StudyFilter result = new StudyFilter();
		references.putReference(ReferenceKey.STUDY_FILTER, result);
		
		if(instancePopulation == InstancePopulation.DEFAULT)
		{
			result.setFromDate(DEFAULT_START_DATE);
			result.setToDate(DEFAULT_END_DATE);
			result.setMaximumAllowedLevel(DEFAULT_PATIENT_SENSITIVITY);
		}
		
		return result;
	}

	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @return
	 * @throws URNFormatException
	 */
	public Study createStudy(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references) 
	throws URNFormatException
	{
		Study result = null;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = Study.create(null, null, StudyDeletedImageState.cannotIncludeDeletedImages);
			references.putReference(ReferenceKey.STUDY, result);
		}
		else
		{
			StudyURN studyUrn = (StudyURN)references.getReference(ReferenceMap.ReferenceKey.STUDY_URN);
			if(studyUrn == null)
				studyUrn = createStudyUrn();
			references.putReference(ReferenceKey.STUDY_URN, studyUrn);
	
			if(instancePopulation == InstancePopulation.DEFAULT || instancePopulation == InstancePopulation.REQUIRED)
			{
				result = Study.create(studyUrn, StudyLoadLevel.STUDY_ONLY, StudyDeletedImageState.cannotIncludeDeletedImages);
				references.putReference(ReferenceKey.STUDY, result);
			}
			else if(instancePopulation == InstancePopulation.FULL)
			{
				result = Study.create(studyUrn, StudyLoadLevel.STUDY_AND_IMAGES, StudyDeletedImageState.cannotIncludeDeletedImages);
				references.putReference(ReferenceKey.STUDY, result);
			}	
		}
		
		return result;
	}

	/**
	 * 
	 * @param study
	 */
	public void postPopulateStudy(Study study)
	{
		if( study.getFirstImage() == null && study.getSeries() != null)
		{
			Image firstImage = null;
			for( Series series : study  )
			{
				for(Image image : series)
				{
					firstImage = image;
					break;
				}
				break;
			}
			if(firstImage != null)
			{
				study.setFirstImage(firstImage);
				study.setFirstImageIen(firstImage.getIen());
				study.setPatientName(firstImage.getPatientName());
			}
		}
		
		return;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @param references
	 * @return
	 * @throws URNFormatException
	 */
	public Series createSeries(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references) 
	throws URNFormatException
	{
		Series series = null;
		if(instancePopulation == InstancePopulation.NULL)
		{
			series = Series.create(null, null, null);
			references.putReference(ReferenceKey.SERIES, series);
		}
		
		ObjectOrigin objectOrigin = (ObjectOrigin)references.getReference(ReferenceKey.OBJECT_ORIGIN);
		
		if(instancePopulation == InstancePopulation.DEFAULT || instancePopulation == InstancePopulation.REQUIRED)
		{
			if(objectOrigin == null)
				objectOrigin = ObjectOrigin.VA;
			
			references.putReference(ReferenceKey.OBJECT_ORIGIN, objectOrigin);
			series = Series.create(objectOrigin, getOrCreateSeriesIen(references), getOrCreateSeriesNumber(references));
		}
		else if(instancePopulation == InstancePopulation.FULL)
		{
			if(objectOrigin == null)
				objectOrigin = createObjectOrigin();
			
			references.putReference(ReferenceKey.OBJECT_ORIGIN, objectOrigin);
			series = Series.create(objectOrigin, createSeriesIen(), Integer.toString(createRandomInt(1, 128)) );
		}
		
		return series;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @param references
	 * @return
	 * @throws URNFormatException
	 */
	public Image createImage(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	throws URNFormatException
	{
		Image image = null;
		if(instancePopulation == InstancePopulation.NULL)
		{
			image = Image.create(null, null, null, null, null);
			references.putReference(ReferenceKey.IMAGE, image);
		}
		else
		{
			image = Image.create( 
				getOrCreateSiteNumber(references), 
				getOrCreateImageIen(references), 
				getOrCreateStudyId(references), 
				PatientIdentifier.icnPatientIdentifier(getOrCreatePatientICN(references)), 
				getOrCreateModality(references)
			);
			references.putReference(ReferenceKey.IMAGE, image);
		}
		
		return image;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @param references
	 * @return
	 * @throws URNFormatException
	 */
	public Patient createPatient(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	throws URNFormatException
	{
		Patient patient = null;
		if(instancePopulation == InstancePopulation.NULL)
		{
			patient = Patient.create(null, null, null, null, null, null, null, false);
			references.putReference(ReferenceKey.PATIENT, patient);
		}
		else
		{
			patient = Patient.create(
				getOrCreatePatientName(references), 
				getOrCreatePatientICN(references), 
				createVeteranStatus(), 
				createPatientSex(), 
				createDOB(),
				createRandomSSN(),
				null,
				false);
			references.putReference(ReferenceKey.PATIENT, patient);
		}
		
		return patient;
	}
	
	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		ClinicalDisplayDataGenerator generator = new ClinicalDisplayDataGenerator(
			new DataGenerationConfiguration(Mode.RANDOMIZE, EXCEPTION_MODE.RELIABLE, DELAY_MODE.NONE)
		);
		
		try
		{
			Study study = generator.createStudy(
				InstancePopulation.FULL, 
				AggregationPopulation.MANY,
				CompositionPopulation.MANY,
				ReferenceMap.createRoot()
			);
			System.out.println("Study");
			System.out.println(study.toString());
			
			Series series = generator.createSeries(
				InstancePopulation.FULL, 
				AggregationPopulation.MANY,
				CompositionPopulation.MANY,
				ReferenceMap.createRoot()
			);
			System.out.println("Series");
			System.out.println(series.toString());

			Image image = generator.createImage(
				InstancePopulation.FULL, 
				AggregationPopulation.MANY,
				CompositionPopulation.MANY,
				ReferenceMap.createRoot()
			);
			System.out.println("Image");
			System.out.println(image.toString());
		}
		catch (URNFormatException x)
		{
			x.printStackTrace();
		}
	}
}
