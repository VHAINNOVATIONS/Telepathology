/*
 * Originally AcceptMediaRange.java 
 * created on Nov 22, 2004 @ 2:48:47 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import java.util.StringTokenizer;
import gov.va.med.imaging.http.exceptions.*;


/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 2:48:47 PM
 *
 * The part of a accept header element that is strictly the type and subtype.
 * 
 *     media-range    = ( "* / *"
 *                      | ( type "/" "*" )
 *                      | ( type "/" subtype )
 *                      ) 
 */
public class AcceptMediaRange
implements Cloneable, Comparable
{
	public final static String wildcardType="*";
	public final static String wildcardSubType="*";
	public static final String typeDelimiter = "/";
	
	public final static String defaultType=wildcardType;
	public final static String defaultSubType=wildcardSubType;
	
	private String type = defaultType;
	private String subType = defaultSubType;
	
	/**
	 * Parse a String in the form type/subtype and create a AcceptMediaRange with those
	 * values.
	 * 
	 * @param mediaRange
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptMediaRange parseAcceptMediaRange(String mediaRange)
	throws HttpHeaderParseException
	{
		AcceptMediaRange newMediaRange = new AcceptMediaRange();
		newMediaRange.parse(mediaRange);
		
		return newMediaRange;
	}

	/**
	 * Create a AcceptMediaRange given the type and subtype as Strings
	 * @param mediaType
	 * @param mediaSubType
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptMediaRange createAcceptMediaRange(String mediaType, String mediaSubType)
	throws HttpHeaderParseException
	{
		AcceptMediaRange newMediaRange = new AcceptMediaRange();
		newMediaRange.setType(mediaType);
		newMediaRange.setSubType(mediaSubType);
		
		return newMediaRange;
	}

	/**
	 * Create an AcceptMediaRange with default values, "* / *"
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptMediaRange createAcceptMediaRange()
	throws HttpHeaderParseException
	{
		AcceptMediaRange newMediaRange = new AcceptMediaRange();
		newMediaRange.setType(defaultType);
		newMediaRange.setSubType(defaultSubType);
		
		return newMediaRange;
	}
	
	private AcceptMediaRange() 
	{}

	/**
	 * @return
	 */
	public String getSubType()
	{
		return subType;
	}

	/**
	 * @return
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param string
	 */
	private void setSubType(String string)
	{
		subType = string;
	}

	/**
	 * @param string
	 */
	private void setType(String string)
	{
		type = string;
	}
	
	/**
	 * 
	 * @param mediaRange
	 * @throws HttpAcceptHeaderParseException
	 */
	private void parse(String mediaRange)
	throws HttpHeaderParseException
	{
		StringTokenizer mediaRangeTokenizer = new StringTokenizer(mediaRange, "/");

		if(mediaRangeTokenizer.hasMoreTokens())
		{	
			setType( mediaRangeTokenizer.nextToken().trim() );
			if(mediaRangeTokenizer.hasMoreTokens())
				setSubType( mediaRangeTokenizer.nextToken().trim() );
			else
				throw new HttpAcceptMediaRangeParseException("No accept subtype specified '" + mediaRange + "' and it is required.");
		}
		else
			throw new HttpAcceptMediaRangeParseException("No accept type specified, media range is " + 
				(mediaRange==null ? "null" : "blank") + ".");
	}
	
	/**
	 * More specific types come before less specific types, otherwise return
	 * character-set ordering (i.e. alphabetical) 
	 * (note that spaces have been added so the examples don't look like commas)
	 * 
	 * Examples:
	 * image/jpeg   <   image/png 
	 * image/jpeg   <   image /*
	 * image/*      <   * / *
	 * 
	 * return < 0 if this is less than that
	 * return > 0 if this greater than that
	 * return 0 if this is equal to that
	 * 
	 * If the types differ then return a greater magnitude difference than if just
	 * the subtypes differ.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if(o instanceof String)
		{
			try
			{
				return compareTo( parseAcceptMediaRange((String)o) );
			}
			catch (HttpHeaderParseException e)
			{
				throw new ClassCastException("Attempt to compare String that was not in media range format");
			}
		}
		
		AcceptMediaRange that = (AcceptMediaRange)o;
		
		// if this and that are equal return 0
		if(this.equals(that))
			return 0;

		// if the types equal, but this and that are not equal then the subtypes must differ					
		if(this.getType().equals(that.getType()))
		{
			if( wildcardType.equals(this.getSubType()) )
				return 1;
			if( wildcardType.equals(that.getSubType()) )
				return -1;
			return this.getSubType().compareTo(that.getSubType());
		}
		else
		{
			if( wildcardType.equals(this.getType()) )
				return 2;
			if( wildcardType.equals(that.getType()) )
				return -2;
			return this.getType().compareTo(that.getType()) * 2;
		}
	}

	/**
	 * Return true if the type and subtype are equals.  This method does not
	 * consider wildcards.
	 */
	public boolean equals(Object o)
	{
		if(o instanceof AcceptMediaRange)
		{
			AcceptMediaRange that = (AcceptMediaRange)o;
			return this.getType().equals(that.getType()) &&
			       this.getSubType().equals(that.getSubType());
		}
		
		return false;
	}

	/**
	 * Return true if this AcceptMediaRange is logically subsumed by 
	 * that AcceptMediaRange.  For example:
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
	public boolean isSubsumedIn(AcceptMediaRange that)
	{
		// if that Type is * then it matches all
		if( AcceptMediaRange.wildcardType.equals(that.getType()) )
			return true;
		// if that Type is not * then this Type must equal that Type
		// and that Subtype must be * or 
		// that SubType must equal this SubType
		if( this.getType().equalsIgnoreCase(that.getType()) &&
		    (AcceptMediaRange.wildcardSubType.equals(that.getSubType()) ||
		     this.getSubType().equalsIgnoreCase(that.getSubType()) ))
		    	return true;
		    	
		// if no matches then that does not subsume this
		return false;
	}



	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return createAcceptMediaRange(getType(), getSubType());
		}
		catch (HttpHeaderParseException pX)
		{
			throw new CloneNotSupportedException(pX.getMessage());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getType() + "/" + getSubType();
	}

}
