package com.algosome.eutils.util;

/**
 * An interface to be used to send information to a client. This interface can be implemented
 * using command line or GUI implementations, retrieving user selected values. Only a single object
 * should implement this interface, otherwise there may be input conflicts resulting
 * in unexpected behavior.
 * @see OutputListener
 * @author gregcope
 *
 */
public interface InputListener {

	/**
	 * Retrieves a full file path from the user in an implementation depdendent fashion.
	 * @return A string containing the file, or an empty string if none was selected.
	 */
	public String getFile();
	
	/**
	 * Retrieves a string value from the user based upon a previous message or question. 
	 * Callers of this method should first call message()
	 * to one or more OutputListeners so users know the program is waiting for input.
	 * @return
	 */
	public String getString();
	
}
