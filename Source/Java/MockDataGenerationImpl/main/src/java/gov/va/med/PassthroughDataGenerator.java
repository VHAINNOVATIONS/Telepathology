/**
 * 
 */
package gov.va.med;

import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.ReferenceMap.ReferenceKey;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.PassthroughParameter;
import gov.va.med.imaging.exchange.business.PassthroughParameterType;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;

/**
 * @author vhaiswbeckec
 *
 */
public class PassthroughDataGenerator
extends VistaDataGenerator
{
	public static final PatientSensitivityLevel DEFAULT_PATIENT_SENSITIVITY = PatientSensitivityLevel.NO_ACTION_REQUIRED;
	/**
	 * @param mode
	 */
	public PassthroughDataGenerator(DataGenerationConfiguration configuraton)
	{
		super(configuraton);
	}

	/**
	 * @return
	 */
	public PassthroughParameter createPassthroughParameter(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		PassthroughParameter result;
		
		if(instancePopulation == InstancePopulation.NULL)
		{
			result = new PassthroughParameter();
			references.putReference(ReferenceKey.PASSTHROUGH_PARAMETER, result);
		}
		else
		{		
			result = new PassthroughParameter();
			references.putReference(ReferenceKey.PASSTHROUGH_PARAMETER, result);
				
			if(instancePopulation == InstancePopulation.DEFAULT)
				return result;
			
			populateChildren(result, instancePopulation, aggregation, composition, references);
			populateComponentInstances(ImageAccessLogEvent.class, result, instancePopulation, aggregation, composition, references);
		}
		
		return result;
	}
	
	public PassthroughParameterType createPassthroughParameterType(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregation,
		CompositionPopulation composition,
		ReferenceMap references)
	{
		PassthroughParameterType result = 
			getConfiguration().getMode() == Mode.DEFAULT ? PassthroughParameterType.literal :
			this.selectEnum(PassthroughParameterType.class);
		
		return result;
		
	}
	
	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		PassthroughDataGenerator generator = new PassthroughDataGenerator(
			new DataGenerationConfiguration(Mode.RANDOMIZE, EXCEPTION_MODE.RELIABLE, DELAY_MODE.NONE)
		);
		
		PassthroughParameter passthrough = generator.createPassthroughParameter(
			InstancePopulation.FULL, 
			AggregationPopulation.MANY,
			CompositionPopulation.MANY,
			ReferenceMap.createRoot()
		);
		System.out.println("Passthrough Parameter");
		System.out.println(passthrough.toString());
	}
}
