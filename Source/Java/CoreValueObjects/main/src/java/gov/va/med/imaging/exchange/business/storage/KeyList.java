/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.exchange.business.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KeyList implements Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private List<Key> keyList = new ArrayList<Key>();
	
	//
	// Default Constructor
	//
	public KeyList()
	{
	}

	//
	// Additional Constructors
	//
	public KeyList(int id, List<Key> keyList) {
		super();
		this.id = id;
		this.keyList = keyList;
	}

	// 
	// Properties
	//
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Key> getKeyList() 
	{
		// Sort it first, before returning
		List<Key> sortedList = new ArrayList<Key>();
		for (int i=1; i<=keyList.size(); i++)
		{
			for(Key key : keyList)
			{
				if (key.getLevel() == i) sortedList.add(key);
			}
		}
		keyList = sortedList;
		return keyList;
	}

	public void setKeyList(List<Key> keyList) {
		this.keyList = keyList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
