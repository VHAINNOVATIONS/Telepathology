/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.rest.translator.PathologyRestTranslator;
import gov.va.med.imaging.pathology.rest.types.PathologyAcquisitionSitesType;

import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyGetAcquisitionSitesCommand
extends AbstractPathologyGetSitesCommand<PathologyAcquisitionSitesType>
{
	/**
	 * @param methodName
	 */
	public PathologyGetAcquisitionSitesCommand(String siteId)
	{
		super(siteId, false);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected PathologyAcquisitionSitesType translateRouterResult(
			List<AbstractPathologySite> routerResult)
	throws TranslationException, MethodException
	{
		return PathologyRestTranslator.translateAcquisitionSites(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyAcquisitionSitesType> getResultClass()
	{
		return PathologyAcquisitionSitesType.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getEntriesReturned(java.lang.Object)
	 */
	@Override
	public Integer getEntriesReturned(PathologyAcquisitionSitesType translatedResult)
	{
		return translatedResult == null ? 0 : (translatedResult.getSite() == null ? 0 : translatedResult.getSite().length );
	}

}
