package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;

import java.awt.Color;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
@SPI(description="Defines the interface for user preferences.")
public interface UserPreferenceDataSourceSpi
extends VersionableDataSourceSpi
{
    public abstract Color getForegroundColor()
    throws ConnectionException, MethodException;
}
