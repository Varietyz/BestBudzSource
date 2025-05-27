package com.mayhem.rs2.content.event;

import com.mayhem.rs2.entity.World;

/**
 * @author Andy || ReverendDread Mar 29, 2017
 */
public abstract class Event {

	//Called when exectuting an event.
	public abstract boolean start();
	
	//Called every tick when event is being executed. Place conditions here.
	public abstract boolean preStartupCheck();
	
	//Called every tick when event is being executed.
	public abstract int process();
	
	//Called when stopping an event.
	public abstract void stop();
	
	//Sets delay for the event processing.
	protected final void setDelay(World world, int delay) {
		World.getEventManager().setEventDelay(delay);
	}
	
	public void sendMessage(String message) {
		World.sendGlobalMessage("<img=1><col=ff0000>[EVENT]: " + message + "</col>");
	}

}
