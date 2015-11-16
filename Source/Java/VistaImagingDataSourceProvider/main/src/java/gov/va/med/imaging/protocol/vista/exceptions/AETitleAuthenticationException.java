//3.0;IMAGING;**66**;1-Aug-2005
// Per VHA Directive 2004-038, this routine should not be modified.
//+---------------------------------------------------------------+
//| Property of the US Government.                                |
//| No permission to copy or redistribute this software is given. |
//| Use of unreleased versions of this software requires the user |
//| to execute a written test agreement with the VistA Imaging    |
//| Development Office of the Department of Veterans Affairs,     |
//| telephone (301) 734-0100.                                     |
//|                                                               |
//| The Food and Drug Administration classifies this software as  |
//| a medical device.  As such, it may not be changed in any way. |
//| Modifications to this software may result in an adulterated   |
//| medical device under 21CFR820, the use of which is considered |
//| to be a violation of US Federal Statutes.                     |
//+---------------------------------------------------------------+

package gov.va.med.imaging.protocol.vista.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

/**
 * Implementation of an SCU for DICOM Query/Retrieve.
 * <p>The ResultSetException is the exception that may
 * be thrown by the method readStudyRoot when no valid
 * result can be obtained from the HIS/RIS.
 * @author VistA Imaging - DICOM Team
 * @version 3.0.66.1
 */

public class AETitleAuthenticationException extends MethodException
{
  public AETitleAuthenticationException(){
      //constructor
  }
  public AETitleAuthenticationException(String msg)
  {
    super(msg);
  }
  public AETitleAuthenticationException(Exception e)
  {
    super(e);
  }
}