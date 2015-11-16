/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 21, 2013
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.business.dicom.importer;

/**
 * @author vhaiswlouthj
 *
 */
public class NonDicomFile 
{
	private String filePath;
	private String name;
	private String originalFileName;
	private String size;
	private String importErrorMessage;
	private boolean isImportedSuccessfully;
	
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the originalFileName
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}
	/**
	 * @param originalFileName the originalFileName to set
	 */
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the importErrorMessage
	 */
	public String getImportErrorMessage() {
		return importErrorMessage;
	}
	/**
	 * @param importErrorMessage the importErrorMessage to set
	 */
	public void setImportErrorMessage(String importErrorMessage) {
		this.importErrorMessage = importErrorMessage;
	}
	/**
	 * @return the importedSuccessfully
	 */
	public boolean isImportedSuccessfully() {
		return isImportedSuccessfully;
	}
	/**
	 * @param importedSuccessfully the importedSuccessfully to set
	 */
	public void setImportedSuccessfully(boolean isImportedSuccessfully) {
		this.isImportedSuccessfully = isImportedSuccessfully;
	}
	
}
