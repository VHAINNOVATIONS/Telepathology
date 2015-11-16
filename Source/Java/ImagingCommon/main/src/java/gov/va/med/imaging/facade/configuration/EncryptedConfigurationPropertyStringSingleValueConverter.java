/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 25, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.facade.configuration;

import org.apache.log4j.Logger;

import gov.va.med.imaging.encryption.AesEncryption;
import gov.va.med.imaging.encryption.exceptions.AesEncryptionException;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * @author VHAISWWERFEJ
 *
 */
public class EncryptedConfigurationPropertyStringSingleValueConverter
implements SingleValueConverter
{
	private final static Logger logger = Logger.getLogger(EncryptedConfigurationPropertyStringSingleValueConverter.class);

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type)
	{
		return type.equals(EncryptedConfigurationPropertyString.class);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
	 */
	@Override
	public Object fromString(String arg0)
	{
		String decryptedString = null;
		if(arg0 == null)
			return null;
		try
		{
			decryptedString = AesEncryption.decrypt(arg0);
		}
		catch(AesEncryptionException aesX)
		{
			logger.error("Error decrytping string, " + aesX.getMessage());
		}
		return new EncryptedConfigurationPropertyString(decryptedString);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Object arg0)
	{
		EncryptedConfigurationPropertyString encryptedProperty = (EncryptedConfigurationPropertyString)arg0;
		try
		{
			return AesEncryption.encrypt(encryptedProperty.getValue());
		}
		catch(AesEncryptionException aesX)
		{
			logger.error("Error encrypting string [" + encryptedProperty.getValue() + "], " + aesX.getMessage());
		}
		return null;
	}

}
