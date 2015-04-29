/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm.config;

/**
 * This interface describes the properties that a command line option must make available
 * to the CommandLineParser.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface CommandLineOption
{
	public String getName();
	public Class getType();
	public boolean isRequired();
}