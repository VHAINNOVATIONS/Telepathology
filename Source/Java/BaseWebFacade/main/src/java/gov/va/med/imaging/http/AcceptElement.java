/*
 * Created on Apr 26, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.*;

import java.util.*;

/**
 * A class that encapsulates accept types and quality in such a way that
 * they can be properly compared and sorted.
 * 
 * READ THIS CAREFULLY, especially the description of what differentiates
 * the media parameters from the accept parameters (basically the letter 'q').
 *
 * From the HTTP 1.1 Specification (ftp://ftp.isi.edu/in-notes/rfc2616.txt)
 * ========================================================================
 * The Accept request-header field can be used to specify certain media
 * types which are acceptable for the response. Accept headers can be
 * used to indicate that the request is specifically limited to a small
 * set of desired types, as in the case of a request for an in-line
 * image.
 *
 *     Accept         = "Accept" ":"
 *                      #( media-range [ accept-params ] )
 *
 *     media-range    = ( "* / *"
 *                      | ( type "/" "*" )
 *                      | ( type "/" subtype )
 *                      ) *( ";" parameter )
 *     accept-params  = ";" "q" "=" qvalue *( accept-extension )
 *     accept-extension = ";" token [ "=" ( token | quoted-string ) ]
 *
 *     parameter               = attribute "=" value
 *     attribute               = token
 *     value                   = token | quoted-string
 * 
 * The asterisk "*" character is used to group media types into ranges,
 * with "* / *" indicating all media types and "type/*" indicating all
 * subtypes of that type. The media-range MAY include media type
 * parameters that are applicable to that range.
 *
 * Each media-range MAY be followed by one or more accept-params,
 * beginning with the "q" parameter for indicating a relative quality
 * factor. The first "q" parameter (if any) separates the media-range
 * parameter(s) from the accept-params. Quality factors allow the user
 * or user agent to indicate the relative degree of preference for that
 * media-range, using the qvalue scale from 0 to 1 (section 3.9). The
 * default value is q=1.
 * ========================================================================
 * 
 * Specific semantics for this implementation:
 * property mediaRange includes the type, subtype, media-parameters
 * property type is the fully parsed type, defaulted to "*"
 * property subtype is the fully parsed subtype, defaulted to "*"
 * property qValue (HttpQValueHeaderModifier) is the value of the qvalue, default to 1
 * property mediaParameters is a list of the semicolon seperated values from the subtype to ";q="
 * property acceptExtensions is a map of everything following the qValue, elements seperated by semicolons 
 * 
 * @author Chris Beckey
 */
