package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.protocol.vista.AbstractVistaConnectionTest;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class VistaVersionCheckTest 
extends AbstractVistaConnectionTest 
{
	
	private final static String [] versionFiles = 
		new String[] {"vistaVersions1.version", "vistaVersions2.version"};	
	
	public void testVistaVersionCheckTranslation()
	{
		try
		{
			String searchVersion = "3.0P46";
			
			for(String filename : versionFiles)
			{
			
				StringBuilder sb = new StringBuilder();
				InputStream inStream = getClass().getClassLoader().getResourceAsStream(filename);
				LineNumberReader reader = new LineNumberReader( new InputStreamReader(inStream) );
				String prefix = "";
				for( String line = reader.readLine(); line != null; line = reader.readLine() )
				{
					sb.append(prefix);
					sb.append(line);
					prefix = "\n";
				}
				
				//System.out.println("SB= [" + sb.toString() + "]");
				
	            reader.close();
				List<String> versions = VistaImagingTranslator.convertVistaVersionsToVersionNumbers(sb.toString());
				boolean found = false;
				for(String version : versions)
				{
					if(version.equalsIgnoreCase(searchVersion))
					{
						found = true;
						break;
					}
				}
				if(!found)
					fail("Did not find version '" + searchVersion + "' in file '" + filename + "'");
				System.out.println("Found version '" + searchVersion + "'");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
	}

}
