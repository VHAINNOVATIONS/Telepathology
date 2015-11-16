package gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

public class DoseTemplate
{
    private static Logger logger = Logger.getLogger(DoseTemplate.class);

    private static final String CODING_SCHEME = "ConceptNameCodeSequence_CodingSchemeDesignator";
	private static final String CODE_VALUE = "ConceptNameCodeSequence_CodeValue";
	private static final String VALUE_TYPE = "ValueType";
	
	private HashMap<String, String> templateMap = new HashMap<String, String>();

	public DoseTemplate(Vertex templateRootVertex)
	{
		super();

		// Build the node map
		buildTemplateMap(templateRootVertex);
	}


	private void buildTemplateMap(Vertex currentVertex)
	{
		addVertexToMap(currentVertex);

		for (Vertex childVertex : currentVertex.getVertices(Direction.OUT))
		{
			// Recursively call this function to continue mapping and flattening the tree
			buildTemplateMap(childVertex);
		}
	}

	private void addVertexToMap(Vertex currentVertex)
	{
		String key = (currentVertex.getProperty(CODING_SCHEME) + "") + "_" + (currentVertex.getProperty(CODE_VALUE) + "");
		String valueType = currentVertex.getProperty(VALUE_TYPE) + "";
		
		if (templateMap.containsKey(key))
		{
			logger.error("Map already contains key: " + key);
		}
		
		// Handle Text value type
		if (valueType.equals(ValueTypes.TextValue))
		{
			String value = currentVertex.getProperty(ValueKeys.Text).toString();
			templateMap.put(key, value);
		}
		
		// Handle Numeric value type
		if (valueType.equals(ValueTypes.NumericValue))
		{
			String value = currentVertex.getProperty(ValueKeys.Number).toString() + 
						   "|" + 
						   currentVertex.getProperty(ValueKeys.Units).toString();
			
			templateMap.put(key, value);
		}

		// Handle Coded value type
		if (valueType.equals(ValueTypes.CodedValue))
		{
			String value = currentVertex.getProperty(ValueKeys.Code).toString();
			templateMap.put(key, value);
		}

		// Handle UID value type
		if (valueType.equals(ValueTypes.UidRef))
		{
			String value = currentVertex.getProperty(ValueKeys.Uid).toString();
			templateMap.put(key, value);
		}
	}


	protected String getMappedValue(String key)
	{
		if (templateMap.containsKey(key))
		{
			return templateMap.get(key);
		}
		else
		{
			return "";
		}
	}
	
	protected String getNumericValueInSpecifiedUnits(String valueWithUnits, String desiredUnits)
	{
		valueWithUnits += "";
		valueWithUnits = valueWithUnits.trim();
		
		// If the value is an empty string, return empty string
		if (valueWithUnits.equals(""))
		{
			return "";
		}
		
		// If the value is in the expected format, delimited by |, parse it and
		// convert units if necessary.
		if (valueWithUnits.contains("|"))
		{
			NumberFormat formatter = new DecimalFormat("###.#########################");  

			String[] parts = StringUtils.split(valueWithUnits, "|");
			String valueAsString = parts[0];
			BigDecimal value = new BigDecimal(valueAsString);

			String units = parts[1].toUpperCase();
			desiredUnits = desiredUnits.toUpperCase();

			
			if (!units.equals(desiredUnits))
			{
				value = convertValueToNewUnits(value, units, desiredUnits);
			}
		
			return formatter.format(value);
		}
		
		// The value was not an empty string, but also did not contain the expected | delimiter, so just return the entire string.
		return valueWithUnits;
	}
	
	private BigDecimal convertValueToNewUnits(BigDecimal value, String units, String desiredUnits)
	{
		if (units.equals("GY") && desiredUnits.equals("MGY"))
		{
			value = value.multiply(new BigDecimal(1000));
		}
		else if (units.equals("GYM2") && desiredUnits.equals("MGYCM2"))
		{
			// Convert to mgy/m2
			value = value.multiply(new BigDecimal(1000));
			
			// Finish conversion to mgy/cm2  (1m2 = 10000 cm2)
			value = value.divide(new BigDecimal(10000));
		}
		else
		{
			logger.error("Unknown conversion: " + units + " to " + desiredUnits);
		}
		
		return value;
	}

	private class ValueTypes
	{
		public static final String CodedValue = "CODE";
		public static final String TextValue = "TEXT";
		public static final String NumericValue = "NUM";
		public static final String UidRef = "UIDREF";
	}
	
	private class ValueKeys
	{
		public static final String Text = "TextValue";
		public static final String Code = "ConceptCodeSequence_CodeValue";
		public static final String Units = "CodeValue";
		public static final String Number = "NumericValue";
		public static final String Uid = "UID";
	}
	
}
