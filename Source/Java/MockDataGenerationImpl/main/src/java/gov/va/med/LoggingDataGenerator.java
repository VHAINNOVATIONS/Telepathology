/**
 * 
 */
package gov.va.med;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.business.vistarad.*;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.ReferenceMap.ReferenceKey;

/**
 * @author vhaiswbeckec
 *
 */
public class LoggingDataGenerator
extends VistaDataGenerator
{
	/**
	 * @param mode
	 */
	public LoggingDataGenerator(DataGenerationConfiguration configuration)
	{
		super(configuration);
	}

	/**
	 * @return
	 */
	public ImageAccessLogEvent createImageAccessLogEvent(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		ImageAccessLogEvent result;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = new ImageAccessLogEvent(null, null, null, null, 0L, null, null, ImageAccessLogEventType.IMAGE_ACCESS, null);
			references.putReference(ReferenceKey.IMAGE_ACCESS_LOG_EVENT, result);
		}
		else
		{
			result = new ImageAccessLogEvent(
				getOrCreateImageIen(references),
				getOrCreatePatientDFN(references),
				getOrCreatePatientICN(references),
				getOrCreateSiteNumber(references),
				createRandomLong("[1-9][0-9]{8}"),
				createRandomString("[A-Z][a-z]{2-5} [A-Z][a-z]{2-5} [A-Z][a-z]{2-5} [A-Z][a-z]{2-5}"),
				createRandomString("[A-Z][a-z]{2-5} [A-Z][a-z]{2-5} [A-Z][a-z]{2-5} [A-Z][a-z]{2-5}"),
				selectEnum(ImageAccessLogEventType.class),
				createSiteNumber() );
			references.putReference(ReferenceKey.IMAGE_ACCESS_LOG_EVENT, result);
				
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildren(result, instancePopulation, aggregation, composition, references);
			populateComponentInstances(ImageAccessLogEvent.class, result, instancePopulation, aggregation, composition, references);
		}		
		return result;
	}
	
	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		LoggingDataGenerator generator = new LoggingDataGenerator(
			new DataGenerationConfiguration(Mode.RANDOMIZE, EXCEPTION_MODE.RELIABLE, DELAY_MODE.NONE)
		);
		
		ImageAccessLogEvent logEvent = generator.createImageAccessLogEvent(
			InstancePopulation.FULL, 
			AggregationPopulation.MANY,
			CompositionPopulation.MANY,
			ReferenceMap.createRoot()
		);
		System.out.println("ImageAccessLogEvent");
		System.out.println(logEvent.toString());
	}
}
