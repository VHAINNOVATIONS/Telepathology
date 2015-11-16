/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 1, 2009
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;

/**
 * Command to get the sensitivity level for a specified patient at a specified site.
 * Will return PatientNotFoundException if the patient is not found. 
 * 
 * @author vhaiswwerfej
 *
 */
public class GetPatientSensitivityLevelCommandImpl 
extends AbstractDataSourceCommandImpl<PatientSensitiveValue, PatientDataSourceSpi>
{
	private static final long serialVersionUID = 3499109552519345441L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier;
	
	private static final String SPI_METHOD_NAME = "getPatientSensitivityLevel";

	public GetPatientSensitivityLevelCommandImpl(RoutingToken routingToken, String patientIcn) 
	{
		this(routingToken, new PatientIdentifier(patientIcn, PatientIdentifierType.icn));
	}
	
	public GetPatientSensitivityLevelCommandImpl(RoutingToken routingToken, PatientIdentifier patientIdentifier) 
	{
		super();
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() 
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getPatientIdentifier());
		sb.append(", ");
		sb.append(getSiteNumber());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected PatientSensitiveValue getCommandResult(PatientDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		PatientSensitiveValue sensitiveLevel = spi.getPatientSensitivityLevel(getRoutingToken(), getPatientIdentifier());
		getLogger().info("Got patient sensitive level [" + (sensitiveLevel == null ? "null" : sensitiveLevel.getSensitiveLevel()) + "] for patient '" + getPatientIdentifier() + "' from site '" + getSiteNumber() + "'");
		return sensitiveLevel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<PatientDataSourceSpi> getSpiClass() 
	{
		return PatientDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getPatientIdentifier()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, PatientIdentifier.class};
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.patientIdentifier == null) ? 0 : this.patientIdentifier.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GetPatientSensitivityLevelCommandImpl other = (GetPatientSensitivityLevelCommandImpl) obj;
		if (this.patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		}
		else if (!this.patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}
	

}
