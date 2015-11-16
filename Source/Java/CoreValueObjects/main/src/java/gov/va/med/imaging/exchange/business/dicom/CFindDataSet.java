/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

*/
package gov.va.med.imaging.exchange.business.dicom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author vhaiswlouthj
 *
 */
@Deprecated
public class CFindDataSet {

	private Set<CFindElement> elements = new TreeSet<CFindElement>();
	
	public void addElement(CFindElement element)
	{
		elements.add(element);
	}
	
	public int getNumberOfElements()
	{
		return elements.size();
	}
	
	public Iterator<CFindElement> getElementIterator()
	{
		return elements.iterator();
	}
	
	public HashMap<String, String> getElementsAsHashMap()
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		// Add each element to the map
		for(CFindElement element : elements)
		{
			map.put(element.getTag(), element.getValue());
		}
		
		return map;
	}
	
}
