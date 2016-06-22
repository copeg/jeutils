package com.algosome.eutils.util;

/**
 * This interface allows clients to listen for output from a caller. Most noteably this interface allows
 * seperation of GUI components and computational components, allowing each to be pluggable based
 * upon implementation. Implementations should deal with 
 * messages, errors, and data transmitions accordingly.
 * @author Greg Cope
 *
 */
public interface OutputListener {

	/**
	 * Receives a message from the caller. 
	 * @param s The message received.
	 */
	public void message(String s);
	
	/**
	 * Sends a notice to this listener. Notices involve programatic state changes, such
	 * as "Connecting" or "Receiving Results"
	 * @param s The notice received.
	 */
	public void notice(String s);
	
	
	/**
	 * Receives an error from the caller. 
	 * @param s Information regarding the error that occurred.
	 */
	public void error( String s );
	
	/**
	 * Receives data from the caller. 
	 * @param s The data received. 
	 */
	public void data( String s );
	
}
