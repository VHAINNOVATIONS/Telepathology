package gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr;

import org.apache.log4j.Logger;

import com.tinkerpop.blueprints.Vertex;

public class CTDoseTemplate extends DoseTemplate
{
    private static Logger logger = Logger.getLogger(CTDoseTemplate.class);

	private String irradiationEventUidKey = "DCM_113769";
	private String meanCTDIvolKey = "DCM_113830";
	private String dlpKey = "DCM_113838";
	private String phantomTypeKey = "DCM_113835";
	private String anatomicTargetRegionKey = "DCM_123014";

	public CTDoseTemplate(Vertex templateRootVertex)
	{
		super(templateRootVertex);
	}

	/**
	 * EV (113769, DCM, “Irradiation Event UID”)
	 * @return
	 */
	public String getIrradiationEventUid()
	{
		return getMappedValue(irradiationEventUidKey);
	}

	/**
	 * CTDIvol (EV [113830, DCM, “Mean CTDIvol”])
	 * @return
	 */
	public String getMeanCTDIvol()
	{
		String valueWithUnits = getMappedValue(meanCTDIvolKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGy");
	}

	/**
	 * DLP (EV [113838, DCM, “DLP”])
	 * @return
	 */
	public String getDlp()
	{
		String valueWithUnits = getMappedValue(dlpKey);
		return getNumericValueInSpecifiedUnits(valueWithUnits, "mGycm");
	}

	/**
	 * Phantom Type (EV [113835, DCM, “CTDIw Phantom Type”])
	 * @return
	 */
	public String getPhantomType()
	{
		return getMappedValue(phantomTypeKey);
	}

	/**
	 * Anatomic target region (EV [123014 , DCM, ”Target Region”])
	 * @return
	 */
	public String getAnatomicTargetRegion()
	{
		return getMappedValue(anatomicTargetRegionKey);
	}
}