public class AcceptElement
implements Cloneable, Comparable
{
	public static final String extensionDelimiter = ",";
	
	private AcceptMediaRange mediaRange = null;
	private HttpParameterList mediaParameters = new HttpParameterList();	// maintains order as added
	private QValueAcceptParameter qValue = null;
	private HttpParameterList acceptExtensions = new HttpParameterList();	// maintains order as added
	
	/**
	 * This method, and any other that creates new AcceptElement instances must 
	 * assure that the MediaRange and qValue are not null by the return.
	 * 
	 * @param rawAcceptElement
	 * @return
	 */
	public static AcceptElement parseAcceptElement(String rawAcceptElement)
	throws HttpHeaderParseException
	{
		AcceptElement newElement = new AcceptElement();
		newElement.parse(rawAcceptElement);

		if(newElement.getAcceptMediaRange() == null)
			newElement.setAcceptMediaRange(AcceptMediaRange.createAcceptMediaRange());

		if(newElement.getQValue() == null)
			newElement.setQValue(QValueAcceptParameter.createQValueParameter(QValueAcceptParameter.maxQValue) );
		
		return newElement;
		
	}
	
	/**
	 * Clones the passed in parameters and then creates an AcceptElement with those 
	 * values.  This makes a copy to avoid issues with mutability. 
	 * This method, and any other that creates new AcceptElement instances must 
	 * assure that the MediaRange and qValue are not null by the return.
	 * 
	 * @param acceptMediaRange
	 * @param qValue
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptElement createAcceptElement(AcceptMediaRange acceptMediaRange, QValueAcceptParameter qValue)
	throws HttpHeaderParseException
	{
		AcceptElement newElement = new AcceptElement();
		try
		{
			if(acceptMediaRange != null)
				newElement.setAcceptMediaRange((AcceptMediaRange)acceptMediaRange.clone());
			else
				newElement.setAcceptMediaRange(AcceptMediaRange.createAcceptMediaRange());
				
			if( qValue != null )
				newElement.setQValue((QValueAcceptParameter)qValue.clone());
			else
				newElement.setQValue(QValueAcceptParameter.createQValueParameter(QValueAcceptParameter.maxQValue));
		}
		catch (CloneNotSupportedException cnsX)
		{
			cnsX.printStackTrace();
		}
		
		return newElement;
	}
	
	/**
	 * Create an HttpAccept with the specified mime type and a specified quality as a double
	 * @param qualityValue
	 * @param acceptMimeType
	 */
	public static AcceptElement createAcceptElement(String acceptMediaType, double qValue)
	throws HttpHeaderParseException
	{
		AcceptElement newElement = new AcceptElement();
		newElement.setAcceptMediaRange(AcceptMediaRange.parseAcceptMediaRange(acceptMediaType));
		newElement.setQValue(QValueAcceptParameter.createQValueParameter(qValue));
		
		return newElement;
	}

	/**
	 * Create an HttpAccept with the specified mime type and a specified quality as a type
	 * @param qualityValue
	 * @param acceptMimeType
	 */
	public static AcceptElement createAcceptElement(String acceptMediaType, QValueAcceptParameter qValue)
	throws HttpHeaderParseException
	{
		AcceptElement newElement = new AcceptElement();
		newElement.setAcceptMediaRange( AcceptMediaRange.parseAcceptMediaRange(acceptMediaType) );
		try
		{
			if( qValue != null )
				newElement.setQValue((QValueAcceptParameter)qValue.clone());
			else
				newElement.setQValue(QValueAcceptParameter.createQValueParameter(QValueAcceptParameter.maxQValue));
		}
		catch (CloneNotSupportedException cnsX)
		{
			cnsX.printStackTrace();
		}
		
		return newElement;
	}

	/**
	 * Make this private so that the above static create methods must be used
	 */
	private AcceptElement()
	{}
	
	/**
	 * @return Returns the acceptMimeType as an AcceptMediaRange instance.
	 * Use getMediaType and getMediaSubType to get the String components.
	 */
	public AcceptMediaRange getAcceptMediaRange()
	{
		return mediaRange;
	}
	
	public String getMediaType()
	{
		return mediaRange.getType();
	}
	
	public String getMediaSubType()
	{
		return mediaRange.getSubType();
	}
	
	private void setAcceptMediaRange(AcceptMediaRange mediaRange)
	{
		this.mediaRange = mediaRange;
	}
	
	/**
	 * @return
	 */
	public QValueAcceptParameter getQValue()
	{
		return qValue;
	}
	
	public double getQualityValue()
	{
		return qValue.getQualityValue();
	}

	/**
	 * @param modifier
	 */
	protected void setQValue(QValueAcceptParameter qValue)
	{
		this.qValue = qValue;
	}

	/**
	 * Just a clone, nothing but a clone.
	 * 
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return createAcceptElement(this.getAcceptMediaRange(), this.getQValue());
		}
		catch (HttpHeaderParseException e)
		{
			// this should never occur because the values should all be valid
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Return an element string in the correct format (i.e. element;q=1.0)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(64);	// 64 is an optimization, not magic
		
		sb.append(getAcceptMediaRange().toString());
		sb.append(";");
		
		// iterate through the media-range parameters and append them
		String mediaParametersString = mediaParameters.toString();
		sb.append(mediaParametersString);
		if(mediaParametersString.length() > 0)
			sb.append(";");
	
		// append the qValue
		sb.append(getQValue().toString());

		// iterate through the accept-extension and append them
		String extensionsString = acceptExtensions.toString();
		if(extensionsString.length() > 0)
			sb.append(";");
		sb.append(extensionsString);
		
		return sb.toString();
	}

	/**
	 * Implements a non case-specific comparison on the
	 * accept type and an equality test on the quality value.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof AcceptElement)
		{
			AcceptElement that = (AcceptElement)obj;
			return 
				this.getAcceptMediaRange().equals(that.getAcceptMediaRange()) &&
				this.getQValue().equals(that.getQValue());
		}
		
		return false;
	}
	
	/**
	 * Return true if this AcceptElement is logically subsumed by the
	 * parent AcceptElement.  For example:
	 * 
	 * [x/y].isIncludedIn([x /*]) is true
	 * [x/y].isIncludedIn([* /*]) is true
	 * [x/*].isIncludedIn([x /*]) is true
	 * [x/*].isIncludedIn([* /*]) is true
	 * 
	 * Partial wildcards are not supported (i.e. x* /y* is an invalid type)
	 * 
	 * @param parent
	 * @return
	 */
	public boolean isSubsumedIn(AcceptElement parent)
	{
		return this.getAcceptMediaRange().isSubsumedIn(parent.getAcceptMediaRange());
	}

	/**
	 * Implements an inverse comparison on the quality value..
	 * If the quality value is equal then compares the 
	 * acceptMimeType by dictionary order.
	 * Note that the purpose of this comparison is to assure that
	 * the highest quality comes first.  MimeAccept is used here just to
	 * keep equals semantics correct.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * From the spec - 
	 * Compares this object with the specified object for order. 
	 * Returns a negative integer, zero, or a positive integer as this object 
	 * is less than, equal to, or greater than the specified object.
	 * 
	 */
	public int compareTo(Object obj)
	{
		if(obj instanceof AcceptElement)
		{
			AcceptElement that = (AcceptElement)obj;
			int qValueCompare = this.getQValue().compareTo(that.getQValue());
			return 
				qValueCompare != 0 ? 
				qValueCompare :
				this.getAcceptMediaRange().compareTo(that.getAcceptMediaRange());
		}
		return 1;
	}

	/* 
	 * ============= The Parser ===========================
	 */
	private static final int tokenizeModeMediaRange = 0;
	private static final int tokenizeModeMediaParameter = 1;
	private static final int tokenizeModeGenericExtension = 2;

	/**
	 * @param parseElement is a String in the form defined by RFC2616 for an
	 * HTTP request header accept element, as shown below. 
	 * 
	 * Note that what differentiates the media-range parameters from the
	 * accep-params is that the accept-params must start with a qvalue,
	 * which is basically a accept-extension with an attribute of "q".
	 * 
	 *     #( media-range [ accept-params ] )
	 *
	 *     media-range    = ( "* / *"
	 *                      | ( type "/" "*" )
	 *                      | ( type "/" subtype )
	 *                      ) *( ";" parameter )
	 *     accept-params  = ";" "q" "=" qvalue *( accept-extension )
	 *     accept-extension = ";" token [ "=" ( token | quoted-string ) ]
	 * 
	 *     parameter        = attribute "=" value
	 *     attribute        = token
	 *     value            = token | quoted-string
	 * 
	 * Examples:
	 * 'image/jpeg'						media-type="image" media-subtype="jpeg" qValue=1.0(default)
	 * 'image/jpeg;q=0.8'				media-type="image" media-subtype="jpeg" qValue=0.8
	 * 'image/*;x=1233;y=1433;q=1.0;n=1;m="hello world"' 
	 *                                  media-type="image" media-subtype="*" 
	 *                                  media-range-parameter=["x"="1233", "y"="1433"] 
	 *                                  qValue=1.0
	 *                                  accept-extensions=["n"="1", "m"="hello world"
	 * 
	 * Operation of this method:
	 * 1.) parse the parameter by semicolons
	 * 2.) iterate through the parsed tokens and assign to values as follows:
	 * 2a.) the first token is always the media range
	 * 2b.) create a AcceptParameter from the token
	 * 2c.) the token is a media-range-parameter, a qValue or an accept-extension depending on:
	 *      if its parameter is "q" and there is no qValue defined yet then it is a qValue
	 *      if a qValue has been defined then it is an accept-extension
	 *      else it is a media-range-parameter
	 */
	protected void parse(String parseElement)
	throws HttpHeaderParseException
	{
		int mode = tokenizeModeMediaRange;		// this is used to track what has been found so far
		// tokenize on semicolons, 
		// the first token will always be the media range
		String[] elementComponents = parseElement.split(";");
		for( int elementComponentIndex=0; elementComponentIndex < elementComponents.length; ++elementComponentIndex ) 
		{
			String token = elementComponents[elementComponentIndex];
			if(token == null)
				continue;
			
			token = token.trim();
			switch(mode)
			{
				// the first thing we find must be the media range
				case tokenizeModeMediaRange:
					this.setAcceptMediaRange(
						AcceptMediaRange.parseAcceptMediaRange(token)
					);
					mode = tokenizeModeMediaParameter;
					break;

				// everything after the media range is a media parameter until
				// we find a parameter starting a 'q', which is a special extension type					
				case tokenizeModeMediaParameter:
					if( token.startsWith("q") )
					{
						this.setQValue(QValueAcceptParameter.parseQValueParameter(token));
						mode = tokenizeModeGenericExtension;
					}
					else
					{
						this.parseAndAddMediaParameter(token);
					}
					break;
					
				case tokenizeModeGenericExtension:
					this.parseAndAddExtension(token);
					break;
			}
		}
		
	}
	
	/**
	 * @param parameter
	 */
	private void parseAndAddMediaParameter(String parameter)
	throws HttpHeaderParseException
	{
		HttpParameter acceptParameter = HttpParameter.parseHttpParameter(parameter);
		this.mediaParameters.addLast(acceptParameter);
	}

	/**
	 * 
	 * @param parameter
	 * @throws HttpAcceptHeaderParseException
	 */	
	private void parseAndAddExtension(String parameter)
	throws HttpHeaderParseException
	{
		AcceptParameter acceptParameter = AcceptParameter.parseAcceptParameter(parameter);
		this.acceptExtensions.addLast(acceptParameter);
	}

	/**
	 * A simple test driver 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		try
		{
			System.out.println( "image/jpeg      --> " + AcceptElement.parseAcceptElement("image/jpeg").toString() );
			System.out.println( "image/jpeg;q=0.8--> " + AcceptElement.parseAcceptElement("image/jpeg;q=0.8").toString() );
			System.out.println( "image/*;x=1233;y=1433;q=1.0;n=1;m=\"hello world\" --> " + 
				AcceptElement.parseAcceptElement("image/*;x=1233;y=1433;q=1.0;n=1;m=\"hello world\"")); 
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
}
