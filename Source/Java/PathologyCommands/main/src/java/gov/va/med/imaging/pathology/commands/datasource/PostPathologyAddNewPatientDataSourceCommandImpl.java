package gov.va.med.imaging.pathology.commands.datasource;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyPatientInfoItem;
import gov.va.med.imaging.pathology.commands.AbstractPathologyDataSourceCommandImpl;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;

public class PostPathologyAddNewPatientDataSourceCommandImpl 
extends AbstractPathologyDataSourceCommandImpl<String>
{
	private static final long serialVersionUID = 627085897419896305L;
	
	private final RoutingToken globalRoutingToken;
	private final List<PathologyPatientInfoItem> patientDataList;
	private final String originatingSiteId;
	
	public PostPathologyAddNewPatientDataSourceCommandImpl(RoutingToken globalRoutingToken, 
			List<PathologyPatientInfoItem> patientDataList, String originatingSiteId)
	{
		this.globalRoutingToken = globalRoutingToken;
		this.patientDataList = patientDataList;
		this.originatingSiteId = originatingSiteId;
	}

	public List<PathologyPatientInfoItem> getPatientDataList() {
		return patientDataList;
	}

	public String getOriginatingSiteId() {
		return originatingSiteId;
	}

	@Override
	public RoutingToken getRoutingToken() {
		return globalRoutingToken;
	}

	@Override
	protected String getSpiMethodName() {
		return "addNewPatient";
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] {RoutingToken.class, List.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] {getRoutingToken(), getPatientDataList(), getOriginatingSiteId()};
	}

	@Override
	protected String getCommandResult(PathologyDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.addNewPatient(getRoutingToken(), getPatientDataList(), getOriginatingSiteId());
	}

}
