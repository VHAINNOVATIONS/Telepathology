/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2011
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
package gov.va.med.imaging.core.router.commands.provider;

import gov.va.med.imaging.core.CommandCreatorProvider;
import gov.va.med.imaging.core.interfaces.router.CommandContext;

/**
 * @author VHAISWWERFEJ
 *
 */
public class CoreCommandCreatorProvider 
extends CommandCreatorProvider
{

	public CoreCommandCreatorProvider()
	{
		// must have default constructor
	}

	@Override
	protected String[] getCommandPackageNames() 
	{
		return new String []
           {
				"gov.va.med.imaging.core.router.commands",
				"gov.va.med.imaging.core.router.commands.datasource",
				"gov.va.med.imaging.core.router.commands.dicom.importer",
				"gov.va.med.imaging.core.router.commands.storage",
				"gov.va.med.imaging.core.router.commands.storage.datasource",
				"gov.va.med.imaging.core.router.commands.worklist"
           };
	}
	
	private static CommandContext localCommandContext = null;
	private synchronized static CommandContext getLocalCommandContext(CommandContext baseCommandContext)
	{
		if(localCommandContext == null)
		{
			localCommandContext = new CoreCommandContext(baseCommandContext);
		}
		return localCommandContext;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.CommandFactoryProviderImpl#getCommandContext(gov.va.med.imaging.core.interfaces.router.CommandContext)
	 */
	@Override
	protected CommandContext getCommandContext(CommandContext baseCommandContext) 
	{
		// override command context if necessary
		return getLocalCommandContext(baseCommandContext);
	}
}
