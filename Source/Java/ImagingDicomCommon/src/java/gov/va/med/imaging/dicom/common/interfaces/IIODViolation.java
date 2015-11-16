/**
 * 
 */
package gov.va.med.imaging.dicom.common.interfaces;


/**
 * @author vhaiswpeterb
 *
 */
public interface IIODViolation {
	
	public final static int VIOLATION_WARNING = 1;
	public final static int VIOLATION_ERROR = 2;
	
	public String getError();
	
	public int getViolationLevel();
	
	public void setViolationLevel(int level, String violation);

}
