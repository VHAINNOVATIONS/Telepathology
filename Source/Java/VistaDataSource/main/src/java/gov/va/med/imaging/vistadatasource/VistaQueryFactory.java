/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 6, 2009
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
package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.url.vista.VistaQuery;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaQueryFactory 
{
	private final static String RPC_XUS_SET_VISITOR = "XUS SET VISITOR";
	
	/**
	 * Create a query using an RPC that does not use MAG rpc calls.
	 * @return
	 */
	public static VistaQuery createGetBrokerTokenQuery()
	{
		VistaQuery vm = new VistaQuery(RPC_XUS_SET_VISITOR);
		
		return vm;
	}
}
