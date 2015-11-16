package gov.va.med.imaging.exchange.enums;

/**
 * @author VHAISWBECKEC
 *
 */
public enum ImageQuality
implements java.io.Serializable
{
	// NOTE: the min and max values in the following declarations MUST NOT OVERLAP
	// NOTE: the order of the enumerated values is significant.  They MUST be in ascending order
	// by increasing quality.
	THUMBNAIL(1, 20, 49, "Thumbnail"),					// 20 is the canonical value
	REFERENCE(50, 70, 79, "Reference"), 					// 70 is the canonical value
	DIAGNOSTIC(80, 90, 99, "Diagnostic"),					// 90 is the canonical value
	DIAGNOSTICUNCOMPRESSED(100, 100, 100, "Uncompressed");	// 100 is a special case for uncompressed
	
	private final int min;
	private final int canonical;
	private final int max;
	private final String humanReadable;
	
	ImageQuality(int min, int canonical, int max, String humanReadable)
	{
		this.min = min;
		this.canonical = canonical;
		this.max = max;
		this.humanReadable = humanReadable;
	}

	public boolean isApplicableQuality(int qvalue)
	{
		return qvalue >= getMin() && qvalue <= getMax();
	}
	
	public int getMin()
	{
		return this.min;
	}
	
	public int getCanonical()
	{
		return this.canonical;
	}

	public int getMax()
	{
		return this.max;
	}
	
	@Override
    public String toString()
    {
	    return humanReadable;
    }

	public static ImageQuality getImageQuality(String qvalue)
	{
		return qvalue == null ? null : getImageQuality(Integer.parseInt(qvalue));
	}
	
	public static ImageQuality getImageQuality(double qValue)
	{
		int intVal = (int)(qValue * 100.0);
		return getImageQuality(intVal);
	}
	
	public static ImageQuality getImageQuality(int qvalue)
	{
		// values must be between 1 and 100
		if ((qvalue < 1) || (qvalue > 100)) 
			qvalue = 100;

		// ALWAYS check DIAGNOSTIC first because that is the safest return
		if( DIAGNOSTIC.isApplicableQuality(qvalue) )
			return DIAGNOSTIC;
		
		if( REFERENCE.isApplicableQuality(qvalue) )
			return REFERENCE;
		
		if( THUMBNAIL.isApplicableQuality(qvalue) )
			return THUMBNAIL;

		if( DIAGNOSTICUNCOMPRESSED.isApplicableQuality(qvalue) )
			return DIAGNOSTICUNCOMPRESSED;
		
		return null;
	}
	
	public static int getImageQualityValue(ImageQuality imageQuality) 
	{
		return imageQuality.getCanonical();
	}
	
	/**
	 * 
	 * @param ordinal
	 * @return
	 */
	public static ImageQuality valueOf(int ordinal)
	{
		for(ImageQuality imageQuality : ImageQuality.values())
			if(imageQuality.ordinal() == ordinal)
				return imageQuality;
		
		return null;
	}
	
}
