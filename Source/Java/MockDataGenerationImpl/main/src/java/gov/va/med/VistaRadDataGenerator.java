/**
 * 
 */
package gov.va.med;

import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.ReferenceMap.ReferenceKey;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExam;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;

/**
 * @author vhaiswbeckec
 *
 */
public class VistaRadDataGenerator
extends VistaDataGenerator
{
	public static final PatientSensitivityLevel DEFAULT_PATIENT_SENSITIVITY = PatientSensitivityLevel.NO_ACTION_REQUIRED;
	/**
	 * @param mode
	 */
	public VistaRadDataGenerator(DataGenerationConfiguration configuration)
	{
		super(configuration);
	}

	/**
	 * 
	 * @param instancePopulation
	 * @param children
	 * @return
	 */
	public Exam createExamInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation children,
		CompositionPopulation components,
		ReferenceMap references)
	throws URNFormatException
	{
		Exam exam;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			exam = Exam.create(null, null, null);
			references.putReference(ReferenceKey.EXAM, exam);
		}
		else
		{		
			exam = Exam.create( getOrCreateSiteNumber(references), getOrCreateStudyId(references), getOrCreatePatientICN(references) );
			references.putReference(ReferenceKey.EXAM, exam);
			if(instancePopulation == InstancePopulation.DEFAULT)
				return exam;
			
			populateChildrenThroughSetters(exam, instancePopulation, children, components, references);
		}
		
		return exam;
	}
	
	public ExamSite createExamSiteInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation children,
		CompositionPopulation components,
		ReferenceMap references)
	{
		ExamSite result;
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = new ExamSite(null, ArtifactResultStatus.fullResult, null);
			references.putReference(ReferenceKey.EXAM_SITE, result);
		}
		else
		{
			result = new ExamSite(getOrCreateRoutingToken(references), ArtifactResultStatus.fullResult, null);
			references.putReference(ReferenceKey.EXAM_SITE, result);
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildrenThroughSetters(result, instancePopulation, children, components, references);
		}
		
		return result;
	}
	
	/**
	 * @param full
	 * @param many
	 * @return
	 */
	public ActiveExams createActiveExamsInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		if(instancePopulation == InstancePopulation.NULL)
			return new ActiveExams(null, null, null);

		String siteNumber = getOrCreateSiteNumber(references);
		String rawHeader1 = getOrCreateRawHeader1(references);
		String rawHeader2 = getOrCreateRawHeader2(references);
		
		ActiveExams result = new ActiveExams(siteNumber, rawHeader1, rawHeader2);
		references.putReference(ReferenceKey.ACTIVE_EXAMS, result);
		if(instancePopulation == InstancePopulation.DEFAULT)
			return result;
		
		populateChildren(result, instancePopulation, aggregation, composition, references);
		populateComponentInstances(ActiveExams.class, result, instancePopulation, aggregation, composition, references);
		
		return result;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @return
	 */
	public ActiveExam createActiveExamInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		ActiveExam result;
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = new ActiveExam(null, null, null);
			references.putReference(ReferenceKey.ACTIVE_EXAM, result);
		}
		else
		{
			result = new ActiveExam(createSiteNumber(), createExamId(), createPatientICN());
			references.putReference(ReferenceKey.ACTIVE_EXAM, result);
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildren(result, instancePopulation, aggregation, composition, references);
		}		
		return result;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @return
	 */
	public ExamImage createExamImageInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	throws URNFormatException
	{
		ExamImage result;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = ExamImage.create(null, null, null, null);
			references.putReference(ReferenceKey.EXAM_IMAGE, result);
		}
		else
		{
			result = ExamImage.create( createSiteNumber(), createImageIen(), createExamId(), createPatientICN() );
			references.putReference(ReferenceKey.EXAM_IMAGE, result);
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildren(result, instancePopulation, aggregation, composition, references);
		}
		return result;
	}
	
	/**
	 * 
	 * @param instancePopulation
	 * @param aggregation
	 * @param composition
	 * @return
	 */
	public ExamImages createExamImagesInstance(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		ExamImages result;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = new ExamImages(null, false);
			references.putReference(ReferenceKey.EXAM_IMAGES, result);
		}
		else
		{
			result = new ExamImages( getOrCreateRawHeader1(references), false );
			references.putReference(ReferenceKey.EXAM_IMAGES, result);
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildren(result, instancePopulation, aggregation, composition, references);
			populateComponentInstances(ExamImages.class, result, instancePopulation, aggregation, composition, references);
		}		
		return result;
	}
	
	public PatientRegistration createPatientRegistration(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		PatientRegistration result = null;
		
		if( InstancePopulation.NULL == instancePopulation )
			result = new PatientRegistration();
		else if(InstancePopulation.DEFAULT == instancePopulation)
			result = new PatientRegistration();
		else if(InstancePopulation.REQUIRED == instancePopulation)
		{
			result = new PatientRegistration();
			populateAnnotatedFields(result, instancePopulation, aggregation, composition, references);
		}
		else if(InstancePopulation.FULL == instancePopulation)
		{
			result = new PatientRegistration();
			populateAnnotatedFields(result, instancePopulation, aggregation, composition, references);
		}
		
		return result;
	}	
	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		VistaRadDataGenerator generator = new VistaRadDataGenerator(
			new DataGenerationConfiguration(Mode.RANDOMIZE, EXCEPTION_MODE.RELIABLE, DELAY_MODE.NONE)
		);
		
		ActiveExam activeExam = generator.createActiveExamInstance(
			InstancePopulation.FULL, 
			AggregationPopulation.MANY,
			CompositionPopulation.MANY,
			ReferenceMap.createRoot()
		);
		System.out.println("Active Exam");
		System.out.println(activeExam.toString());
		
		ActiveExams activeExams = generator.createActiveExamsInstance(
			InstancePopulation.FULL, 
			AggregationPopulation.MANY,
			CompositionPopulation.MANY,
			ReferenceMap.createRoot()
		);
		System.out.println("Active Exams");
		System.out.println(activeExams.toString());

		try
		{
			ExamImage examImage = generator.createExamImageInstance(
				InstancePopulation.FULL, 
				AggregationPopulation.MANY,
				CompositionPopulation.MANY,
				ReferenceMap.createRoot()
			);
			System.out.println("Exam Image");
			System.out.println(examImage.toString());
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		ExamImages examImages = generator.createExamImagesInstance(
			InstancePopulation.FULL, 
			AggregationPopulation.MANY,
			CompositionPopulation.MANY,
			ReferenceMap.createRoot()
		);
		System.out.println("Exam Images");
		System.out.println(examImages.toString());
	}
}
