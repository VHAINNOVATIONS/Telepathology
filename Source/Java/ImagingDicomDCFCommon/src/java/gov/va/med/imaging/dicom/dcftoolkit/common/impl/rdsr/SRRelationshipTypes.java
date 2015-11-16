package gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr;


public class SRRelationshipTypes 
{
	public static final String Implicit = "Implicit";
	public static final String Contains = "Contains";
	public static final String HasProperties = "HasProperties";
	public static final String InferredFrom = "InferredFrom";
	public static final String SelectedFrom = "SelectedFrom";
	public static final String HasObservationContext = "HasObservationContext";
	public static final String HasAcquisitionContext = "HasAcquisitionContext";
	public static final String HasConceptModifier = "HasConceptModifier";

	public static String getType(String relationship)
	{
		relationship = relationship.trim();

		if (relationship.equals("CONTAINS"))
		{
			return Contains;
		}

		if (relationship.equals("HAS ACQUISITION CONTEXT"))
		{
			return HasAcquisitionContext;
		}

		if (relationship.equals("HAS CONCEPT MOD"))
		{
			return HasConceptModifier;
		}

		if (relationship.equals("HAS OBS CONTEXT"))
		{
			return HasObservationContext;
		}

		if (relationship.equals("HAS PROPERTIES"))
		{
			return HasProperties;
		}

		if (relationship.equals("SELECTED FROM"))
		{
			return SelectedFrom;
		}

		if (relationship.equals("INFERRED FROM"))
		{
			return InferredFrom;
		}

		return Implicit;
	}
}
