/**
 * 
 */
package gov.va.med.imaging.dicom.common.interfaces;


/**
 * @author vhaiswpeterb
 *
 */
public interface IIODViolationList {

	public void addViolation(IIODViolation violation);
		
	public String getManufacturerName();
	
	public String getModelName();
	
	public String getSoftwareVersion();
	
	public String getSOPClass();
	
	public IIODViolation[] getViolationArray();
	
	public IIODViolation getViolationAt(int index);
	
	public int getViolationCount();
	
	public int getWarningCount();

	public int getErrorCount();
	
	public boolean hasViolationErrors();
	
	public boolean hasViolationWarnings();
	
	public void setDeviceInformation(String manufacturer, String model, 
										String softwareVersion, String sopClass);
	
}
