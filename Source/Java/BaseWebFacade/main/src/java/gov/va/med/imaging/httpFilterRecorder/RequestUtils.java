/**
 * License:
 * Copyright (c) 2009 Christopher Schultz
 * Free to use for any purpose for no fee. No guarantees. Credits and shout-outs are appreciated.
 * 
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 26, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging.httpFilterRecorder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;

public class RequestUtils
{
	public static String getPostQueryString(HttpServletRequest req)
	{
		try
		{
			return getPostQueryString(req, "UTF-8");
		}
		catch (UnsupportedEncodingException uee)
		{
			throw new InternalError("UTF-8 charset is suddenly unsupported");
		}
	}

	public static String getPostQueryString(HttpServletRequest req, String uriCharset)
		throws UnsupportedEncodingException
	{
		if (!("POST".equals(req.getMethod()) && "application/x-www-form-urlencoded".equals(req.getContentType())))
			throw new IllegalArgumentException("Request must be both POST and application/x-www-urlencoded");

		StringBuffer sb = new StringBuffer();

		Map<String, Integer> queryParameterCounts = countQueryParameters(req.getQueryString());

		boolean first = true;

		for (Iterator<Map.Entry<?,?>> i = req.getParameterMap().entrySet().iterator(); i.hasNext();)
		{
			Map.Entry<?,?> entry = i.next();

			String paramName = (String) entry.getKey();
			String[] values = (String[]) entry.getValue();

			int skip = 0;
			Integer queryCount = queryParameterCounts.get(paramName);

			if (null != queryCount)
				skip += queryCount.intValue();

			if (values.length > skip)
			{
				for (int j = skip; j < values.length; ++j)
				{
					if (first)
						first = false;
					else
						sb.append('&');

					sb.append(URLEncoder.encode(paramName, uriCharset)).append('=').append(
						URLEncoder.encode(values[j], uriCharset));
				}
			}
		}

		return sb.toString();
	}

	private static Map<String,Integer> countQueryParameters(String query)
	{
		HashMap<String,Integer> counts = new HashMap<String,Integer>();

		if (null != query)
		{
			String[] paramNames = query.split("=[^&]*(&)?");

			for (int i = 0; i < paramNames.length; ++i)
			{
				Integer count = (Integer) counts.get(paramNames[i]);

				if (null == count)
					count = new Integer(1);
				else
					count = new Integer(count.intValue() + 1);

				counts.put(paramNames[i], count);
			}
		}

		return counts;
	}
}