/**
 * This package contains the definitions of each of the data source service provider interfaces (SPI). 
 * Each SPI defines the methods for operations on a subset of the business objects.
 * <p>SPIs are classified as either "local" or "versionable".  A local SPI usually has one instance running
 * in a VISA application and references information accessible through file operations.  A versionable
 * SPI does some network operation to access its data and supports versioning of the protocol used to
 * access the data.
 *
 * @since 1.0
 */
package gov.va.med.imaging.datasource;
