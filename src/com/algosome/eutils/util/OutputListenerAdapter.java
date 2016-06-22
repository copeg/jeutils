package com.algosome.eutils.util;

/**
 * An empty implementation of the OutputListener interface. This class allows objects
 * to be created and override a select few functions rather than the entire OutputListener 
 * interface.
 * @author Greg Cope
 * @implements OutputListener
 * 
 *
 */
public abstract class OutputListenerAdapter implements OutputListener{

	/**
	 * Receives a message from the caller. 
	 * @param s The message received.
	 */
	public void message(String s){}
	/**
	 * Sends a notice to this listener. Notices involve programatic state changes, such
	 * as "Connecting" or "Receiving Results"
	 * @param s The notice received.
	 */
	public void notice(String s){}	
	/**
	 * Receives an error from the caller. 
	 * @param s Information regarding the error that occurred.
	 */
	public void error( String s ){}
	
	/**
	 * Receives data from the caller. 
	 * @param s The data received. 
	 */
	public void data( String s ){}
	
}
