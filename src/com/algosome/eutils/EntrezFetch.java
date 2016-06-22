package com.algosome.eutils;
/**
eFetch.java

Created by Greg Cope.
Copyright © 2009 Algosome. All rights reserved.

JeUtils Project
Initial Version by:
Greg Cope
website: www.algosome.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
or visit
<http://www.gnu.org/licenses/>.
**/

import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.StringBuffer;

import com.algosome.eutils.io.*;
import com.algosome.eutils.net.*;

/**
*eFetch  entrez eUtils class to query Entrez NCBI via string based HTTP request. 
*This class is capable of performing an eFetch request to NCBI based upon:</p>
*<ul><li>A WebEnv and QueryKey parameter set in a previous eSearch or</li>
*<li>A list of Genbank Ids</li></ul>
*
*<p>
*@see <a href="http://www.ncbi.nlm.nih.gov/corehtml/query/static/esearch_help.html">NCBI</a> for further information
*@author Greg Cope
*@version 1.1
*@see http://eutils.ncbi.nlm.nih.gov/About/disclaimer.html
*/
public class EntrezFetch extends Entrez implements InputStreamParser{
	
	
	/**The entire output from an eFetch */
	protected String entrez_output = null;
	
	
	
	/**
	*Private empty Constructor. Because eFetch is dependent upon a previous eSearch, an empty eFetch should never be constructed.
	*/
	public EntrezFetch(){
		super();
		setTool("J-eUtils");
		setEmail("gregcope@algosome.com");
		setURL( "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi" );
	}
	
	/**
	*Creates a new eFetch based upon a previous eSearch. It is expected that the eSearch contains valid QueryKey and WebEnv parameters or a list of valid Genbank Ids. 
	*If this is not the case, an EntrezException will be thrown.
	*@param search A preformed eSearch object that contains a valid QueryKey and WebEnv or Genbank Id's to search.
	*@throws IllegalArgumentException if search does not contain a valid QueryKey and WebEnv or there are not ID's to search Genbank.
	*/
	public EntrezFetch(EntrezSearch search) throws IllegalArgumentException{
		super(search);
		setURL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
		if ( search.getUseHistory().trim().equals("y") ){
			if ( search.getWebEnv() == null || 
					search.getQueryKey() == null && getIds() == null ){
					throw new IllegalArgumentException("Invalid QueryKey and/or WebEnv for an eFetch Object.");
			}
		}else{
			if ( search.getIds() == null ){
				throw new IllegalArgumentException("No Genbank Ids to perform an eFetch.");
			}
		}

	}
	/**
	*Processes an eFetch Entrez Output based upon user specified parameters. Default is to print the output to the command line, howevers users can 
	*turn that feature off and/or write the output to a file.  This function should not be called directly, but rather through doQuery. 
	*@param is An InputStream to read from.
	*@throws IOException of the Input Stream could not be read, or the output was emtpy.
	*@see com.algosome.eutils.io.InputStreamParser
	*@see com.algosome.eutils.JeUtils for directions to set output flags.
	*/
	public void parseInput(InputStream is) throws IOException{
		
		
		if ( DEBUG ){
			logger.debug("Processing eFetch...");
		}
		if ( stopProcess ) return;
		StringBuffer output = new StringBuffer();
		String output_file = getOutputFile();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ( ( line = rd.readLine()) != null ){
			if ( stopProcess ) return;
			if ( getPrintEFetchOutput() != null ){
				if ( getPrintEFetchOutput().equals("y")){
					if ( output == null ){
						System.out.println(line);
					}else{
						this.output.data(line + "\n" );
					}
				}
			}
			if ( output_file != null ){
				output.append(line).append("\n");
			}
			output.append(line).append("\n");
		}
		if ( stopProcess ) return;
		rd.close();		
		entrez_output = output.toString();
		if ( entrez_output.length() < 1 ){
			throw new IOException("Empty output returned from NCBI.");
		}
		if ( output_file != null ){
			BufferedWriter writer = new BufferedWriter(new FileWriter(output_file));
			writer.write(entrez_output);
			writer.close();
		}
	}
	/**
	*Empty implementation of the InputStreamParser
	*@param start The location to start from.
	*/
	public void parseFrom(int start){}
	/**
	*Empty implementation of the InputStreamParser.
	*@param end The location to stop at.
	*/
	public void parseTo(int end){}
	/**
	*Formats a query string to submit to NCBI based upon the current parameters. This function filters certain program specific parameters so all parameters 
	*are not sent in a query string to NCBI.
	*@return a String in the format of a query string. (e.g. term=x&db=protein)
	*/	
	@Override public String getQueryString() throws UnsupportedEncodingException{
		String query = super.getQueryString();
		if ( DEBUG ){
			if ( output == null ){
				System.out.println("eFetch: " +query);
			}else{
				output.message( "eFetch: " +query );
			}
		}
		StringBuffer appender = new StringBuffer();
		String[] filter = query.split("&");
		if ( getUseHistory().equals("y") ){
			if ( getQueryKey() != null && getWebEnv() != null ){
				for ( String st : filter ){
					String s = st.replaceAll("-", "");
					if ( isESearchSpecificParam(s.substring(0, s.indexOf("="))) )continue;
					if ( s.indexOf("id=") == 0 || s.indexOf("usehistory=") == 0 || s.indexOf("term=") == 0 ) continue;//don't do ID's when we use history.
					appender.append(s);
					appender.append("&");
				}
			}
		}else{
			for ( String st : filter ){
				String s = st.replaceAll("-", "");
				if ( isESearchSpecificParam(s.substring(0, s.indexOf("="))) )continue;
				if ( s.indexOf("WebEnv=") == 0 || s.indexOf("query_key=") == 0 || s.indexOf("term=") == 0 || s.indexOf("usehistory=") == 0) continue;//don't do ID's when we use history.
				appender.append(s);
				appender.append("&");
			}
		}		
		if ( DEBUG ){
			if ( output == null ){
				System.out.println(appender.toString());
			}else{
				output.message( appender.toString() );
			}
		}
		return appender.toString();
		
	}
	/**
	*Performs the actual eFetch HTTP request to NCBI. 
	*@param is An InputStreamParser specifying what to do with the retrieved results.
	*@throws IOException if the connecetion cannot be read or established.
	*@throws UnsupportedEncodingException If the submission cannot be UTF-8 Encoded.
	*/		
	@Override public void doQuery(InputStreamParser is) throws IOException, UnsupportedEncodingException{
		if ( getIds() == null ){
			throw new IOException("No ID's to perform an eFetch.");
		}
		if ( getUseHistory().equals("y") ){
			if ( getQueryKey() == null || getWebEnv() == null ){
				throw new IOException("Cannot to perform eFetch: no WebEnv or QueryKey provided, and no IDs to Search. A previous eSearch may have returned an empty set.");
			}
		}
		super.doQuery(is);
	}

	
}