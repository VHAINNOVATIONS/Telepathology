/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 26, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging;

import gov.va.med.imaging.url.vista.StringUtils;


/**
 * Identifier that represents a list of images, the identifier comes from CPRS and 
 * is translated into the list of images that are contained by the CPRS identifier
 * 
 * @author vhaiswwerfej
 *
 */
public class CprsIdentifier 
{
	private final String cprsIdentifier;
	
	public CprsIdentifier(String cprsIdentifier)
	{
		this.cprsIdentifier = cprsIdentifier;
	}

	/**
	 * @return the cprsIdentifier
	 */
	public String getCprsIdentifier() {
		return cprsIdentifier;
	}
	
	/**
	 * Get the type of Cprs Identifier represented
	 * @return
	 */
	public CprsIdentifierType getCprsIdentifierType()
	{
		String identifier = StringUtils.MagPiece(cprsIdentifier, StringUtils.CARET, 4);
		for(CprsIdentifierType identifierType : CprsIdentifierType.values())
		{
			if(identifier.equals(identifierType.identifier))
				return identifierType;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return cprsIdentifier;
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CprsIdentifier that = (CprsIdentifier)obj;
		
		return this.cprsIdentifier.equals(that.cprsIdentifier);
	}

	/**
	 * Enum for the possible types of CPRS identifiers supported
	 * @author vhaiswwerfej
	 *
	 */
	public enum CprsIdentifierType
	{
		TIU("TIU"), 
		RAD_EXAM("RA");
		
		final String identifier;
		
		CprsIdentifierType(String identifier)
		{
			this.identifier = identifier;
		}
	}
}
