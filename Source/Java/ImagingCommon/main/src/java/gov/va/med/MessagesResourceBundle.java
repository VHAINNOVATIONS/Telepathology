/**
 * 
 */
package gov.va.med;

import java.text.MessageFormat;
import java.util.ListResourceBundle;

/**
 * @author VHAISWBECKEC
 *
 */
public class MessagesResourceBundle 
extends ListResourceBundle 
{
	private final String[][] messages;
	
	public MessagesResourceBundle(final String[][] messages)
	{
		this.messages = messages;
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public String formatMessage(String key, String... values)
	{
		String pattern = this.getString(key);
		return MessageFormat.format(pattern, (Object[])values);
	}
	
	/**
	 * @see java.util.ListResourceBundle#getContents()
	 */
	@Override
	protected Object[][] getContents() 
	{
		return this.messages;
	}

}
