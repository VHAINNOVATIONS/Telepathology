/*
 * Created on Dec 2, 2006
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
 *
 */
package gov.va.med.imaging;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class SimpleStopWatch {

    private long startTime = 0;
    private long intervalTime = 0;
    /**
     * Constructor
     *
     * 
     */
    public SimpleStopWatch() {
        super();
    }
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
    
    public long getElapsedTime() {
        this.intervalTime = System.currentTimeMillis();
        return(intervalTime - startTime);
    }

    public long getElapsedTimeInSecs() {
        this.intervalTime = System.currentTimeMillis();
        return((intervalTime - startTime) / 1000);
    }
}
