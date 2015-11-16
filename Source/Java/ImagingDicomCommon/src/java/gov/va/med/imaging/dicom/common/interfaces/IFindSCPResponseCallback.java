/**
 * 
 */
package gov.va.med.imaging.dicom.common.interfaces;


/**
 * @author vhaiswpeterb
 *
 */
public interface IFindSCPResponseCallback {	

	public void cFindResponseComplete(int status, IDicomDataSet dds);
	
	public int cFindResponseResult (int status, IDicomDataSet dds);
}
