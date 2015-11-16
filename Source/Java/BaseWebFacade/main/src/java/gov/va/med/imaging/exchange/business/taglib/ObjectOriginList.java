package gov.va.med.imaging.exchange.business.taglib;

import gov.va.med.imaging.exchange.enums.ObjectOrigin;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class ObjectOriginList 
extends StringArrayList
{
	private static final long serialVersionUID = 1L;
	
	private String[] listValues = new String[]{"UNSPECIFIED", "VA", "NON-VA", "DOD", "FEE"};
	
	protected String[] getListValues()
	{
		return listValues;
	}
}
