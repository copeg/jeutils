package com.algosome.eutils;
/**
eSearch.java

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
import java.lang.*;
import java.io.*;
import java.util.regex.*;

import com.algosome.eutils.io.*;
import com.algosome.eutils.net.*;

/**
*A class to access entrez via a query string based HTTP request. This class is capable of performing an eSearch and retrieving:
*<ul><li>WebEnv and QueryKey parameters</li><li>A list of Genbank Ids</li></ul>
*both of which can be retrieved in a later eFetch request.</p>
*<p>The default parameters upon construction are:
*<ul><li><b>Database</b>: Pubmed</li>
*<li><b>UseHistory:</b> n</li>
*<li><b>Retrieval Mode:</b> xml</li>
*<li><b>printesearch</b>: n Does NOT print eSearch output to the terminal.</li>
*<li><b>printefetch</b>: y Prints eFetch output to the terminal.</li></ul>
*@see <a href="http://www.ncbi.nlm.nih.gov/corehtml/query/static/esearch_help.html">NCBI</a> for further information
*@author Greg Cope
*@version 1.1
*@see http://eutils.ncbi.nlm.nih.gov/About/disclaimer.html
*/
public class EntrezSearch extends Entrez implements InputStreamParser{

	
	/**
	*Creates a new eSearch Object, and sets the default parameters. The created eSearch object is deficient in search terms, which must be set prior 
	*to running the query.
	*/
	public EntrezSearch(){
		super();
		url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";
		setDefaultParameters();
	}
	/**
	*Creates a new eSearch based upon a previously filled hashmap containing valid Entrez parameters.
	*@param args A Hashmap object containing previously set Entrez parameters.
	*@see Entrez for parameters.
	*/
	public EntrezSearch(Map<String, String> args){
		this();
		Set<Map.Entry<String,String>> entries = args.entrySet();
		if ( DEBUG ){
			if ( output == null ){
				System.out.print( "Constructing eSearch for terms..." );
			}else{
				output.notice("Constructing eSearch for terms...");
			}
		}
		for (Map.Entry entry : entries){
			parameters.put( (String)entry.getKey(),(String) entry.getValue() );
			if ( DEBUG ){
				if ( output == null ){
					System.out.print( entry.getValue() + " ");
				}else{
					output.message(entry.getValue() + " ");
				}
			}
		}			
	}

	/**
	*Sets any default parameters. This is called from the main constructor, or any object that wishes
	*to re-use this object (in which case reset() should be called prior to this funcion).
	*Database: Pubmed
	*UseHistory: y
	*Retrieval Mode: XML
	*Does not print eSearch output to the terminal.
	*Prints eFetch output to the terminal.
	*/
	public void setDefaultParameters(){
		setDatabase(DB_PUBMED);
		setUseHistory("n");
		setRetType("xml");
		setPrintESearchOutput("n");
		setPrintEFetchOutput("n");
		setTool("J-eUtils");
		setEmail("gregcope@algosome.com");
	}

