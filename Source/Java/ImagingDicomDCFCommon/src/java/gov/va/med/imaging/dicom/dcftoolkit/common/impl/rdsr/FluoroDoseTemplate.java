package gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr;

import org.apache.log4j.Logger;

import com.tinkerpop.blueprints.Vertex;

public class FluoroDoseTemplate extends DoseTemplate
{
    private static Logger logger = Logger.getLogger(FluoroDoseTemplate.class);

	private String doseRpTotalKey = "DCM_113725";
	private String doseAreaProductTotalKey = "DCM_113722";
	private String fluoroTimeTotalKey = "DCM_113730";
	private String fluoroDoseRpTotalKey = "DCM_113728";
	private String fluoroDoseAreaProductTotalKey = "DCM_113726";
	private String cineDoseRpTotalKey = "DCM_113729";
	private String cineDoseAreaProductTotalKey = "DCM_113727";
	private String cineTimeKey = "DCM_113855";

	public FluoroDoseTemplate(Vertex templateRootVertex)
	{
		super(templateRootVertex);
	}

	/**
	 * Dose (RP) Total (Air Kerma equivalent) (EV [113725, DCM, “Dose ( RP) Total”])
	 * @return
	 */
	public String getDoseRpTotal()
	{
		String valueWithUnits = getMappedValue(doseRpTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGy");
	}

	/**
	 * Dose Area Product Total (AKAP equivalent) (EV [113722, DCM, “Dose Area Product Total”])
	 * @return
	 */
	public String getDoseAreaProductTotal()
	{
		String valueWithUnits = getMappedValue(doseAreaProductTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGycm2");
	}

	/**
	 * Fluoro Time Total (EV [113730, DCM, “Total Fluoro Time”])
	 * @return
	 */
	public String getFluoroTimeTotal()
	{
		String valueWithUnits = getMappedValue(fluoroTimeTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "s");
	}

	/**
	 * Fluoro Dose (RP) Total (EV [113728, DCM, “Fluoro Dose ( RP) Total”])
	 * @return
	 */
	public String getFluoroDoseRpTotal()
	{
		String valueWithUnits = getMappedValue(fluoroDoseRpTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGy");
	}

	/**
	 * Fluoro Dose Area Product Total (EV [113726, DCM, “Fluoro Dose Area Product Total”])
	 * @return
	 */
	public String getFluoroDoseAreaProductTotal()
	{
		String valueWithUnits = getMappedValue(fluoroDoseAreaProductTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGycm2");
	}

	/**
	 * Cine (Acquisition) Dose (RP) Total (EV [113729, DCM, “Acquisition Dose ( RP) Total”])
	 * @return
	 */
	public String getCineDoseRpTotal()
	{
		String valueWithUnits = getMappedValue(cineDoseRpTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGy");
	}

	/**
	 * Cine (Acquisition) Dose Area Product Total (EV [113727, DCM, “Acquisition Dose Area Product Total”])
	 * @return
	 */
	public String getCineDoseAreaProductTotal()
	{
		String valueWithUnits = getMappedValue(cineDoseAreaProductTotalKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "Gym2");
	}

	/**
	 * Cine (Acquisition) Time (EV [113855, DCM, “Total Acquisition Time”])
	 * @return
	 */
	public String getCineTime()
	{
		String valueWithUnits = getMappedValue(cineTimeKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "s");
	}
}
