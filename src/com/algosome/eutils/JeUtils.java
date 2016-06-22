package com.algosome.eutils;
/**
JeUtils.java

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

import com.algosome.eutils.*;
import com.algosome.eutils.net.*;
import com.algosome.eutils.util.*;

import java.lang.*;
import java.util.HashMap;
import java.io.*;

import java.util.*;



/**
*JeUtils acts as a basic mediator for the Eutils package. To run an eUtils command, you can 
*set the terms, database, etc.. of the instance variable esearch (using getESearch).</p>
*<p>For example
*<ul>
*<li>getESearch().setTerm("centrin");</li>
*<li>getESearch().setDatabase("protein");<li></p>
*<p>To interact with this object, users can add listeners of Output (OutputListener) and/or Thread begin/end (ThreadListener).
*These will allow one to deal with output via file IO or GUI IO, and anything necessary to do at thread start and end.
*Note: adding an OutputListener results in all output being sent to the listener(s) rather than the default (command line).
*Note: adding ThreadListeners is completely optional, but can allow for necessary clean-up or GUI updates.
*/
public final class JeUtils extends NotificationThread implements OutputListener{
	
	/**
	 * The eSearch Object for performing eSearch to NCBI. This object is initially 
	 * set to the default NCBI parameters 
	 * @see EntrezSearch#setDefaultParameters() 
	 */
	private EntrezSearch esearch = new EntrezSearch();	
	
	/**
	 * The eFetch Object for performing eFetch to NCBI. This object is instantiated
	 * int the doRun method of this object.
	 */	
	private EntrezFetch efetch = null;
	
	/**A list of listeners to send output to.*/
	private final List<OutputListener> listeners = Collections.synchronizedList(new ArrayList<OutputListener>());
	
	/**Sets whether we wish to perform eSearches only. */
	private boolean eSearchOnly = false;
	
	/**A flag to indicate this process should terminate */
	private boolean stopProcess = false;
		
	public JeUtils(){
		esearch.setOutputListener(this);
	}
	
	/**
	 * Retrieves the eSearch associated with this object.
	 * @return The eSearch object associated with this object.
	 */
	public EntrezSearch getESearch(){
		return esearch;
	}
	
	/**
	 * Allows users to set a preformatted eSearch for this object. 
	 * @param es The new eSearch object to set.
	 */
	public void setESearch(EntrezSearch es){
		esearch = es;
	}
	
	/**
	 * Adds an output listener to this object.
	 * @param l
	 */
	public void addOutputListener( OutputListener l ){
		listeners.add( l );
	}
	/**
	 * Sets the eSearchOnly flag. 
	 * @param b
	 */
	public void setESearchOnly( boolean b ){
		eSearchOnly = b;
	}
	/**
	 * Implementation of the doRun method from NotificationThread superclass.
	 */
	public void doRun(){
		try{
			esearch.setStopProcess(false);//reset the stop flag
			esearch.doQuery();
			if ( eSearchOnly ){
				return;
			}	
			if ( stopProcess )return;
			efetch = new EntrezFetch(esearch);			
			efetch.setOutputListener(this);
			efetch.doQuery();
			if ( stopProcess )return;
		}catch( UnsupportedEncodingException uee){
			error("UnsupportedEncodingException: " + uee.getMessage());
			return;
		}catch (IOException io){
			error("Input/Output Exception." + io.getMessage());
			return;
		}catch ( IllegalArgumentException ee ){
			error("IllegalArgumentException: " + ee.getMessage());
			return;
		}		
	}
	/**
	 * Receives a message from the caller. 
	 * @param s The message received.
	 */
	public void message(String s){
		for ( int i = 0; i < listeners.size(); i++ ){
			listeners.get(i).message(s);
		}		
	}
	
	/**
	 * Sends a notice to this listener. Notices involve programatic state changes, such
	 * as "Connecting" or "Receiving Results"
	 * @param s The notice received.
	 */
	public void notice(String s){
		for ( int i = 0; i < listeners.size(); i++ ){
			listeners.get(i).notice(s);
		}		
	}
	
	
	/**
	 * Receives an error from the caller. 
	 * @param s Information regarding the error that occurred.
	 */
	public void error( String s ){
		for ( int i = 0; i < listeners.size(); i++ ){
			listeners.get(i).error(s);
		}		
	}
	
	/**
	 * Receives data from the caller. 
	 * @param s The data received. 
	 */
	public void data( String s ){
		for ( int i = 0; i < listeners.size(); i++ ){
			listeners.get(i).data(s);
		}
	}
	
	/**
	 * Sets the stopProcess Flag. A value of true stops this thread from running.
	 * @param b
	 */
	public void setStopProcess( boolean b ){
		stopProcess = b;
		if ( stopProcess ){
			if ( esearch != null ){
				esearch.setStopProcess(b);
			}
			if ( efetch != null ){
				efetch.setStopProcess(b);
			}
		}
		
	}
	
}
