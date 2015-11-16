package gov.va.med.imaging.exchange.business.dicom.importer;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class SopInstance {

	private String uid;
    private String filePath;
	private String transferSyntaxUid;
	private String sopClassUid;
	private String imageNumber;
	private String numberOfFrames;
	private boolean isImportedSuccessfully;
	private String importErrorMessage;

	

	public SopInstance(){}
    
	public SopInstance(String uid, String filePath) 
	{
		this.uid = uid;
		this.filePath = filePath;
	}

	public SopInstance(String uid, String filePath, String transferSyntaxUid) 
	{
		this.uid = uid;
		this.filePath = filePath;
		this.transferSyntaxUid = transferSyntaxUid;
	}

    public String getUid() {
        return uid;
    }

    public void setUid(String value) {
        this.uid = value;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String value) {
        this.filePath = value;
    }

	public void setTransferSyntaxUid(String transferSyntaxUid) {
		this.transferSyntaxUid = transferSyntaxUid;
	}

	public String getTransferSyntaxUid() {
		return transferSyntaxUid;
	}

	public void setSopClassUid(String sopClassUid) {
		this.sopClassUid = sopClassUid;
	}

	public String getSopClassUid() {
		return sopClassUid;
	}

	public void setImageNumber(String imageNumber) {
		this.imageNumber = imageNumber;
	}

	public String getImageNumber() {
		return imageNumber;
	}

	public void setNumberOfFrames(String numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}

	public String getNumberOfFrames() {
		return numberOfFrames;
	}

	public void setImportedSuccessfully(boolean isImportedSuccessfully) {
		this.isImportedSuccessfully = isImportedSuccessfully;
	}

	public boolean isImportedSuccessfully() {
		return isImportedSuccessfully;
	}

	public void setImportErrorMessage(String importErrorMessage) {
		this.importErrorMessage = importErrorMessage;
	}

	public String getImportErrorMessage() {
		return importErrorMessage;
	}

}
