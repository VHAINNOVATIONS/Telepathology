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
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.PassthroughDataSourceSpi;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * A command that blindly calls an RPC method on VistA and passes the 
 * String result back to the client.
 * 
 * @author vhaiswwerfej
 *
 */
public class PostPassthroughMethodCommandImpl 
extends AbstractDataSourceCommandImpl<String, PassthroughDataSourceSpi>
{
	private static final long serialVersionUID = 1L;
	
	private final RoutingToken routingToken;
	private final PassthroughInputMethod inputMethod;
	
	private static final String SPI_METHOD_NAME = "executePassthroughMethod";
	
	/**
	 * 
	 * @param siteNumber
	 * @param inputMethod
	 */
	public PostPassthroughMethodCommandImpl(RoutingToken routingToken, PassthroughInputMethod inputMethod)
	{
		this.routingToken = routingToken;
		this.inputMethod = inputMethod;
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

	/**
	 * @return the inputMethod
	 */
	public PassthroughInputMethod getInputMethod() 
	{
		return inputMethod;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#setTransactionContextFields()
	 */
	@Override
	protected void setTransactionContextFields() 
	{
		super.setTransactionContextFields();
		TransactionContextFactory.get().setItemCached(Boolean.FALSE);
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.inputMethod == null) ? 0 : this.inputMethod.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	/**
	 * Since we cannot determine the idempotency of the called method we must
	 * assume that running the command twice will result in different answers.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
					
		sb.append(this.getSiteNumber());
		sb.append(this.getInputMethod());		
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected String getCommandResult(PassthroughDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.executePassthroughMethod(getRoutingToken(), getInputMethod());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected String postProcessResult(String result) 
	{
		getLogger().info("Got " + (result == null ? "null" : result.length()) + " bytes from passthrough method.");
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<PassthroughDataSourceSpi> getSpiClass() 
	{
		return PassthroughDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getInputMethod()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, PassthroughInputMethod.class};
	}
	
}
