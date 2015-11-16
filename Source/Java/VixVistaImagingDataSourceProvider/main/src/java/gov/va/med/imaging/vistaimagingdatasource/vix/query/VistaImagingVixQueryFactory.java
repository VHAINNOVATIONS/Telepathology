/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 29, 2011
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
package gov.va.med.imaging.vistaimagingdatasource.vix.query;

import java.util.HashMap;
import java.util.Map;

import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;

/**
 * Factory for creating queries to VistA for the VIX
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingVixQueryFactory
{
	/**
	 * RPC to get the annotations for an image
	 */
	private final static String RPC_MAG_GET_IMAGE_ANNOTATIONS = "MAG ANNOT GET IMAGE";
	/**
	 * RPC to get the details of a specific image annotation layer
	 */
	private final static String RPC_MAG_GET_IMAGE_ANNOTATION_DETAILS = "MAG ANNOT GET IMAGE DETAIL";
	/**
	 * RPC to store image annotation details
	 */
	private final static String RPC_MAG_STORE_IMAGE_ANNOTATION_DETAILS = "MAG ANNOT STORE IMAGE DETAIL";	
	
	/**
	 * Get the list of annotations (layers) for an image
	 * @param imageIen
	 * @return
	 */
	public static VistaQuery createGetImageAnnotationsQuery(String imageIen)
	{
		VistaQuery query = new VistaQuery(RPC_MAG_GET_IMAGE_ANNOTATIONS);
		query.addParameter(VistaQuery.LITERAL, imageIen);
		return query;
	}
	
	/**
	 * Get annotation details (a single layer of XML) for an image
	 * @param imageIen
	 * @param imageAnnotationIen
	 * @return
	 */
	public static VistaQuery createGetImageAnnotationDetailsQuery(String imageIen, 
			String imageAnnotationIen)
	{
		VistaQuery query = new VistaQuery(RPC_MAG_GET_IMAGE_ANNOTATION_DETAILS);
		query.addParameter(VistaQuery.LITERAL, imageIen);
		query.addParameter(VistaQuery.LITERAL, imageAnnotationIen);
		return query;
	}
	
	/**
	 * Store annotation details (a layer) for an image
	 * @param imageIen
	 * @param annotationDetails
	 * @param version
	 * @return
	 */
	public static VistaQuery createStoreImageAnnotationDetailsQuery(String imageIen, 
			String annotationDetails, String version, ImageAnnotationSource annotationSource)
	{
		VistaQuery query = new VistaQuery(RPC_MAG_STORE_IMAGE_ANNOTATION_DETAILS);
		query.addParameter(VistaQuery.LITERAL, imageIen);
		query.addParameter(VistaQuery.LITERAL, annotationSource.getEncodedValue());
		query.addParameter(VistaQuery.LITERAL, version);
		
		Map<String, String> annotationsMap = new HashMap<String, String>();
		String [] lines = StringUtils.Split(annotationDetails, StringUtils.NEW_LINE);
		if(lines != null)
		{
			for(int i = 0; i < lines.length; i++)
			{
				//annotationsMap.put("\"" +  Integer.toString(annotationsMap.size()) + "\"", lines[i].trim());
				// JMW 9/27/2011 - removed trim function to keep extra characters (including new lines) in the database
				annotationsMap.put("\"" +  Integer.toString(annotationsMap.size()) + "\"", lines[i]);
			}			
		}
		
		query.addParameter(VistaQuery.LIST, annotationsMap);
		query.addParameter(VistaQuery.LITERAL, "0");
		return query;
	}
}
