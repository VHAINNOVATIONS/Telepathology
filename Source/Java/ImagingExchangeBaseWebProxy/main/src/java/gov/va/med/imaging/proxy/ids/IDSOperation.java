/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.proxy.ids;

/**
 * @author VHAISWWERFEJ
 *
 */
public class IDSOperation 
{
	// current list of known operations IDS supports, operations don't have to be defined here, but it makes things easier
	public final static String IDS_OPERATION_IMAGE = "Image";
	public final static String IDS_OPERATION_METADATA = "Metadata";
	public final static String IDS_OPERATION_TEXT = "Text";
	public final static String IDS_OPERATION_PHOTO = "Photo";
	public final static String IDS_OPERATION_EXAM_IMAGE = "ExamImage";
	public final static String IDS_OPERATION_EXAM_IMAGE_TEXT = "ExamImageText";
	public final static String IDS_OPERATION_VISTARAD_METADATA = "VistaRadMetadata";
	public final static String IDS_OPERATION_DOCUMENT = "Document";
	//public final static String IDS_OPERATION_PATHOLOGY = "Pathology";
	
	private final String operationType;
	private String operationPath;
	
	public IDSOperation(String operationType) {
		super();
		this.operationType = operationType;
	}

	public IDSOperation(String operationType, String operationPath) 
	{
		super();
		this.operationType = operationType;
		this.operationPath = operationPath;
	}

	public String getOperationType() {
		return operationType;
	}

	public String getOperationPath() {
		return operationPath;
	}

	public void setOperationPath(String operationPath) {
		this.operationPath = operationPath;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Operation Type: " + operationType);
		sb.append(" [" + operationPath + "]");
		return sb.toString();
	}
}
