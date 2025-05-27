package com.mayhem.rs2.content.event;

import java.util.Timer;
import java.util.TimerTask;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.event.impl.*;
import com.mayhem.rs2.entity.World;

/**
 * @author Andy || ReverendDread Mar 29, 2017
 */
public class EventManager {

	//Fields
	private Event event;
	private int delay;
	private int[] eventAmount = new int[3];
	Timer timer = new Timer();
	
	/**
	 * Constructs a new EventManager object.
	 * @param player 
	 */
	public EventManager() {

	}
	
	//Methods	
	
	/**
	 * Processes task on {@link World} thread every game tick.
	 */
	public void process() {
		if (event != null) {
			if (!event.preStartupCheck()) {
				forceStop();
			}
		}
		if (delay > 0) {
			delay--;
			return;
		}
		if (event == null)
			return;
		int delay = event.process();
		if (delay == -1) {
			forceStop();
			return;
		}
		this.delay += delay;
	}
	
	/**
	 * Force stops an event safely.
	 */
	public void forceStop() {
		if (event == null)
			return;
		event.stop();
		event = null;
	}
	
	//Getters and setters
	
	/**
	 * Gets the delay.
	 * @return The delay.
	 */
	public int getEventDelay() {
		return delay;
	}
	 
	/**
	 * Adds delay the the event processing. 
	 * @param delay The event.
	 */
	public void addEventDelay(int delay) {
		this.delay += delay;
	}

	/**
	 * Sets the delay.
	 * @param delay The delay.
	 */
	public void setEventDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Sets the current event and stops the previous one.
	 * @param event {@link Event} The event.
	 * @return
	 */
	public boolean setEvent(Event event) {
		forceStop();
		if (!event.start())
			return false;
		this.event = event;
		return true;
	}
	
	/**
	 * Gets the {@link Event} event.
	 * @return the event.
	 */
	public Event getEvent() {
		return event;
	}
	
	/**
	 * Starts event boss timer.
	 */
	public void appendTimer() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {	
				switch (Utility.random(3)) {
					case 0:
						setEvent(new BossEvent());
					    eventAmount[0]++;
					    System.out.println("Exectuing Event: " + "BossEvent: #" + eventAmount[0]);
					    break;
					case 1:
						setEvent(new DoubleGold());
					    eventAmount[1]++;
					    System.out.println("Exectuing Event: " + "DoubleGold: #" + eventAmount[1]);
						break;
					/*case 2:
						setEvent(new DoubleExperience());
					    eventAmount[3]++;
					    System.out.println("Exectuing Event: " + "DoubleExperience: #" + eventAmount[2]);
						break;*/
					case 2:
						setEvent(new DoubleDropRate());
						eventAmount[2]++;
						System.out.println("Executing Event: " + "DoubleDropRate: #" + eventAmount[2]);
						break;
						
					default:
						setEvent(new BossEvent());
					    eventAmount[1]++;
					    System.out.println("Exectuing Event: " + "BossEvent: #" + eventAmount[0]);
				  }
			  }
		}, 120 * 60 * 1000, 120 * 60 * 1000); //Execute every 2 hours.
	}
	
}
