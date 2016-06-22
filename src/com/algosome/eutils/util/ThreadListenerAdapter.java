package com.algosome.eutils.util;


/**
 * An adapter class that has an empty implementation of the ThreadListener interface.This class is
 * provided as a convenience class for thread listeners. </p>
 * <p>Extend this class and override one or more of its methods to provide implementation. This class
 * can then be added as a listener to a NotificationThread object.
 * @author Greg Cope
 * @see NotificationThread
 * @see ThreadListener
 *
 */
public abstract class ThreadListenerAdapter implements ThreadListener{
	/**
	 * Function used to indicate that the Runnable thread runner has finished.
	 * NOTE: this function is called from a thread, and any updates to a single threaded
	 * GUI such as Swing should be forwared to the Event Dispatch Thread.
	 * @param runner The thread that has finished.
	 */
	public void threadFinished(Runnable runner){}
	/**
	 * Function used to indicate that the Runnable thread runner has started.
	 * NOTE: this function is called from a thread, and any updates to a single threaded
	 * GUI such as Swing should be forwared to the Event Dispatch Thread.
	 * @param runner The thread that has started.
	 */	
	public void threadStarted(Runnable runner){}
}
