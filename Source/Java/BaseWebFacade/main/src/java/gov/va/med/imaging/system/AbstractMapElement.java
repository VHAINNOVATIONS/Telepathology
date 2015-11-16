package gov.va.med.imaging.system;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * An abstract tag element for display Map<String, String>
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractMapElement 
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private Map<String, String> displayMap;
	private Iterator<String> keySetIterator;
	private String currentKey = null;
	private String currentValue = null;

	public AbstractMapElement()
	{
		super();
	}

	protected abstract Map<String, String> getDisplayMap();
	
	@Override
    public int doStartTag() throws JspException
    {
    	displayMap = getDisplayMap();
    	if(displayMap != null && !displayMap.isEmpty())
    	{
    		keySetIterator = displayMap.keySet().iterator();
    		return setIterator() ? BodyTag.EVAL_BODY_INCLUDE : BodyTag.SKIP_BODY;
    	}
        return BodyTag.SKIP_BODY;
    }

	@Override
    public int doAfterBody() throws JspException
    {
    	return setIterator() ? BodyTag.EVAL_BODY_AGAIN : BodyTag.SKIP_BODY;
    }

	private boolean setIterator()
    {
    	if(keySetIterator.hasNext())
    	{
    		Object key = keySetIterator.next();
    		currentKey = key.toString();
    		currentValue = displayMap.get(currentKey);
    		return true;
    	}
    	else
    	    return false;
    }

	public String getCurrentKey()
    {
    	return currentKey;
    }

	public String getCurrentValue()
    {
    	return currentValue;
    }

}