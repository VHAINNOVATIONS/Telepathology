package gov.va.med.imaging.proxy;

/**
 * The notification event of a change to a ConnectionParameters
 * instance.  The changed field is available as an Enum in the field
 * property.  A null value in the field property indicates that 
 * the field is unknown or that multiple fields were changed.
 * There is no indication if the field change was an insert,
 * update or removal.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ConnectionParameterChangeEvent
{
	private ConnectionParameters source = null;
	private Enum field;

	public ConnectionParameterChangeEvent(ConnectionParameters source, Enum field)
	{
		super();
		this.source = source;
		this.field = field;
	}

	public ConnectionParameters getSource()
	{
		return this.source;
	}

	public Enum getField()
	{
		return this.field;
	}
}
