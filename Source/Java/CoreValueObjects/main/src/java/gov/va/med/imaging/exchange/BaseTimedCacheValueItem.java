/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 26, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.exchange;

/**
 * @author VHAISWWERFEJ
 *
 */
public class BaseTimedCacheValueItem {
	
	protected long refreshTime;
	protected Object key;
	
	public BaseTimedCacheValueItem() {
		super();
		refreshTime = System.currentTimeMillis();
		key = null;
	}

	/**
	 * @return the refreshTime
	 */
	public long getRefreshTime() {
		return refreshTime;
	}
	
	public void updateRefreshTime() {
		refreshTime = System.currentTimeMillis();
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}
	

}
