package gov.va.med.imaging.exchange.business.dicom;

public class UIDCheckResults
{
	private UIDCheckResult studyResult;
	private UIDCheckResult seriesResult;
	private UIDCheckResult sopInstanceResult;
	
	public UIDCheckResults(UIDCheckResult studyResult, UIDCheckResult seriesResult, UIDCheckResult sopInstanceResult)
	{
		this.studyResult = studyResult;
		this.seriesResult = seriesResult;
		this.sopInstanceResult = sopInstanceResult;
	}
	
	public boolean isDuplicateStudyUID()
	{
		return studyResult.isDuplicateUID();
	}

	public boolean isDuplicateSeriesUID()
	{
		return seriesResult.isDuplicateUID();
	}

	public boolean isDuplicateInstanceUID()
	{
		return sopInstanceResult.isDuplicateUID();
	}

	public boolean anyDuplicatesExist()
	{
		return isDuplicateStudyUID() || isDuplicateSeriesUID() || isDuplicateInstanceUID();
	}

	public boolean anyIllegalsExist()
	{
		return isIllegalStudyUID() || isIllegalSeriesUID() || isIllegalInstanceUID();
	}
		
	public UIDCheckResult getStudyResult()
	{
		return studyResult;
	}

	public UIDCheckResult getSeriesResult()
	{
		return seriesResult;
	}

	public UIDCheckResult getSOPInstanceResult()
	{
		return sopInstanceResult;
	}
	
	public boolean isSOPInstanceResend()
	{
		return this.sopInstanceResult.isSOPInstanceResend();
	}

	public boolean isIllegalStudyUID()
	{
		return this.studyResult.isIllegalUID();
	}
	public boolean isIllegalSeriesUID()
	{
		return this.seriesResult.isIllegalUID();
	}
	public boolean isIllegalInstanceUID()
	{
		return this.sopInstanceResult.isIllegalUID();
	}

}

