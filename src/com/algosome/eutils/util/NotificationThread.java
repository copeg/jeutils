package com.algosome.eutils.util;

import java.util.*;

/**
 * An abstract class used as a Runnable interface to perform lengthy tasks and notify listeners of
 * the start and end of those tasks. This class implements the Runnable interface during which it:</p>
 * <ol>
 * <li>Notifies listeners of the start of the thread.</li>
 * <li>Calls the doRun method - all lengthy tasks should be placed in this method.
 * <li>Notifies listeners of the end of the thread.</li>
 * </ol>
 * <p>Users should extend this class, implement the doRun method, and add any listeners
 * necessary to be notified of the thread endpoints.
 * 
 * @author Greg Cope
 * @see ThreadListener
 * @see ThreadListenerAdapter
 *
 */
public abstract class NotificationThread implements Runnable{
	
	/**Indictes the thread has started*/
	private static final int STARTED = 0;
	/**Indicates the thread has terminated*/
	private static final int ENDED = 1;
	
	/**A List of all ThreadListeners */
	private java.util.List<ThreadListener> listeners = Collections.synchronizedList( new ArrayList<ThreadListener>() );
	
	/**
	 * Adds a listener to this object to listen for thread start and ends.
	 * @param l The ThreadListener to add.
	 */
	public void addThreadListener( ThreadListener l ){
		listeners.add(l);
	}
	/**
	 * Removes a particular listener from this object. 
	 * @param l The ThreadListener to remove.
	 */
	public void removeThreadListener( ThreadListener l ){
		listeners.remove(l);
	}
	/**
	 * Notifies all listeners of a particular event specified in the parameters. 
	 * NOTE: This function is unsychronized, and thus any calls that 
	 * need synchronization in the listener implementation should should be made syncrhonously.
	 * @param i One of STARTED or ENDED.
	 */
	private final void notifyListeners(int i) {
		switch(i){
			case STARTED:
				for (ThreadListener listener : listeners) {
					listener.threadStarted(this);
				}	
				break;
			case ENDED:
				for (ThreadListener listener : listeners) {
					listener.threadFinished(this);
				}
				break;
		}

	}
	/**
	 * Implementation of the Runnable interface.
	 */
	public void run(){
		notifyListeners(STARTED);
		try{
			doRun();
		}finally{
			notifyListeners(ENDED);
		}
	}
	/**
	 * An abstract method to be implemented by children classes, this should contain
	 * any lengthy tasks to be performed. 
	 */
	public abstract void doRun();
}
