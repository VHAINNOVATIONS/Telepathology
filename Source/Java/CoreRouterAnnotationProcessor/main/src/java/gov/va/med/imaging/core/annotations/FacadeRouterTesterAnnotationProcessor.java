/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 6, 2011
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
package gov.va.med.imaging.core.annotations;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * @author VHAISWWERFEJ
 *
 */
@SupportedAnnotationTypes("gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions("gov.va.med.imaging.mock")
public class FacadeRouterTesterAnnotationProcessor 
extends FacadeRouterAnnotationProcessor
implements Processor
{
	public FacadeRouterTesterAnnotationProcessor()
	{
		super();
		setGenerateTests(true);
	}
}
