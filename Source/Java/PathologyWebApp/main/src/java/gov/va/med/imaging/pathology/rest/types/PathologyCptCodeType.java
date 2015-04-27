/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 19, 2012
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
package gov.va.med.imaging.pathology.rest.types;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
@XmlType(propOrder={"cptCode", "description", "multiplyFactor", "dateEntered", "user"})
public class PathologyCptCodeType
{
	private String cptCode;
	private String description;
	private Integer multiplyFactor;
	private Date dateEntered;
	private String user;
	
	public PathologyCptCodeType()
	{
		super();
	}
	
	public PathologyCptCodeType(String cptCode, String description,
			int multiplyFactor, Date dateEntered, String user)
	{
		super();
		this.cptCode = cptCode;
		this.description = description;
		this.multiplyFactor = multiplyFactor;
		this.dateEntered = dateEntered;
		this.user = user;
	}
	
	@XmlElement(nillable=true)
	public String getCptCode()
	{
		return cptCode;
	}
	
	public void setCptCode(String cptCode)
	{
		this.cptCode = cptCode;
	}
	
	@XmlElement(nillable=true)
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@XmlElement(nillable=true)
	public int getMultiplyFactor()
	{
		return multiplyFactor;
	}
	
	public void setMultiplyFactor(int multiplyFactor)
	{
		this.multiplyFactor = multiplyFactor;
	}
	
	@XmlElement(nillable=true)
	public Date getDateEntered()
	{
		return dateEntered;
	}
	
	public void setDateEntered(Date dateEntered)
	{
		this.dateEntered = dateEntered;
	}
	
	@XmlElement(nillable=true)
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
}
