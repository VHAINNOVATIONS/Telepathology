/*
 * Created on Apr 26, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author Chris Beckey
 * @since 1.0
 * 
 * 
 */
public class AcceptElementList
extends ArrayList<AcceptElement>
{
	private static final long serialVersionUID = 1566740741436118972L;

	/**
	 * Creates an instance of AcceptElementList and populates its with the parsed
	 * Strings passed as a parameter.
	 * @param acceptHeader
	 * @return
	 * @throws HttpAcceptHeaderParseException
	 */
	public static AcceptElementList parseAcceptElementList(String acceptHeaderValue)
	throws HttpHeaderParseException
	{
		AcceptElementList newAcceptElementList = new AcceptElementList();
		
		if(acceptHeaderValue == null)
			return newAcceptElementList;
		
		String[] acceptHeaderValues = acceptHeaderValue.split(",");
		
		for( int acceptHeaderIndex=0; acceptHeaderIndex < acceptHeaderValues.length; ++acceptHeaderIndex )
		{
			newAcceptElementList.add( acceptHeaderValues[acceptHeaderIndex] );
		}
			
		return newAcceptElementList;
	}

	/**
	 * Given a String array of comma-delimited String containing accept elements, create
	 * a new AcceptElementList and parse the String[] into it.
	 * This factory is intended to be used where the accept header is specified as part
	 * of the URL (as in the WADO contentType parameter).
	 * 
	 * @param rawAcceptElements
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptElementList parseAcceptElementList(String[] rawAcceptElements)
	throws HttpHeaderParseException
	{
		AcceptElementList newAcceptElementList = new AcceptElementList();
		if(rawAcceptElements == null)
			return newAcceptElementList;
		
		for( int index=0; rawAcceptElements != null && index < rawAcceptElements.length; ++index )
			newAcceptElementList.add(rawAcceptElements[index]);
			
		return newAcceptElementList;
	}

	/**
	 * Make the constructor private so that the only way to make
	 * one of these is through the parseAcceptElementList static
	 * method
	 */
	private AcceptElementList()
	{}
	
	private void add(String acceptHeader)
	throws HttpHeaderParseException
	{
		parse(acceptHeader);		
	}
	
	/**
	 * 
	 * @param acceptHeader
	 * @throws HttpHeaderParseException
	 */
	private void parse(String acceptHeader)
	throws HttpHeaderParseException
	{
		for( 
		  StringTokenizer commaTokenizer = new StringTokenizer(acceptHeader, ",");
		  commaTokenizer.hasMoreTokens(); )
			this.add( AcceptElement.parseAcceptElement(commaTokenizer.nextToken()) );
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(64);
	
		for(java.util.Iterator iter = iterator(); iter.hasNext(); )
		{
			if(sb.length() > 0)
				sb.append(",");
			sb.append(iter.next().toString());
		}
		return sb.toString();
	}

	/**
	 * Returns true iff this list is a subset of the 
	 * master list (the parameter).  Specifically this means that for every 
	 * member of this list, there is at least one member of the master list
	 * which logically includes its type.  For example:
	 * 
	 * [image/jpeg,image/png,image/tiff,text/ascii] is a subset of [image/*,text/*]
	 * 
	 * @param masterList
	 * @return
	 */	
	public boolean isEntirelySubsumedIn(AcceptElementList masterList)
	{
		if(this.size() == 0)
			return true;
		
		for(Iterator<AcceptElement> iter=this.iterator(); iter.hasNext(); )
			if(masterList.contains(iter.next()))
				return true;

		System.out.println("AcceptElementList [" + this.toString() + "]");
		System.out.println("is not a subset of [" + masterList.toString() + "]");
		
		return false;
	}
	
	/**
	 * 
	 * @param singleElement
	 * @return
	 */
	public boolean includes(AcceptElement singleElement)
	{
		if(singleElement == null)
			return false;
		
		for(Iterator iter=this.iterator(); iter.hasNext(); )
			if( singleElement.isSubsumedIn((AcceptElement)iter.next()) )
				return true;

		return false;
	}

	/**
	 * 
	 * @param mediaSpecifier
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public boolean includes(String mediaSpecifier) 
	throws HttpHeaderParseException
	{
		AcceptElement element = AcceptElement.parseAcceptElement(mediaSpecifier);
	
		return includes(element);
	}
	
	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		String[][] testCases = {
			{"image/jpeg,image/jpeg;q=0.8"},
			{"image/jpeg;q=0.8,image/jpeg"},
			{"image/*;x=1233;y=1433;q=1.0;n=1;m=\"hello world\""},
			{"image/*;x=1233;y=1433;q=1.0;n=1;m=\"hello world\",image/jpeg,image/jpeg;q=0.8"},
			{"image/jpeg,image/jpeg;q=0.8","image/*;x=1233;y=1433;q=1.0;n=1;m=\"hello world\"" }
		};
		
		try
		{
			for(int index=0; index<testCases.length; ++index)
			{
				System.out.println( testCases[index][0] + "      --> " + 
					AcceptElementList.parseAcceptElementList(testCases[index]).toString() );
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}
}