	/**
	*Processes an eSearch Entrez Output. This parses the XML output and retrieves all Entrez Id's and WebEnv/QueryKey if provided.
	*This function should not be called directly, but rather through doQuery. 
	*@param is An InputStream to read from.
	*@throws IOException If an error occurred while reading the input stream
	*/
	public void processResult(InputStream is) throws IOException{
		if ( DEBUG ){
			if ( output == null ){
				System.out.println("Processing eSearch...");
			}else{
				output.notice("Processing eSearch...");
			}
		}
		String output_file =  getESearchOutput();
		ArrayList<String> entrez_ids = new ArrayList<String>();
		BufferedReader line_reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer	output = new StringBuffer();
		String line = null;
		while ( (line = line_reader.readLine() ) != null ){
			if ( getPrintESearchOutput() != null ){
				if ( getPrintESearchOutput().equals("y") ){
					if ( this.output == null ){
						System.out.println(line);
					}else{
						this.output.data(line + "\n" );
					}
				}
			}
			if ( output_file != null ){
				output.append(line);
				output.append("\n");
			}
			if ( line.indexOf("<Id>") != -1 ){
				String id = parseID(line);
				if ( id != null ){
					entrez_ids.add(id);
				}
				if ( DEBUG ){
					if ( this.output == null ){
						System.out.println("Id: " + id);
					}else{
						this.output.message("Id: " + id);
					}
				}
			}
			if ( line.indexOf("<QueryKey>") != -1 ){
				String id = parseQueryKey(line);
				if ( id != null ){
					setQueryKey(id);
					if ( DEBUG ){
						if ( this.output == null ){
							System.out.println("QueryKey: " + id);
						}else{
							this.output.message("QueryKey: " + id);
						}
					}
				}
				
			}
			if ( line.indexOf("<WebEnv>") != -1 ){
				String id = parseWebEnv(line);
				if ( this.output == null ){
					System.out.println(id);
				}else{
					this.output.message(id);
				}
				if ( id != null ){
					setWebEnv(id);
					if ( DEBUG ){
						if ( this.output == null ){
							System.out.println("WebEnv: " + id);
						}else{
							this.output.message("WebEnv: " + id);
						}
					}
				}
				
			}
		}
		line_reader.close();
		if (entrez_ids.size() == 0 ){
			throw new IOException("No search ID's were found.");
		}
		if ( output_file != null ){
			BufferedWriter bw = new BufferedWriter(new FileWriter(output_file));
			bw.write(output.toString());
			bw.close();
		}
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < entrez_ids.size(); i++ ){
			sb.append(entrez_ids.get(i));
			if ( i < entrez_ids.size()-1){
				sb.append(",");
			}
		}
		setIds(sb.toString());
	}
	/**
	*Processes an eSearch Entrez Output. This parses the XML output and retrieves all Entrez Id's and WebEnv/QueryKey if provided.
	*This function should not be called directly, but rather through doQuery. 
	*@param is An InputStream to read from.
	*@throws IOException If an error occurred while reading the input stream
	*/
	public void parseInput(InputStream is) throws IOException{
		if ( DEBUG ){
			if ( output == null ){
				System.out.println("Processing eSearch...");
			}else{
				output.notice("Processing eSearch...");
			}
		}
		if ( stopProcess ) return;
		String output_file =  getESearchOutput();
		ArrayList<String> entrez_ids = new ArrayList<String>();
		BufferedReader line_reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer	output = new StringBuffer();
		String line = null;
		while ( (line = line_reader.readLine() ) != null ){
			if ( stopProcess ) return;
			if ( getPrintESearchOutput() != null ){
				if ( getPrintESearchOutput().equals("y") ){
					if ( output == null ){
						System.out.println(line);
					}else{
						this.output.data(line);
					}
				}
			}
			if ( output_file != null ){
				output.append(line);
				output.append("\n");
			}
			if ( line.indexOf("<Id>") != -1 ){
				String id = parseID(line);
				if ( id != null ){
					entrez_ids.add(id);
				}
				if ( DEBUG ){
					if ( this.output == null ){
						System.out.println("Id: " + id);
					}else{
						this.output.message("Id: " + id);
					}
				}
			}
			if ( line.indexOf("<QueryKey>") != -1 ){
				String id = parseQueryKey(line);
				if ( id != null ){
					setQueryKey(id);
					if ( DEBUG ){
						if ( this.output == null ){
							System.out.println("QueryKey: " + id);
						}else{
							this.output.message("QueryKey: " + id);
						}
					}
				}
				
			}
			if ( line.indexOf("<WebEnv>") != -1 ){
				String id = parseWebEnv(line);
				if ( this.output == null ){
					System.out.println(id);
				}else{
					this.output.message(id);
				}
				if ( id != null ){
					setWebEnv(id);
					if ( DEBUG ){
						if ( this.output == null ){
							System.out.println("WebEnv: " + id);
						}else{
							this.output.message("WebEnv: " + id);
						}
					}
				}
				
			}
		}
		line_reader.close();
		if (entrez_ids.size() == 0 ){
			throw new IOException("No search ID's were found.");
		}
		if ( output_file != null ){
			BufferedWriter bw = new BufferedWriter(new FileWriter(output_file));
			bw.write(output.toString());
			bw.close();
		}
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < entrez_ids.size(); i++ ){
			sb.append(entrez_ids.get(i));
			if ( i < entrez_ids.size()-1){
				sb.append(",");
			}
		}
		if ( stopProcess ) return;
		setIds(sb.toString());
	}
	/**
	*Empty implementation of the InputStreamParser
	*@param start The location to start from.
	*@see com.algosome.eutils.io.InputStreamParser
	*/
	public void parseFrom(int start){}
	/**
	*Empty implementation of the InputStreamParser
	*@param end The location to stop at.
	*/
	public void parseTo(int end){}
	
	/**
	*Parses an ID identifier out of the give line. 
	*@param line A line of text to parse.
	*@return A string representation of the ID. This parses any text between <Id> and </Id>
	*@see com.algosome.eutils.io.InputStreamParser
	*/
	protected String parseID(String line){
		return parseTextFromLine(line, "Id");
	}
	/**
	*Parses a WebEnv identifier out of the give line. 
	*@param line A line of text to parse.
	*@return A string representation of the WenEnv. This parses any text between <WenEnv> and </WenEnv>
	*/
	protected String parseWebEnv( String line ){
		if ( output == null ){
			System.out.println(line);
		}else{
			output.message( line );
		}
		return parseTextFromLine(line, "WebEnv");
	}
	/**
	*Parses a QueryKey identifier out of the give line. 
	*@param line A line of text to parse.
	*@return A string representation of the QueryKey. This parses any text between <QueryKey> and </QueryKey>
	*/
	protected String parseQueryKey(String line){
		return parseTextFromLine(line, "QueryKey");
	}
	/**
	*Parses out an XML value tag out of line using a regular expression. 
	*@param line A line of text to parse.
	*@param tag The XML tag identifier.
	*@return A string representation of the text between the given tags, or an empty string if nothing was found.
	*/
	protected String parseTextFromLine(String line, String tag){
		Pattern pattern = Pattern.compile( tag+">(.+)</"+tag );
		Matcher matcher = pattern.matcher( line );
		matcher.find();
		try{
		return matcher.group(1);
		}catch( IllegalStateException ise ){
			return "";
		}
	}
	/**
	*Formats a query string to submit to NCBI based upon the current parameters. This function 
	*filters certain program specific parameters for the query string.
	*@return a String in the format of a query string. (e.g. term=x&db=protein)
	*@throws UnsupportedEncodingException if UTF-8 Encoding is not available.
	*/	
	@Override public String getQueryString() throws UnsupportedEncodingException{
		String query = super.getQueryString();
		StringBuffer appender = new StringBuffer();
		String[] filter = query.split("&");
		for ( String st : filter ){
			String s = st.replaceAll("-", "");
			if ( isESearchSpecificParam( s.substring(0, s.indexOf("=") ).trim() ) )continue;
			if ( s.indexOf("query_key=") == 0 ) continue;
			appender.append(s);
			appender.append("&");
		}
		if ( DEBUG ){
			if ( output == null ){
				System.out.println(appender.toString());
			}else{
				output.message(appender.toString());
			}
		}
		return appender.toString();
	}	

}