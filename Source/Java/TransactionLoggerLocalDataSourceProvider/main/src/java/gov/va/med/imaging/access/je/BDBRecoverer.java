/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 27, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.access.je;


import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;
import java.util.Collections;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;


/**
 * 
 * If BDB Environment initialization fails, then try running the recovery routine(s) here.
 * 
 * @author vhaiswbatesl1
 *
 */
public class BDBRecoverer implements Runnable
{

   private static final String fylSep = System.getProperty ("file.separator");
	   
   private Logger logger = Logger.getLogger(this.getClass ());
   
   private File databaseDirectory = null;
   
   private Process appProc = null;
   private boolean processRunning = false;
   private boolean processFinished = true;
   private StringBuilder sbConsoleOutput = null;
   

   /**
    * Construct with the BDB Environment directory.
    * @param databaseDirectory The directory with the BDB files.
    */
   public BDBRecoverer (File databaseDirectory)
   {
   
      this.databaseDirectory = databaseDirectory;
   
   } // BDBRecoverer
   

   /**
    * 1. If there aren't any .jdb files, forget it.
    * 2. Backup .jdb files to Database Directory slash BDBRecovererBackup- today's date.
    * 3. Starting with the latest .jdb file, and working backwards to the first
    * "00000000.jdb" file, remove the file from play and run DbVerify against
    * the .jdb files still left until DbVerify reports everything is AOK.
    * @throws DatabaseException if anything goes wrong.
    */
   public void recover () throws MethodException
   {

	   File shellScript = null;
	   
	   try
	   {
          logger.info ("Attempting To Recover BDB Environment " + databaseDirectory.getCanonicalPath ());
		      
	      Vector<File> vJdbFyls = new Vector<File> ();
		  File[] fylLst = databaseDirectory.listFiles ();

	      for (File fyl : fylLst)
	      {
	    	  if (fyl.isFile () && fyl.getName ().endsWith (".jdb")) vJdbFyls.add (fyl);
	      }
	      
	      if (vJdbFyls.isEmpty ())
	      {
	    	  throw new MethodException ("BDB Environment Recovery Failed - No \".jdb\" Files Found");
	      }

	      Vector<Long> vJdbFylNums = new Vector<Long> ();
	      Map<Long, File> mapJdbFyls = new Hashtable<Long, File> ();
	      for (File fyl : vJdbFyls)
	      {
	    	  int idx1 = fyl.getName ().indexOf (".jdb");
	    	  String fileNumTxt = fyl.getName ().substring (0, idx1);
	    	  Long fileNum = new Long (Long.parseLong (fileNumTxt, 16));
	    	  vJdbFylNums.add (fileNum);
	    	  mapJdbFyls.put (fileNum, fyl);
	      }
	      Collections.sort (vJdbFylNums);
	      
	      SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
	      String dateTxt = sdf.format(new Date ());
	      File backupDir = new File (databaseDirectory.getCanonicalPath() + fylSep + "BDBRecovererBackup-" + dateTxt);
	      backupDir.mkdir ();
	      
	      logger.info ("Backing Up \".jdb\" Files To " + backupDir.getCanonicalPath ());
	      
   		  int size = 1000000;
          byte[] fileBytes = new byte[size];
	      for (File fyl : vJdbFyls)
	      {
             File bakFyl = new File (backupDir.getCanonicalPath () + fylSep + fyl.getName ());
   			 BufferedOutputStream bos = new BufferedOutputStream (new FileOutputStream (bakFyl));
   			 
             BufferedInputStream bis = new BufferedInputStream (new FileInputStream (fyl));
             int numRead = bis.read (fileBytes);
             while (numRead > 0)
             {
    			bos.write (fileBytes, 0, numRead);
                numRead = bis.read (fileBytes);
             }
             bis.close ();
             
 			 bos.flush ();
			 bos.close ();
	      }
	      
          boolean isUnix = (fylSep.equals ("/") ? true : false);
          String script = (isUnix ? "dbverify.sh" : "dbverify.bat");
          shellScript = new File (System.getProperty ("java.io.tmpdir") + fylSep + script);
          
          String javaExe = "java";
          String javaHome = System.getenv ("JAVA_HOME");
          if (javaHome != null) javaExe = "\"".concat (javaHome).concat (fylSep).concat ("bin").concat (fylSep).concat ("java\"");
          
          String catalinaHome = System.getenv("CATALINA_HOME");
          logger.info("Catalina Home: [" + catalinaHome + "]");
          logger.info("fylSep: [" + fylSep + "]");
          
          String libDir = catalinaHome + fylSep + "lib";
          logger.info("libDir: [" + libDir + "]");
          File jeJar = new File (libDir + fylSep + "je-3.2.76.jar");
          logger.info("jeJar path [" + jeJar.getAbsolutePath() + "]");
          
          if (!jeJar.exists ())
          {
        	  throw new MethodException ("BDB Environment Recovery Failed - Can't Find File " + jeJar.getCanonicalPath ());
          }

          PrintWriter pw = new PrintWriter (new FileOutputStream (shellScript));
    	  if (isUnix) pw.println ("#!/bin/sh");
   		  pw.println (javaExe + " -cp \"" + jeJar.getCanonicalPath () + "\" com.sleepycat.je.util.DbVerify -h \""
             + databaseDirectory.getCanonicalPath() + "\"");
      	  pw.flush ();
       	  pw.close ();
	      
       	  shellScript.setExecutable (true);

	      int jdbKtr = vJdbFylNums.size () - 1;
	      while (jdbKtr >= 0)
	      {
	    	  Long fileNum = (Long) vJdbFylNums.get (jdbKtr);
   	    	  File jdbFyl = mapJdbFyls.get (fileNum);

   	    	  logger.info ("Verifying " + jdbFyl.getName ());
    	    	  
    		  File bakFyl = new File (jdbFyl.getCanonicalPath () + ".bak");
    		  jdbFyl.renameTo (bakFyl);
    		  
    		  sbConsoleOutput = new StringBuilder ();

    		  appProc = Runtime.getRuntime ().exec (shellScript.getCanonicalPath ());
              processFinished = false;
              processRunning = true;
              new Thread (this).start ();
              appProc.waitFor ();
              processRunning = false;
              while (!processFinished);
              
//              logger.info("Done processing: \n" + sbConsoleOutput.toString());
              
              if (sbConsoleOutput.toString ().toLowerCase ().indexOf ("exit status = true") >= 0) break;
	              
    		  jdbKtr--;
	      }
	      
	      for (File fyl : vJdbFyls)
	      {
    		  File bakFyl = new File (fyl.getCanonicalPath () + ".bak");
	    	  if (bakFyl.exists ()) bakFyl.delete ();
	      }
	      
	      if (jdbKtr < 0)
	      {
	    	  throw new MethodException ("BDB Environment Recovery Failed - No Valid \".jdb\" Files Found");
	      }

    	  Long fileNum = (Long) vJdbFylNums.get (jdbKtr);
    	  File jdbFyl = mapJdbFyls.get (fileNum);
    	  logger.info ("Problem Found With File " + jdbFyl.getName () +
    	     ".  This And All Later Files Have Been Deleted.");
 
    	  if (shellScript != null && shellScript.exists ()) shellScript.delete ();
	   }
	   
	   catch (DatabaseException dbe)
	   {
		   throw (dbe);
	   }
	   
	   catch (Exception x)
	   {
		   throw new MethodException (x);
	   }

       finally
       {
    	   if (shellScript != null && shellScript.exists ()) shellScript.delete ();
       }
	   
   } // recover
   

