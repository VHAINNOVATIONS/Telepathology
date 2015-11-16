/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 14, 2006
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
package gov.va.med.imaging.exchange.business;

import java.io.Serializable;
import java.util.Comparator;


/**
 * 
 * A Comparator class to assure that the Study remains in order of ien relation 
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SeriesComparator implements Comparator<Series>, Serializable {
	
	private static final long serialVersionUID = 4739063635641777002L;

	public int compare(Series series1, Series series2)
	{
		// JMW 3/12/08 - reversing the order of the sorts (was causing the series to be in descending order instead of ascending)
		int ienRelation = series1.getSeriesIen().compareTo(series2.getSeriesIen());
		int numberRelation = 0;
		int seriesUidRelation = series1.getSeriesUid().compareTo(series2.getSeriesUid());
		
		try
		{
			Integer series1Number = Integer.parseInt(series1.getSeriesNumber());
			Integer series2Number = Integer.parseInt(series2.getSeriesNumber());
			numberRelation = series1Number.compareTo(series2Number);
		}
		catch(Exception ex)
		{
			if(series1.getSeriesNumber() != null)
			{
				numberRelation = series1.getSeriesNumber().compareTo(series2.getSeriesNumber());
			}
		}
		
		return numberRelation != 0 ? numberRelation :
			ienRelation != 0 ? ienRelation :
				seriesUidRelation;
		/*
		return ienRelation != 0 ? ienRelation :
			seriesUidRelation != 0 ? seriesUidRelation :
				numberRelation;
				*/
	}
}
