/**
 * 
 */
package gov.va.med.net;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A collection of static utility methods that are used to
 * manipulate URL instances. 
 * 
 * @author vhaiswbeckec
 *
 */
public class URLUtility
{
	/**
	 * 
	 * @param url
	 * @param protocol
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL changeProtocol(URL url, String protocol)
	throws MalformedURLException
	{
		return build(protocol, url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
	}
	
	public static URL removeUserInfo(URL url)
	throws MalformedURLException
	{
		return changeUserInfo(url, null);
	}
	
	public static URL changeUserInfo(URL url, String userInfo)
	throws MalformedURLException
	{
		return build(url.getProtocol(), userInfo, url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
	}
	
	public static URL changeHost(URL url, String host)
	throws MalformedURLException
	{
		return build(url.getProtocol(), url.getUserInfo(), host, url.getPort(), url.getPath(), url.getQuery(), url.getRef());
	}
	
	public static URL changePort(URL url, int port)
	throws MalformedURLException
	{
		return build(url.getProtocol(), url.getUserInfo(), url.getHost(), port, url.getPath(), url.getQuery(), url.getRef());
	}
	
	public static URL changePath(URL url, String path)
	throws MalformedURLException
	{
		return build(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), path, url.getQuery(), url.getRef());
	}
	
	public static URL changeQuery(URL url, String query)
	throws MalformedURLException
	{
		return build(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), query, url.getRef());
	}
	
	public static URL changeRef(URL url, String ref)
	throws MalformedURLException
	{
		return build(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), ref);
	}
	
	public static URL build(
		String protocol, 
		String userInfo, 
		String host, 
		int port, 
		String path, 
		String query, 
		String ref) 
	throws MalformedURLException
	{
		StringBuilder sb = new StringBuilder();
		
		if(protocol != null)
		{
			sb.append(protocol);
			sb.append(':');
		}
		
		if(host != null)
		{
			sb.append("//");
			if(userInfo != null && host != null)
			{
				sb.append(userInfo);
				sb.append('@');
			}
			sb.append(host);
			
			if(port > 0)
			{
				sb.append(':');
				sb.append(Integer.toString(port));
			}
		}		
		
		if(path != null)
			sb.append(path);
		
		if(query != null)
		{
			sb.append('?');
			sb.append(query);
		}
		
		if(ref != null)
		{
			sb.append('#');
			sb.append(ref);
		}
		
		return new URL(sb.toString());
	}
	
	public static String buildUserInfo(String uid, String pwd)
	{
		return uid == null ? null :
			pwd == null ? uid : 
				(uid + ":" + pwd);
	}
	
	public static String parseUidFromUserInfo(String userInfo)
	{
		if(userInfo == null || userInfo.length() == 0)
			return null;
		String[] userInfoElements = userInfo.split(":");
		return userInfoElements[0];
	}
	
	public static String parsePwdFromUserInfo(String userInfo)
	{
		if(userInfo == null || userInfo.length() == 0)
			return null;
		String[] userInfoElements = userInfo.split(":");
		return userInfoElements.length > 1 && userInfoElements[1].length() > 0 ? userInfoElements[1] : null;
	}
}
