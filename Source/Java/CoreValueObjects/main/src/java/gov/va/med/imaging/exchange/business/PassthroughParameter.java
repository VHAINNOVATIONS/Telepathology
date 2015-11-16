/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 30, 2009
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
package gov.va.med.imaging.exchange.business;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author vhaiswwerfej
 *
 */
public class PassthroughParameter
implements Comparable<PassthroughParameter>, Serializable
{
	private static final long serialVersionUID = 6697771018573530571L;
	
	private int index;
	private String value;
	private String [] multipleValues;
	private PassthroughParameterType parameterType;
	
	public PassthroughParameter()
	{
		parameterType = PassthroughParameterType.literal;
		index = 0;
		value = "";
		multipleValues = null;
	}
	
	public PassthroughParameter(int index, String value, String[] multipleValues, PassthroughParameterType parameterType) 
	{
		super();
		this.index = index;
		this.value = value;
		this.multipleValues = multipleValues;
		this.parameterType = parameterType;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the multipleValues
	 */
	public String[] getMultipleValues() {
		return multipleValues;
	}
	/**
	 * @param multipleValues the multipleValues to set
	 */
	public void setMultipleValues(String[] multipleValues) {
		this.multipleValues = multipleValues;
	}

	/**
	 * @return the parameterType
	 */
	public PassthroughParameterType getParameterType() {
		return parameterType;
	}

	/**
	 * @param parameterType the parameterType to set
	 */
	public void setParameterType(PassthroughParameterType parameterType) {
		this.parameterType = parameterType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PassthroughParameter that) 
	{
		if(this.index < that.index)
			return -1;
		if(this.index > that.index)
			return 1;
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(index);
		sb.append(" - [" + getParameterType() + "]=");
		sb.append("'" + getValue() + "'.");
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.multipleValues);
		result = prime * result + ((this.parameterType == null) ? 0 : this.parameterType.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PassthroughParameter other = (PassthroughParameter) obj;
		if (!Arrays.equals(this.multipleValues, other.multipleValues))
			return false;
		if (this.parameterType == null)
		{
			if (other.parameterType != null)
				return false;
		}
		else if (!this.parameterType.equals(other.parameterType))
			return false;
		if (this.value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!this.value.equals(other.value))
			return false;
		return true;
	}
}
