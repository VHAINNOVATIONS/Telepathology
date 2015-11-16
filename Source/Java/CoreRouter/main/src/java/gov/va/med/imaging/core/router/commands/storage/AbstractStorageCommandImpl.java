/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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
package gov.va.med.imaging.core.router.commands.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.core.router.storage.StorageDataSourceRouter;
import gov.va.med.imaging.core.router.storage.providers.StorageProviderFactory;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;

/**
 * An abstract superclass of Exam-related commands, grouped because there is significant
 * overlap in the Exam commands that is contained here.
 * 
 * @author vhaiswlouthj
 *
 */
public abstract class AbstractStorageCommandImpl<R extends Object> 
extends AbstractCommandImpl<R>
{
    protected StorageDataSourceRouter dataSourceRouter = StorageContext.getDataSourceRouter();

	/**
	 * @param commandContext - the context available to the command
	 */
	public AbstractStorageCommandImpl()
	{
		super();
	}
	@Override
	public boolean equals(Object obj)
	{
		// Check objectEquivalence
		if (this == obj)
		{
			return true;
		}
		
		// Check that classes match
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		return areClassSpecificFieldsEqual(obj);
		
	}

	protected abstract boolean areClassSpecificFieldsEqual(Object obj);

	public boolean areFieldsEqual(Object field1, Object field2)
	{
		// Check the study URN
		if (field1 == null)
		{
			if (field2 != null)
			{
				return false;
			}
		} 
		else if (!field1.equals(field2))
		{
			return false;
		}
		
		return true;
	}
	
}
