/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.tomcat.vistarealm;

public interface IConnection 
{
    public static final String NOT_CONNECTED = "Not connected";

	//public void setPid(String pid);
	//public void setPid(Patient patient) throws DaoException;
	//public String getPid();
	//public String getPid(String patientID) throws DaoException;
	//public void setLocalPID(String mpiPID) throws DaoException;

	//public void setPatient(Patient patient);
	//public Patient getPatient();

	//public void setUid(String uid);
	//public String getUid();

    public DataSource getDataSource();

	//public String getSiteID();

	//public void setRemoteFlag(boolean remoteFlag);
	//public boolean isRemote();

    public void connect() throws Exception;
    public String call(String query) throws Exception;
    public void disconnect() throws Exception;
	//public void getWelcome(Dto dto) throws DaoException;
	//public String getWelcome() throws DaoException;
	//public Site[] getRemoteSites(SiteTable allSites) throws DaoException;
	//public Object openConnection(User user, String patientID) throws DaoException;
	//public Object closeConnection() throws DaoException;
	//public String getTimestamp() throws DaoException;
}
