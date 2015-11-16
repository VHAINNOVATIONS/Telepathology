/**
 * 
 */
package gov.va.med.imaging.wado;

public enum ImageContentType
{
	dicom( "application", "dicom" ), 
	jpeg( "image", "jpeg" ), 
	jp2( "image", "jp2" ),
	text("text", "plain"),
	anything("*", "*");
	
	
	public static ImageContentType getContentType(String mimetype)
	{
		String[] mimeParts = null;

		if( mimetype != null)
			mimeParts = mimetype.split("/");
		else
			return null;
		
		return getContentType(mimeParts[0], mimeParts[1]);
	}
	
	public static ImageContentType getContentType(String type, String subtype)
	{
		for(ImageContentType ct:ImageContentType.values())
			if(ct.isMatch(type, subtype))
				return ct;
		
		return null;
	}
	
	private String type = null;
	private String subtype = null;
	ImageContentType(String type, String subtype)
	{
		this.type = type;
		this.subtype = subtype;
	}
	
	boolean isMatch(String type, String subtype)
	{
		return this.type.equalsIgnoreCase(type) && this.subtype.equalsIgnoreCase(subtype);
	}
	
	public String toString()
	{
		return this.type + "/" + this.subtype;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getSubType()
	{
	return this.subtype;
	}
}