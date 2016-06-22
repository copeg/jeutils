package com.algosome.eutils.util;

/**
 * Interface used to interact with threads to indicate start and end progression. Users who
 * wish to listen to the start and end of a thread can implement this interface, and add the listener
 * to a NotificationThread class, during which the methods threadStarted and threadFinished methods will
 * be called at the start and end (respectively) of the thread duration.
 * @author Greg Cope
 * @see NotificationThread.
 * @see ThreadListenerAdapter
 */
public interface ThreadListener {
	
	/**
	 * Function used to indicate that the Runnable thread runner has finished.
	 * NOTE: this function is called from a thread, and any updates to a single threaded
	 * GUI such as Swing should be forwared to the Event Dispatch Thread.
	 * @param runner The thread that has finished.
	 */
	public void threadFinished(Runnable runner);
	/**
	 * Function used to indicate that the Runnable thread runner has started.
	 * NOTE: this function is called from a thread, and any updates to a single threaded
	 * GUI such as Swing should be forwared to the Event Dispatch Thread.
	 * @param runner The thread that has started.
	 */	
	public void threadStarted(Runnable runner);
}