   /**
    * The script and its commands are handled by a separate process.
    * Use this Thread here to monitor and capture any console output produced by
    * running the script.
    */
   public void run ()
   {
   	
      BufferedInputStream appIn = new BufferedInputStream (appProc.getInputStream ());
	  BufferedInputStream appErr = new BufferedInputStream (appProc.getErrorStream ());
	  PrintStream appOut = new PrintStream (appProc.getOutputStream (), true);
	  
      while (true)
      {
          try
          {
    	      int numIn = 0;
    	      int numErr = 0;

              while ((numIn = appIn.available ()) > 0)
              {
                  byte[] bfr = new byte[numIn];
                  appIn.read (bfr, 0, numIn);
//                System.out.print (new String (bfr));
	              sbConsoleOutput.append (new String (bfr));
	          }

              while ((numErr = appErr.available ()) > 0)
              {
                  byte[] bfr = new byte[numErr];
                  appErr.read (bfr, 0, numErr);
//                System.out.print (new String (bfr));
	              sbConsoleOutput.append (new String (bfr));
	          }

	          if (!processRunning || processFinished) break;
	      }

	      catch (Exception x)
	      {
//            System.out.println (x.getClass ().getName () + ": " + x.getMessage ());
	       	  logger.error (x.getClass ().getName () + ": " + x.getMessage ());
	       	  sbConsoleOutput.setLength (0);
	          break;
          }
      }

      try
      {
          if (appOut != null) appOut.close ();
          if (appErr != null) appErr.close ();
          if (appIn != null) appIn.close ();
      }

      catch (Exception ignore)
      {
      }
          
      appOut = null;
      appErr = null;
      appIn = null;
          
      processFinished = true;

   } // run
   
} // class BDBRecoverer 
