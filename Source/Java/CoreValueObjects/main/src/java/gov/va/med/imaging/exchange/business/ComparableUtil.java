/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 14, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.business;

import java.util.Date;

/**
 * Just a collection of static methods to make Comparable easier to
 * implement.
 * 
 * The methods in this class are all public static and all take three args:
 * the first two args are the things to compare and must always be the same
 * type
 * the third arg is a boolean indicating ascending sort if true, else descending.
 * 
 * Regardless of sort order, null args are after non-null args.
 * 
 * If the sort order is ascending then:
 * The return values are -1, 0, or 1 if this value is less than, equal to, or greater
 * than that value.
 * 
 * If the sort order is descending then:
 * The return values are 1, 0, or -1 if this value is less than, equal to, or greater
 * than that value.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ComparableUtil
{

	/**
	 * 
	 * @param thisDate
	 * @param thatDate
	 * @param ascending
	 * @return -1, 0, 1 if ascending is true and thisDate is less than, equal to, or greater than thatDate
	 */
	public static int compare(Date thisDate, Date thatDate, boolean ascending)
    {
    	int comparison = 
    		thisDate == null ?					// if this is null
    		( thatDate == null ? 0 : 1 ) :		// if this is null and that is null then 0,
    											// if this is null and that is not null then 1
        	thatDate == null ? -1 : 			// if that is null then -1 (this is NOT null)
        	ascending ? thisDate.compareTo(thatDate) : (-1 * thisDate.compareTo(thatDate));
        										// neither is null, do a regular compare and then reverse order for descending
    	return comparison;
    }

	/**
	 * Compare two, possibly null String instances:
	 * In all cases nulls come after non-null
	 * If ascending is true 
	 * -1, 0, 1 thisString is less than, equal to, or greater than thatString
	 * If ascending is false 
	 * 1, 0, -1 thisString is less than, equal to, or greater than thatString
	 * 
	 * @param thisString
	 * @param thatString
	 * @param ascending
	 * @return -1, 0, 1 if ascending is true and thisString is less than, equal to, or greater than thatString
	 */
	public static int compare(String thisString, String thatString, boolean ascending)
    {
    	int comparison = 
    		thisString == null ?				// if this is null
    		( thatString == null ? 0 : 1 ) :	// if this is null and that is null then 0, 
    											// if this is null and that is not null then 1
    		thatString == null ? -1 : 			// if that is null then -1 (this is NOT null)
    		ascending ? thisString.compareTo(thatString) : (-1 * thisString.compareTo(thatString));
    											// neither is null, do a regular compare and then reverse order for descending
    	return comparison;
    }

}
