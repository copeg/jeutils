package com.algosome.eutils.net;
/**
URLConnect.java

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
import java.net.*;

import org.apache.log4j.Logger;

import com.algosome.eutils.io.*;
import com.algosome.eutils.util.OutputListener;

/**
*URLConnect can connect to and read a website. This class provides functionality to perform GET and PUT methods
*for dynamic websites, the parameters of which are stored within a hashmap keyed with the parameters. Parameters are formatted into a query string using the getQueryString function, the default
*implementation uses UTF-8 encoding. </p><p><b>Note</b>: WWW consortium stipulates UTF-8 encoding should always be used: UTF-8 is the default encoding of this object. </p>
*<p>To utilize this object properly, users must <ul><li>Create a class that implements the InputStreamParser interface.</li><li>Call setURL with a properly formatted URL</li>
*<li>Define any parameters necessary for a query string</li><li>Specify if GET or PUT will be used (default: GET)</li><li>Call doQuery - specifying the parser from step 1 as a parameter -
*to submit the URL and parse the output.</li></ul>
*
*
*@author Greg Cope
*@version 1.1
*/

public class URLConnect{
	
	/* Logging */
	private Logger logger = Logger.getLogger(URLConnect.class);
	
	/*
	 * Timeout in seconds
	 */
	protected int timeOutInSeconds = 30;

	/**
	*HashMap defining parameters and values for a GET or PUT query string.
	*/
	protected Map<String, String> parameters = null; 
	
	/**
	*Debugging Flag. Set to true to output debugging lines.
	*/
	protected boolean DEBUG = false;
	
	/** 
	*Specifies a URL to look up. Default implementation is null. If this is NOT set via setURL, an IOException will be thrown.
	*/
	protected String url = null;
			 
	/**
	*Indicates whether GET or PUT will be used to contact a server. Set to true if you wish to use GET protocol, or false if you wish to use PUT protocol.
	*/
	protected boolean query_method = true;
	
	 /**
	 *The type of encoding to use for the URL submission. Default is UTF-8
	 */
	protected String	encoding = "UTF-8";
	
	/**
	 * An object that implements the OutputListener interface to send output to.
	 */
	protected OutputListener	output = null;

	/**A flag to indicate any connections or output parsing should continue (true) or terminate (false)
	 *Note: if this is set to true in any way, it MUST be set back to false prior to re-use of this object.
	 **/
	protected boolean stopProcess = false;
	
	/**
	*Creates an empty URLConnect object containing no parameters.  
	*/
	public URLConnect(){
		super();
		parameters = new HashMap<String,String>();
	}
	
	/**
	*Creates a URLConnect object with a given URL
	*@param url The URL to set this object to retrieve.
	*/
	public URLConnect(String url){
		this();
		this.url = url;
	}
	
	/**
	*Creates an URLConnect object from a previous URLConnect object. 
	*@param e A pre-formed URLConnect object containing a set of parameters to use.
	*/
	public URLConnect(URLConnect e){
		this(e.getURL());
		query_method = e.getQueryMethod();
		encoding = e.getEncoding();
		parameters = e.parameters;
		
	}
	
