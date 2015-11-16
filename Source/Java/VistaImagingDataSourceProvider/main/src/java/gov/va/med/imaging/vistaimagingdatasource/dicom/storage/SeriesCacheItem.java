package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.business.dicom.Series;

public class SeriesCacheItem extends BaseTimedCacheValueItem
{

	Series series;
	public SeriesCacheItem(Series series)
	{
		this.series = series;
	}

	@Override
	public Object getKey()
	{
		return getCacheKey(series);
	}

	public static Object getCacheKey(Series series)
	{
		// TODO Auto-generated method stub
		return series.getSeriesIUID();
	}

	public Series getSeries()
	{
		return series;
	}
}
