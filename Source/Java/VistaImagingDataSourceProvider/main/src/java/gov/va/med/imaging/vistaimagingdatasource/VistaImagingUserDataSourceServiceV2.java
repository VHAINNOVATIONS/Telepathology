/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 8, 2011
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
package gov.va.med.imaging.vistaimagingdatasource;

import java.io.IOException;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingUserDataSourceServiceV2
extends VistaImagingUserDataSourceService
{
	public final static String MAG_REQUIRED_VERSION = "3.0P122";
	
	public VistaImagingUserDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource,
			String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "2";
	}

	@Override
	protected String getRequiredVersion()
	{
		return MAG_REQUIRED_VERSION;
	}

	@Override
	protected boolean userCanAnnotate(VistaSession vistaSession) 
	throws IOException, InvalidVistaCredentialsException, VistaMethodException, MethodException
	{
		VistaQuery query = VistaImagingQueryFactory.createAllowAnnotateQuery();
		String rtn = vistaSession.call(query);
		
		if(rtn.startsWith("1"))
		{
			String [] pieces = StringUtils.Split(rtn, StringUtils.CARET);
			return "1".equals(pieces[1]);
		}
		else
			throw new MethodException("RTN did not start with 1, indicates error: '" + rtn + "'.");
	}

}
