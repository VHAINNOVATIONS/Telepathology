/**
 * 
 */
package gov.va.med.imaging;

/**
 * This is a bit of a kludge because checksums got thrown in late in the project.
 * What happens is that the cache calculates the checksums when the Instance is written.
 * When a caller reads something from the cache, the read may or may not succeed depending
 * on whether the Instance existed in the cache.  Even if it does exist it may not be completely
 * written so the checksum won't be available.
 * 
 * What this interface does is define a callback so that when the web application gets an image,
 * regardless of whether it is cached or not when the request is made, the notification method
 * in here will be sent the valid checksum when it is available but before the content has been
 * streamed.  This way the web application can write the headers (with the checksum) before writing
 * the image.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface InstanceChecksumNotification
{
	public void instanceChecksum(String checksumValue);
}
