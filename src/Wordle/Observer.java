package Wordle;

import javafx.event.Event;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:01 PM
 */
public interface Observer {

	/**
	 * 
	 * @param event
	 */
	public void update(Event event);

}