/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 21, 2012
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
package gov.va.med.imaging.pathology.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.pathology.enums.PathologyField;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractDeletePathologyCaseSnomedCodeCommandImpl
extends AbstractPathologyDataSourceCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 6014042259165120753L;
	
	private final PathologyCaseURN pathologyCaseUrn;
	private final String tissueId;
	private final String snomedId;
	private final PathologyField snomedField;
	private final String etiologyId;

	/**
	 * There is no reason to externally call this method will all parameters specified (if deleting an etiology you don't need the snomedField)
	 * @param pathologyCaseUrn
	 * @param tissueId
	 * @param snomedId
	 * @param snomedField
	 * @param etiologyId
	 */
	private AbstractDeletePathologyCaseSnomedCodeCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			String tissueId, String snomedId, PathologyField snomedField, String etiologyId)
	{
		super();
		this.pathologyCaseUrn = pathologyCaseUrn;
		this.tissueId = tissueId;
		this.snomedId = snomedId;
		this.etiologyId = etiologyId;
		this.snomedField = snomedField;
	}
	
	protected AbstractDeletePathologyCaseSnomedCodeCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			String tissueId, String snomedId, String etiologyId)
	{
		this(pathologyCaseUrn, tissueId, snomedId, null, etiologyId);
	}
	
	protected AbstractDeletePathologyCaseSnomedCodeCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			String tissueId, String snomedId, PathologyField snomedField)
	{
		this(pathologyCaseUrn, tissueId, snomedId, snomedField, null);
	}
	
	protected AbstractDeletePathologyCaseSnomedCodeCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			String tissueId)
	{
		this(pathologyCaseUrn, tissueId, null, null, null);
	}

	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	public String getTissueId()
	{
		return tissueId;
	}

	public String getSnomedId()
	{
		return snomedId;
	}

	public PathologyField getSnomedField()
	{
		return snomedField;
	}

	public String getEtiologyId()
	{
		return etiologyId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getRoutingToken()
	 */
	@Override
	public RoutingToken getRoutingToken()
	{
		return getPathologyCaseUrn();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName()
	{
		return "deleteSnomedCode";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[] {PathologyCaseURN.class, String.class, String.class, PathologyField.class, String.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[] {getPathologyCaseUrn(), getTissueId(), getSnomedId(), getSnomedField(), getEtiologyId()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected Void getCommandResult(PathologyDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
		spi.deleteSnomedCode(getPathologyCaseUrn(), getTissueId(), getSnomedId(), getSnomedField(), getEtiologyId());
		return (java.lang.Void)null;
	}

}
