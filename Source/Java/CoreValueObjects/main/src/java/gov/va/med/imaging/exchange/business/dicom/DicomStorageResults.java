package gov.va.med.imaging.exchange.business.dicom;

public class DicomStorageResults
{
	private static final long serialVersionUID = 1L;

	private UIDCheckResults uidCheckResults;
	
	private boolean existInDicomCorrect = false;
	
	public UIDCheckResults getUidCheckResults()
	{
		return uidCheckResults;
	}

	public DicomStorageResults(UIDCheckResults uidCheckResults)
	{
		this.uidCheckResults = uidCheckResults;
	}
	
	public DicomStorageResults (boolean existInDicomCorrect){
		this.existInDicomCorrect = existInDicomCorrect;
	}

	/**
	 * @return the currentlyExistInDicomCorrect
	 */
	public boolean isExistInDicomCorrect() {
		return this.existInDicomCorrect;
	}
	
}
