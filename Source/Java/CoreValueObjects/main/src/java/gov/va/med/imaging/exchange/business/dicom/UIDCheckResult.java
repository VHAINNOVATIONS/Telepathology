package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

public class UIDCheckResult implements PersistentEntity 
{
	public static final String NODUPLICATEFOUND = "0";
	public static final String DUPLICATEFOUND = "1";
	public static final String SOPINSTANCERESEND= "2";
	public static final String FATALERROR= "-1"; // Fatal error
	public static final String FATALERROR2= "-3"; // fatal UID gen error
	public static final String ILLEGALUID= "3"; // was "-2" before

	private int id;
	private String originalUID;
	private String correctedUID;
	private String resultCode;

	private UIDCheckResult() {}
	private UIDCheckResult(String resultCode) 
	{
		this.resultCode = resultCode;
	}
	
	private UIDCheckResult(String originalUID, String correctedUID, String resultCode) 
	{
		this.originalUID = originalUID;
		this.correctedUID = correctedUID;
		this.resultCode = resultCode;
	}
	
	public static UIDCheckResult createResultFatalError()
	{
		return new UIDCheckResult(FATALERROR);
	}
	
	public static UIDCheckResult createResultDuplicateFound(String originalUID, String correctedUID)
	{
		return new UIDCheckResult(originalUID, correctedUID, DUPLICATEFOUND);
	}

	public static UIDCheckResult createResultIllegalUIDFound(String originalUID, String correctedUID)
	{
		return new UIDCheckResult(originalUID, correctedUID, ILLEGALUID);
	}

	public static UIDCheckResult createResultNoDuplicateFound(String originalUID)
	{
		return new UIDCheckResult(originalUID, originalUID, NODUPLICATEFOUND);
	}
	
	public static UIDCheckResult createResultSOPInstanceResendFound(String originalUID)
	{
		return new UIDCheckResult(originalUID, originalUID, SOPINSTANCERESEND);
	}
	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isDuplicateUID()
	{
		return resultCode.equals(DUPLICATEFOUND);
	}

	public boolean isIllegalUID()
	{
		return resultCode.equals(ILLEGALUID);
	}

	public boolean isSOPInstanceResend()
	{
		return resultCode.equals(SOPINSTANCERESEND);
	}

	public boolean isFatalError()
	{
		return (resultCode.equals(FATALERROR) || resultCode.equals(FATALERROR2));
	}

	public String getOriginalUID()
	{
		return originalUID;
	}

	public String getCorrectedUID()
	{
		return correctedUID;
	}
	
	public String getResultCode()
	{
		return resultCode;
	}
	
}