	/**
	 * 
	 * @param seconds
	 */
	public void setTimeout(int seconds){
		this.timeOutInSeconds = seconds;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTimeout(){
		return timeOutInSeconds;
	}

	/**
	*Sets the main URL to retrieve
	*@param u A String represention of the URL associated with this object.
	*/
	public void setURL(String u){
		url = u;
	}
	
	/**
	 * Sets the output listener for this object that output should be writted to. 
	 * Setting the output listener results in output being writting to the listener rather
	 * than the command line using System.out.
	 * @param output An object that implements the OutputListener interface.
	 */
	public void setOutputListener(OutputListener output){
		this.output = output;
	}
	
	/**
	*Retrieves the URL associated with this object.
	*@return A String representation of the URL associated with this object.
	*/
	public String getURL(){
		return url;
	}
	
	/**
	*Sets the HTTP method for query strings. Default behavior is GET.
	*@param b A boolean indicating the type of HTTP method to use<ul><li>true: GET</li><li>false: PUT</li></ul>
	*/
	public void setQueryMethod(boolean b){
		query_method = b;
	}
	
	/**
	*Retrieves the HTTP method for query strings. Default behavior is GET.
	*@return A boolean indicating the type of HTTP method to use<ul><li>true: GET</li><li>false: PUT</li></ul>
	*/
	public boolean getQueryMethod(){
		return query_method;
	}
	/**
	*Retrieves the encoding of this object.
	*@return String representation of the encoding type.
	*/
    public String getEncoding(){
        return encoding;
    }
	/**
	*Sets the encoding of this object.</p><p><b>Note</b>: WWW specifies URL encoding to be UTF-8. Changing this value from the default may result in unexpected behavior.
	*@param encoding String representation of the encoding type.
	*@see <a href="http://www.w3.org/TR/html4/">WWW Consortium</a> for more on URL encoding.
	*/
    public void setEncoding(String encoding){
		this.encoding = encoding;
    }
	/**
	*Sets the value to a parameter for dynamic websites. In the case of a query string, this equates to: param=value&
	*@param param The parameter key
	*@param value The value of the parameter.
	*/		
	public void setParameter(String param, String value){
		parameters.put( param, value );
	}
	
	/**
	*Retrieves the value to a parameter, or null if none has been set. In the case of a query string, this retrieves the value portion of: param=value&
	*@param param The parameter to retrieve
	*@return A String containing the value of the parameter, or null if none was found.
	*/
	public String getParameter(String param){
		return parameters.get(param);
	}
	/**
	*Retrieves the output from the given url parameter. This function is called indirectly through doQuery.  
	*Calls parseOutput of the input parser to parse the output depending upon implementation.
	*@param url The URL to read and return the contents.
	*@param parser An object that implements the InputStreamParser interface.
	*@throws IOException if a connection could not be read or established.
	*@throws UnsupportedEncodingException If encoding of a POST query could not be established.
	*@see #doQuery(InputStreamParser parser)	
	*@see com.algosome.eutils.io.InputStreamParser
	*/
	private void retrieveURLOutput(String url, InputStreamParser parser) throws IOException, UnsupportedEncodingException{

		if ( DEBUG ){
			if ( this.output == null ){
				System.out.println("Connecting...");
				System.out.println(url);
			}else{
				this.output.notice( "Connecting..." );
				this.output.message( url );
			}
		}
		if ( stopProcess ) return;
		URL	entrez = new URL(url);
		URLConnection conn = entrez.openConnection();
		conn.setConnectTimeout(timeOutInSeconds*1000);
		if ( !query_method ){
			conn.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(getQueryString());
			out.flush();
		}
		if ( stopProcess ) return;
		parser.parseInput(conn.getInputStream());
	}
		
	/**
	*Retrieves a query string based upon the current parameters. 
	*@return A String in the format of a query string. (e.g. term=x&db=protein)
	*@throws UnsupportedEncodingException in the case the appropriate encoding cannot be used.
	*/	
	protected String getQueryString() throws UnsupportedEncodingException{
	
		StringBuffer queryString = new StringBuffer();
		Set<Map.Entry<String,String>> entries = parameters.entrySet();
		
		//URLFormat query_string = new URLFormat();		
		for (Map.Entry entry : entries){
			if (entry.getValue() != null ){	
				if (queryString.length() != 0)
					queryString.append("&");
				if ( entry.getValue().toString().length() != 0){
					queryString.append(URLEncoder.encode(entry.getKey().toString().trim(), encoding)).append("=");
					queryString.append(URLEncoder.encode(entry.getValue().toString().trim(), encoding));
				}
			}
		}
		if ( queryString.charAt(queryString.length()-1) == '&' ){
			queryString.delete(queryString.length()-1, queryString.length());
		}
		return queryString.toString();
	}
	
	/**
	*Performs an HTTP request to NCBI: sets the URL based upon the current query method, then calls retrieveURLOutput. Indirectly calls parseOutput of the parser
	*via retrieveURLOutput.
	*@param parser An object that implements the InputStreamParser interface.
	*@throws IOException if the connecetion cannot be read or established.
	*@throws UnsupportedEncodingException If the submission cannot be UTF-8 Encoded.
	*@see #retrieveURLOutput(String url, InputStreamParser is)
	*@see com.algosome.eutils.io.InputStreamParser
	*/		
	public void doQuery(InputStreamParser parser)  throws IOException, UnsupportedEncodingException{
		if ( url == null ){
			throw new IOException("Invalid URL to query: there is no URL specified.");
		}	
		
		StringBuffer request = new StringBuffer();
		request.append(url);
		if ( query_method ){
			request.append("?");
			request.append(getQueryString());
		}
		if ( stopProcess ) return;
		if ( DEBUG ){
			if ( this.output != null ){
				this.output.message("Request URL: " + request.toString());
			}else{
				System.out.println("Request URL: " + request.toString());
			}
		}
		logger.info("Connecting to URL " + request.toString());
		retrieveURLOutput(request.toString(), parser);
	}	
	/**
	 * Sets the stopProcess Flag. A value of true stops this thread from running.
	 * @param b
	 */
	public void setStopProcess( boolean b ){
		stopProcess = b;
	}
}